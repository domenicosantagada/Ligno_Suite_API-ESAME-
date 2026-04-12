package uni.lignosuiteapi.service;

import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.ASPMapper;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.specializations.dlv2.desktop.DLV2DesktopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uni.lignosuiteapi.asp.model.LamaIn;
import uni.lignosuiteapi.asp.model.PezzoIn;
import uni.lignosuiteapi.asp.model.PosizionatoOut;
import uni.lignosuiteapi.asp.model.ScartoIn;
import uni.lignosuiteapi.dto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service che si occupa di gestire la logica di business per l'ottimizzazione del taglio dei pannelli.
 * Il metodo principale è ottimizzaTaglio, che accetta un PannelloRequestDTO con le specifiche del pannello
 * e dei pezzi da tagliare, e restituisce un RisultatoOttimizzazioneDTO con la disposizione ottimale dei pezzi
 * sui pannelli, gli scarti e le statistiche di efficienza.
 * Il servizio utilizza il solver ASP DLV per trovare la disposizione ottimale dei pezzi sui pannelli,
 * e include una serie di strategie di ordinamento dei pezzi per cercare di migliorare l'efficienza del taglio.
 * Il servizio è progettato per essere facilmente estendibile con nuove strategie di ordinamento o nuovi criteri
 * di posizionamento, e include un sistema di logging dettagliato per tracciare il processo decisionale e
 * facilitare il debug. Il servizio è completamente indipendente dal livello di accesso (controller) e
 * si occupa solo della logica di ottimizzazione, restituendo sempre e solo DTO come output, senza esporre
 * alcuna logica di dominio o entity al controller o al frontend.
 */
@Service
public class OttimizzazioneService {

    // Logger per tracciare il processo decisionale e facilitare il debug.
    private static final Logger logger = LoggerFactory.getLogger(OttimizzazioneService.class);

    // Costante che definisce il percorso del solver DLV.
    private static final String DLV_PATH = "lib/dlv-2.1.1-macos";

    // Costante che definisce il percorso dell'encoding ASP.
    private static final String ENCODING_PATH = "src/main/resources/taglio.dl";

    // Costruttore del servizio, in cui registriamo le classi di input e output per ASPMapper.
    public OttimizzazioneService() {
        try {
            ASPMapper.getInstance().registerClass(PezzoIn.class);
            ASPMapper.getInstance().registerClass(ScartoIn.class);
            ASPMapper.getInstance().registerClass(LamaIn.class);
            ASPMapper.getInstance().registerClass(PosizionatoOut.class);
            logger.info("Configurazione embASP completata con successo.");
        } catch (Exception e) {
            logger.error("Errore durante la registrazione delle classi ASPMapper: ", e);
        }
    }

    /**
     * Metodo che esegue l'ottimizzazione del taglio dei pannelli.
     */
    public RisultatoOttimizzazioneDTO ottimizzaTaglio(PannelloRequestDTO request) {

        // Lista che conterrà i pezzi espansi in base alla loro quantità, con ID univoci per ogni pezzo.
        List<PezzoDTO> espansi = new ArrayList<>();

        for (int idx = 0; idx < request.pezzi.size(); idx++) {

            PezzoDTO p = request.pezzi.get(idx);

            for (int q = 0; q < p.quantita; q++) {
                PezzoDTO nuovo = new PezzoDTO();
                nuovo.id = idx + "-" + q;
                nuovo.nome = p.nome != null ? p.nome : "P" + (idx + 1);
                nuovo.larghezza = p.larghezza;
                nuovo.altezza = p.altezza;
                nuovo.quantita = 1;
                nuovo.puoRuotare = p.puoRuotare;
                nuovo.indiceColore = p.indiceColore;
                espansi.add(nuovo);
            }
        }

        // Definiamo una serie di strategie di ordinamento dei pezzi, che saranno testate in sequenza per cercare di migliorare l'efficienza del taglio.
        List<Comparator<PezzoDTO>> strategieOrdinamento = Arrays.asList(
                (a, b) -> Double.compare(b.larghezza * b.altezza, a.larghezza * a.altezza),
                (a, b) -> Double.compare(Math.max(b.larghezza, b.altezza), Math.max(a.larghezza, a.altezza)),
                (a, b) -> Double.compare((b.larghezza + b.altezza), (a.larghezza + a.altezza))
        );

        // Variabile per tenere traccia del miglior risultato ottenuto tra le diverse strategie di ordinamento.
        RisultatoOttimizzazioneDTO migliorRisultato = null;

        // Per ogni strategia di ordinamento, clona la lista dei pezzi espansi, ordina i pezzi secondo la strategia, esegue l'ottimizzazione e confronta il risultato con il miglior risultato ottenuto finora.
        for (Comparator<PezzoDTO> strategia : strategieOrdinamento) {

            List<PezzoDTO> pezziCorrenti = clonaLista(espansi);

            // Applica la strategia di ordinamento alla lista dei pezzi correnti secondo la strategia definita (es. area decrescente, lato massimo decrescente, somma dei lati decrescente).
            pezziCorrenti.sort(strategia);

            // Esegue l'ottimizzazione con la lista dei pezzi ordinati secondo la strategia corrente, e ottiene un risultato di ottimizzazione.
            RisultatoOttimizzazioneDTO risultato = eseguiSingolaOttimizzazione(
                    request.pannelloLarghezza,
                    request.pannelloAltezza,
                    request.spessoreLama,
                    request.margine,
                    pezziCorrenti
            );

            // Confronta il risultato ottenuto con il miglior risultato finora, e aggiorna il miglior risultato se il nuovo risultato
            // è migliore in termini di numero di pannelli usati (meno è meglio) o, in caso di parità, in termini di efficienza (più è meglio).
            if (migliorRisultato == null ||
                    risultato.pannelli.size() < migliorRisultato.pannelli.size() ||
                    (risultato.pannelli.size() == migliorRisultato.pannelli.size() && risultato.efficienza > migliorRisultato.efficienza)) {
                migliorRisultato = risultato;
            }
        }

        // Dopo aver testato tutte le strategie di ordinamento, restituisce il miglior risultato ottenuto.
        return migliorRisultato;
    }

    /**
     * Metodo che esegue una singola ottimizzazione del taglio dei pannelli, dato un ordine specifico dei pezzi.
     */
    private RisultatoOttimizzazioneDTO eseguiSingolaOttimizzazione(double pw, double ph, double spessoreLama, double margine, List<PezzoDTO> listaPezzi) {

        // Lista che conterrà i pannelli aperti durante il processo di ottimizzazione, ognuno con i suoi pezzi posizionati, gli spazi liberi (scarti) e i pezzi non posizionabili.
        List<PannelloAperto> pannelliAperti = new ArrayList<>();

        // Handler per interagire con il solver ASP DLV, che sarà usato per trovare la disposizione ottimale dei pezzi sui pannelli.
        DesktopHandler handler = new DesktopHandler(new DLV2DesktopService(DLV_PATH));

        // Se la lista dei pezzi non è vuota, inizializza il primo pannello aperto con le dimensioni del pannello e il margine specificati, e aggiungilo alla lista dei pannelli aperti.
        if (!listaPezzi.isEmpty()) {
            pannelliAperti.add(new PannelloAperto(pw, ph, margine));
        }

        // Per ogni pezzo nella lista dei pezzi, tenta di posizionarlo su uno dei pannelli aperti usando il solver ASP.
        // - Se il pezzo non può essere posizionato su nessun pannello aperto, apri un nuovo pannello e tenta di posizionarlo anche lì.
        // - Se il pezzo non può essere posizionato nemmeno sul nuovo pannello, aggiungilo alla lista dei pezzi non posizionabili di quel pannello.
        for (PezzoDTO pezzo : listaPezzi) {
            boolean posizionato = tentaPosizionamentoConASP(pezzo, pannelliAperti, handler, spessoreLama);

            if (!posizionato) {
                PannelloAperto nuovoPannello = new PannelloAperto(pw, ph, margine);
                pannelliAperti.add(nuovoPannello);

                boolean posizionatoNuovo = tentaPosizionamentoConASP(pezzo, pannelliAperti, handler, spessoreLama);

                if (!posizionatoNuovo) {
                    pezzo.posizionato = false;
                    nuovoPannello.nonPosizionabili.add(pezzo);
                }
            }
        }

        // Dopo aver tentato di posizionare tutti i pezzi, costruisce il risultato dell'ottimizzazione, che include la lista dei pannelli con i pezzi posizionati, gli scarti e i pezzi non posizionabili, e le statistiche di efficienza.
        RisultatoOttimizzazioneDTO res = new RisultatoOttimizzazioneDTO();
        res.pannelli = new ArrayList<>();

        double areaUsata = 0;

        // Per ogni pannello aperto, se ha pezzi posizionati o scarti validi, aggiungilo al risultato dell'ottimizzazione.
        // Calcola l'area usata sommando l'area di ogni pezzo posizionato, e calcola l'efficienza come rapporto tra area usata e area totale dei pannelli usati.
        for (PannelloAperto pa : pannelliAperti) {
            if (pa.pezzi.isEmpty() && pa.nonPosizionabili.isEmpty()) continue;

            RisultatoPannelloDTO rp = new RisultatoPannelloDTO();
            rp.pannelloLarghezza = pw;
            rp.pannelloAltezza = ph;
            rp.pezzi = pa.pezzi;
            rp.scarti = pa.spaziLiberi.stream().filter(s -> s.w > 20 && s.h > 20).collect(Collectors.toList());
            rp.nonPosizionabili = pa.nonPosizionabili;
            res.pannelli.add(rp);

            for (PezzoDTO p : pa.pezzi) {
                areaUsata += (p.larghezzaTaglio * p.altezzaTaglio);
            }
        }

        // Calcola l'area totale dei pannelli usati (numero di pannelli moltiplicato per area di ciascun pannello),
        // e poi calcola l'efficienza come rapporto tra area usata e area totale dei pannelli, espresso in percentuale.
        // Calcola anche l'area di scarto come differenza tra area totale dei pannelli e area usata.
        double areaTotalePannelli = res.pannelli.size() * pw * ph;
        res.areaUsata = areaUsata;
        res.efficienza = areaTotalePannelli > 0 ? (areaUsata / areaTotalePannelli) * 100 : 0;
        res.areaScarto = areaTotalePannelli - areaUsata;

        // Log finale con le statistiche di efficienza e il numero di pannelli usati per questa strategia di ordinamento.
        return res;
    }

    /**
     * Metodo che tenta di posizionare un pezzo su uno dei pannelli aperti usando il solver ASP DLV.
     */
    private boolean tentaPosizionamentoConASP(PezzoDTO pezzo, List<PannelloAperto> pannelliAperti, DesktopHandler handler, double spessoreLama) {

        // Costruiamo il programma di input per ASP, che include le dimensioni del pezzo, lo spessore della lama e gli scarti disponibili sui pannelli aperti.
        ASPInputProgram inputProgram = new ASPInputProgram();
        inputProgram.addFilesPath(ENCODING_PATH);

        try {
            // DLV richiede input in numeri interi quindi arrottondiamo sempre per eccesso le dimensioni dei pezzi e degli scarti.
            int pezzoW = (int) Math.ceil(pezzo.larghezza);
            int pezzoH = (int) Math.ceil(pezzo.altezza);

            // --- LOG 1: Info sul Pezzo ---
            logger.info("\n-------------------------------------------------------------");
            logger.info(">>> INPUT PEZZO: [ID: {} | Nome: '{}' | Dim: {}x{} | Ruotabile: {}]",
                    pezzo.id, pezzo.nome, pezzoW, pezzoH, pezzo.puoRuotare);

            // Inviamo il pezzo e lo spessore lama come input al solver ASP, usando le classi PezzoIn e LamaIn per rappresentarli.
            inputProgram.addObjectInput(new PezzoIn(pezzo.id, pezzoW, pezzoH, pezzo.puoRuotare ? 1 : 0));
            inputProgram.addObjectInput(new LamaIn((int) Math.ceil(spessoreLama)));

            int scartiValidiInviati = 0;
            double latoMinimoPezzo = Math.min(pezzoW, pezzoH);

            // Per tenere traccia degli scarti validi inviati al solver ASP, creiamo una lista di stringhe che conterrà le rappresentazioni testuali degli scarti
            // (ID e dimensioni) che vengono inviati come input al solver. Questo ci permetterà di loggare in modo dettagliato quali scarti sono stati considerati per il posizionamento del pezzo.
            List<String> logScarti = new ArrayList<>();

            for (int pIdx = 0; pIdx < pannelliAperti.size(); pIdx++) {
                PannelloAperto pannello = pannelliAperti.get(pIdx);
                for (int sIdx = 0; sIdx < pannello.spaziLiberi.size(); sIdx++) {
                    ScartoDTO scarto = pannello.spaziLiberi.get(sIdx);
                    // Inviamo lo scarto solo se può fisicamente contenere il pezzo
                    if (scarto.w >= latoMinimoPezzo && scarto.h >= latoMinimoPezzo) {
                        String idScarto = pIdx + "-" + sIdx;

                        inputProgram.addObjectInput(new ScartoIn(idScarto, (int) scarto.w, (int) scarto.h));

                        logScarti.add(String.format("[%s: %dx%d]", idScarto, (int) scarto.w, (int) scarto.h));
                        scartiValidiInviati++;
                    }
                }
            }

            // Se non abbiamo inviato nessuno scarto valido al solver ASP, significa che il pezzo non può essere posizionato su nessuno dei pannelli aperti,
            // quindi logghiamo questa informazione e ritorniamo false per indicare che il pezzo non è stato posizionato.
            if (scartiValidiInviati == 0) {
                logger.info("--- NESSUNO SCARTO COMPATIBILE TROVATO ---");
                return false;
            }

            // --- LOG 2: Info sugli Scarti ---
            logger.info(">>> INPUT SCARTI: Passati ad ASP {} scarti validi: {}", scartiValidiInviati, String.join(", ", logScarti));

            // Inviamo il programma di input al solver ASP e otteniamo l'output, che conterrà la mossa migliore trovata da ASP per posizionare il pezzo su uno degli scarti disponibili.
            handler.addProgram(inputProgram);
            Output output = handler.startSync();

            // Controlliamo se ci sono errori nell'output del solver ASP.
            String erroriDLV = output.getErrors();
            if (erroriDLV != null && !erroriDLV.trim().isEmpty()) {
                logger.error("ERRORE DI SINTASSI DLV: \n" + erroriDLV);
            }

            // Otteniamo l'output testuale puro di ASP, che conterrà i predicati che rappresentano la mossa migliore trovata da ASP (es. scelto("id-scarto", rotazione)).
            String outputTestuale = output.getOutput();

            // --- LOG 3: Output puro di ASP ---
            String cleanOutput = (outputTestuale != null) ? outputTestuale.replace("\n", " ").trim() : "NULL";
            logger.info("<<< OUTPUT ASP: {}", cleanOutput);

            // Estraiamo la mossa migliore dall'output testuale di ASP, cercando il predicato scelto("id-scarto", rotazione) che indica su quale scarto posizionare il pezzo e se ruotarlo o meno.
            PosizionatoOut mossaMigliore = estraiMossaMigliore(outputTestuale);

            // --- LOG 4: Mossa interpretata ---
            if (mossaMigliore != null) {
                logger.info("=== DECISIONE: Inserito nello scarto '{}' - Ruotato: {}", mossaMigliore.getIdScarto(), (mossaMigliore.getRotazione() == 1));
                applicaMossaFisica(mossaMigliore, pezzo, pannelliAperti, spessoreLama);
                return true;
            } else {
                logger.info("=== DECISIONE: ASP non ha trovato nessun incastro valido (Pezzo scartato).");
            }

        } catch (Exception e) {
            logger.error("Errore durante l'esecuzione del solver ASP", e);
        } finally {
            // Rimuoviamo tutti i programmi dall'handler per pulire lo stato prima del prossimo pezzo.
            handler.removeAll();
        }
        return false;
    }

    /**
     * Metodo che applica la mossa migliore trovata da ASP al modello fisico dei pannelli e dei pezzi, aggiornando le posizioni dei pezzi, gli scarti disponibili e i pezzi non posizionabili sui pannelli.
     */
    private void applicaMossaFisica(PosizionatoOut mossa, PezzoDTO pezzo, List<PannelloAperto> pannelliAperti, double spessoreLama) {

        // La mossa contiene l'id dello scarto su cui posizionare il pezzo, che è nel formato "pIdx-sIdx" dove pIdx è l'indice del
        // pannello nella lista dei pannelli aperti e sIdx è l'indice dello scarto nella lista degli scarti di quel pannello.
        String[] indici = mossa.getIdScarto().split("-");

        int pIdx = Integer.parseInt(indici[0]);
        int sIdx = Integer.parseInt(indici[1]);

        // Otteniamo il pannello migliore su cui posizionare il pezzo, e lo scarto migliore su cui posizionare il pezzo, che contiene le coordinate (x,y) e le dimensioni (w,h) dello spazio libero su quel pannello.
        PannelloAperto pannelloMigliore = pannelliAperti.get(pIdx);
        ScartoDTO migliorSpazio = pannelloMigliore.spaziLiberi.get(sIdx);

        boolean rotazioneMigliore = mossa.isRuotato();

        // Calcoliamo le dimensioni finali del pezzo da posizionare, tenendo conto della rotazione decisa da ASP. Se il pezzo è ruotato, invertiamo larghezza e altezza.
        double larghezzaT = rotazioneMigliore ? pezzo.altezza : pezzo.larghezza;
        double altezzaT = rotazioneMigliore ? pezzo.larghezza : pezzo.altezza;

        pezzo.x = migliorSpazio.x;
        pezzo.y = migliorSpazio.y;
        pezzo.larghezzaTaglio = larghezzaT;
        pezzo.altezzaTaglio = altezzaT;
        pezzo.ruotato = rotazioneMigliore;
        pezzo.posizionato = true;

        pannelloMigliore.pezzi.add(pezzo);

        // Dopo aver posizionato il pezzo sul pannello, dobbiamo aggiornare gli spazi liberi (scarti) di quel pannello,
        // rimuovendo lo scarto su cui abbiamo posizionato il pezzo e aggiungendo i nuovi scarti creati dagli spazi residui a destra e sopra il pezzo posizionato.
        double wDestra = migliorSpazio.w - larghezzaT - spessoreLama;
        double hSopra = migliorSpazio.h - altezzaT - spessoreLama;

        pannelloMigliore.spaziLiberi.remove(sIdx);

        if (wDestra >= hSopra) {
            if (wDestra > 0)
                pannelloMigliore.spaziLiberi.add(new ScartoDTO(migliorSpazio.x + larghezzaT + spessoreLama, migliorSpazio.y, wDestra, migliorSpazio.h));
            if (hSopra > 0)
                pannelloMigliore.spaziLiberi.add(new ScartoDTO(migliorSpazio.x, migliorSpazio.y + altezzaT + spessoreLama, larghezzaT, hSopra));
        } else {
            if (wDestra > 0)
                pannelloMigliore.spaziLiberi.add(new ScartoDTO(migliorSpazio.x + larghezzaT + spessoreLama, migliorSpazio.y, wDestra, altezzaT));
            if (hSopra > 0)
                pannelloMigliore.spaziLiberi.add(new ScartoDTO(migliorSpazio.x, migliorSpazio.y + altezzaT + spessoreLama, migliorSpazio.w, hSopra));
        }
    }

    /**
     * Metodo che estrae la mossa migliore dall'output testuale di ASP, cercando il predicato scelto("id-scarto", rotazione) che indica su quale scarto posizionare il pezzo e se ruotarlo o meno.
     */
    private PosizionatoOut estraiMossaMigliore(String rawText) {

        // Se l'output testuale è null o vuoto, ritorniamo null per indicare che non è stata trovata nessuna mossa valida.
        if (rawText == null || rawText.trim().isEmpty()) {
            return null;
        }

        // Usiamo una regex per cercare il predicato scelto("id-scarto", rotazione) nell'output testuale di ASP,
        // e estraiamo l'id dello scarto e la rotazione (0 o 1) dalla mossa migliore trovata da ASP.
        Pattern pattern = Pattern.compile("scelto\\(\"?([^\",\\)]+)\"?,\\s*([01])\\)");
        Matcher matcher = pattern.matcher(rawText);

        PosizionatoOut ultimaMossa = null;

        // Se troviamo una mossa valida, creiamo un oggetto PosizionatoOut con l'id dello scarto e la rotazione estratti dalla mossa, e lo ritorniamo.
        while (matcher.find()) {
            ultimaMossa = new PosizionatoOut();
            ultimaMossa.setIdScarto(matcher.group(1).replace("\"", ""));
            ultimaMossa.setRotazione(Integer.parseInt(matcher.group(2)));
        }

        return ultimaMossa;
    }

    /**
     * Metodo di utility che clona una lista di PezzoDTO, creando nuovi oggetti PezzoDTO con gli stessi valori dei campi, ma con ID univoci per ogni pezzo.
     */
    private List<PezzoDTO> clonaLista(List<PezzoDTO> originali) {

        // Questo metodo è utile per creare nuove liste di pezzi con lo stesso ordine ma con oggetti distinti
        // in modo da poter testare diverse strategie di ordinamento senza modificare gli oggetti originali.
        return originali.stream().map(p -> {
            PezzoDTO c = new PezzoDTO();
            c.id = p.id;
            c.nome = p.nome;
            c.larghezza = p.larghezza;
            c.altezza = p.altezza;
            c.quantita = p.quantita;
            c.puoRuotare = p.puoRuotare;
            c.indiceColore = p.indiceColore;
            return c;
        }).collect(Collectors.toList());
    }

    /**
     * Classe interna che rappresenta un pannello aperto durante il processo di ottimizzazione, con i suoi pezzi posizionati, gli spazi liberi (scarti) e i pezzi non posizionabili.
     */
    private static class PannelloAperto {
        double pannelloLarghezza;
        double pannelloAltezza;
        List<PezzoDTO> pezzi = new ArrayList<>();
        List<ScartoDTO> spaziLiberi = new ArrayList<>();
        List<PezzoDTO> nonPosizionabili = new ArrayList<>();

        public PannelloAperto(double w, double h, double margine) {
            this.pannelloLarghezza = w;
            this.pannelloAltezza = h;
            this.spaziLiberi.add(new ScartoDTO(margine, margine, w - 2 * margine, h - 2 * margine));
        }
    }
}

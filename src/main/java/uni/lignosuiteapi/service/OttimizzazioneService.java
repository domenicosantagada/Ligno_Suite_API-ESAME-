package uni.lignosuiteapi.service;

import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.ASPMapper;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
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
import java.util.stream.Collectors;

@Service
public class OttimizzazioneService {

    // Aggiunto il Logger di Spring Boot per rimuovere il warning del printStackTrace
    private static final Logger logger = LoggerFactory.getLogger(OttimizzazioneService.class);

    // MODIFICA QUI IL PERCORSO SE USI WINDOWS (es. "lib/dlv2.exe") O LINUX
    private static final String DLV_PATH = "lib/dlv-2.1.1-macos";
    private static final String ENCODING_PATH = "src/main/resources/taglio.dl";

    public OttimizzazioneService() {
        // Registrazione delle classi per embASP al momento dell'avvio del servizio
        try {
            ASPMapper.getInstance().registerClass(PezzoIn.class);
            ASPMapper.getInstance().registerClass(ScartoIn.class);
            ASPMapper.getInstance().registerClass(LamaIn.class);
            ASPMapper.getInstance().registerClass(PosizionatoOut.class);
            logger.info("Configurazione embASP completata con successo.");
        } catch (Exception e) {
            // Gestione corretta delle eccezioni di Reflection sollevate da embASP
            logger.error("Errore durante la registrazione delle classi ASPMapper: ", e);
        }
    }

    public RisultatoOttimizzazioneDTO ottimizzaTaglio(PannelloRequestDTO request) {
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

        List<Comparator<PezzoDTO>> strategieOrdinamento = Arrays.asList(
                (a, b) -> Double.compare(b.larghezza * b.altezza, a.larghezza * a.altezza),
                (a, b) -> Double.compare(Math.max(b.larghezza, b.altezza), Math.max(a.larghezza, a.altezza)),
                (a, b) -> Double.compare((b.larghezza + b.altezza), (a.larghezza + a.altezza))
        );

        RisultatoOttimizzazioneDTO migliorRisultato = null;

        for (Comparator<PezzoDTO> strategia : strategieOrdinamento) {
            List<PezzoDTO> pezziCorrenti = clonaLista(espansi);
            pezziCorrenti.sort(strategia);

            RisultatoOttimizzazioneDTO risultato = eseguiSingolaOttimizzazione(
                    request.pannelloLarghezza,
                    request.pannelloAltezza,
                    request.spessoreLama,
                    request.margine,
                    pezziCorrenti
            );

            if (migliorRisultato == null ||
                    risultato.pannelli.size() < migliorRisultato.pannelli.size() ||
                    (risultato.pannelli.size() == migliorRisultato.pannelli.size() && risultato.efficienza > migliorRisultato.efficienza)) {
                migliorRisultato = risultato;
            }
        }

        return migliorRisultato;
    }

    private RisultatoOttimizzazioneDTO eseguiSingolaOttimizzazione(double pw, double ph, double spessoreLama, double margine, List<PezzoDTO> listaPezzi) {
        List<PannelloAperto> pannelliAperti = new ArrayList<>();
        DesktopHandler handler = new DesktopHandler(new DLV2DesktopService(DLV_PATH));

        if (!listaPezzi.isEmpty()) {
            pannelliAperti.add(new PannelloAperto(pw, ph, margine));
        }

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

        RisultatoOttimizzazioneDTO res = new RisultatoOttimizzazioneDTO();
        res.pannelli = new ArrayList<>();
        double areaUsata = 0;

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

        double areaTotalePannelli = res.pannelli.size() * pw * ph;
        res.areaUsata = areaUsata;
        res.efficienza = areaTotalePannelli > 0 ? (areaUsata / areaTotalePannelli) * 100 : 0;
        res.areaScarto = areaTotalePannelli - areaUsata;

        return res;
    }

    private boolean tentaPosizionamentoConASP(PezzoDTO pezzo, List<PannelloAperto> pannelliAperti, DesktopHandler handler, double spessoreLama) {
        ASPInputProgram inputProgram = new ASPInputProgram();
        inputProgram.addFilesPath(ENCODING_PATH);

        try {
            inputProgram.addObjectInput(new PezzoIn(pezzo.id, (int) pezzo.larghezza, (int) pezzo.altezza, pezzo.puoRuotare ? 1 : 0));
            inputProgram.addObjectInput(new LamaIn((int) spessoreLama));

            int scartiValidiInviati = 0;
            double latoMinimoPezzo = Math.min(pezzo.larghezza, pezzo.altezza);

            for (int pIdx = 0; pIdx < pannelliAperti.size(); pIdx++) {
                PannelloAperto pannello = pannelliAperti.get(pIdx);
                for (int sIdx = 0; sIdx < pannello.spaziLiberi.size(); sIdx++) {
                    ScartoDTO scarto = pannello.spaziLiberi.get(sIdx);
                    if (scarto.w >= latoMinimoPezzo && scarto.h >= latoMinimoPezzo) {
                        // FIX: Aggiunto cast (int) per rispettare la firma del costruttore ScartoIn
                        inputProgram.addObjectInput(new ScartoIn(pIdx + "-" + sIdx, (int) scarto.w, (int) scarto.h));
                        scartiValidiInviati++;
                    }
                }
            }

            if (scartiValidiInviati == 0) return false;

            handler.addProgram(inputProgram);

            AnswerSets answerSets = (AnswerSets) handler.startSync();
            PosizionatoOut mossaMigliore = estraiMossaMigliore(answerSets);

            if (mossaMigliore != null) {
                applicaMossaFisica(mossaMigliore, pezzo, pannelliAperti, spessoreLama);
                return true;
            }

        } catch (Exception e) {
            // FIX: Sostituito printStackTrace con logger
            logger.error("Errore durante l'esecuzione del solver ASP", e);
        } finally {
            handler.removeAll();
        }
        return false;
    }

    private void applicaMossaFisica(PosizionatoOut mossa, PezzoDTO pezzo, List<PannelloAperto> pannelliAperti, double spessoreLama) {
        String[] indici = mossa.getIdScarto().split("-");
        int pIdx = Integer.parseInt(indici[0]);
        int sIdx = Integer.parseInt(indici[1]);

        PannelloAperto pannelloMigliore = pannelliAperti.get(pIdx);
        ScartoDTO migliorSpazio = pannelloMigliore.spaziLiberi.get(sIdx);

        boolean rotazioneMigliore = mossa.isRuotato();
        double larghezzaT = rotazioneMigliore ? pezzo.altezza : pezzo.larghezza;
        double altezzaT = rotazioneMigliore ? pezzo.larghezza : pezzo.altezza;

        pezzo.x = migliorSpazio.x;
        pezzo.y = migliorSpazio.y;
        pezzo.larghezzaTaglio = larghezzaT;
        pezzo.altezzaTaglio = altezzaT;
        pezzo.ruotato = rotazioneMigliore;
        pezzo.posizionato = true;

        pannelloMigliore.pezzi.add(pezzo);

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

    private PosizionatoOut estraiMossaMigliore(AnswerSets answerSets) throws Exception {
        if (answerSets.getAnswersets().isEmpty()) return null;

        // FIX: Sostituito .get(size - 1) con il più moderno .getLast() suggerito dall'IDE
        AnswerSet bestAnswerSet = answerSets.getAnswersets().getLast();

        for (Object obj : bestAnswerSet.getAtoms()) {
            if (obj instanceof PosizionatoOut) {
                return (PosizionatoOut) obj;
            }
        }
        return null;
    }

    private List<PezzoDTO> clonaLista(List<PezzoDTO> originali) {
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

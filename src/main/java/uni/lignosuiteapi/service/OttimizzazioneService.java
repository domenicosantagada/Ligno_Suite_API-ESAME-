package uni.lignosuiteapi.service;

import org.springframework.stereotype.Service;
import uni.lignosuiteapi.dto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OttimizzazioneService {

    public RisultatoOttimizzazioneDTO ottimizzaTaglio(PannelloRequestDTO request) {
        List<PezzoDTO> espansi = new ArrayList<>();

        // 1. Espande i pezzi in base alla quantità
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

        // 2. Strategie di ordinamento (Esattamente come in Angular)
        List<Comparator<PezzoDTO>> strategieOrdinamento = Arrays.asList(
                // Area decrescente
                (a, b) -> Double.compare(b.larghezza * b.altezza, a.larghezza * a.altezza),
                // Lato più lungo
                (a, b) -> Double.compare(Math.max(b.larghezza, b.altezza), Math.max(a.larghezza, a.altezza)),
                // Perimetro decrescente
                (a, b) -> Double.compare((b.larghezza + b.altezza), (a.larghezza + a.altezza))
        );

        RisultatoOttimizzazioneDTO migliorRisultato = null;

        // 3. Testiamo le 3 euristiche in parallelo e teniamo la migliore
        for (Comparator<PezzoDTO> strategia : strategieOrdinamento) {
            // Dobbiamo clonare la lista perché l'algoritmo modifica lo stato (x, y, posizionato)
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

    // Utility per la clonazione profonda dei pezzi
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

    // =========================================================================
    // MOTORE MULTI-PANNELLO E SPLIT DINAMICO (Esatto porting da TypeScript)
    // =========================================================================

    private RisultatoOttimizzazioneDTO eseguiSingolaOttimizzazione(double pw, double ph, double spessoreLama, double margine, List<PezzoDTO> listaPezzi) {

        List<PannelloAperto> pannelliAperti = new ArrayList<>();

        if (!listaPezzi.isEmpty()) {
            pannelliAperti.add(new PannelloAperto(pw, ph, margine));
        }

        for (PezzoDTO pezzo : listaPezzi) {
            ScartoDTO migliorSpazio = null;
            PannelloAperto pannelloMigliore = null;
            boolean rotazioneMigliore = false;
            double migliorAreaResidua = Double.MAX_VALUE;
            double migliorLatoCorto = Double.MAX_VALUE;
            int indiceSpazioMigliore = -1;

            // 1. ESPLORAZIONE GLOBALE (Tightest Fit su tutti i pannelli)
            for (PannelloAperto pannello : pannelliAperti) {
                for (int i = 0; i < pannello.spaziLiberi.size(); i++) {
                    ScartoDTO spazio = pannello.spaziLiberi.get(i);

                    // Prova dritto
                    if (pezzo.larghezza <= spazio.w && pezzo.altezza <= spazio.h) {
                        double area = (spazio.w * spazio.h) - (pezzo.larghezza * pezzo.altezza);
                        double latoCorto = Math.min(spazio.w - pezzo.larghezza, spazio.h - pezzo.altezza);

                        if (migliorSpazio == null || area < migliorAreaResidua || (area == migliorAreaResidua && latoCorto < migliorLatoCorto)) {
                            migliorAreaResidua = area;
                            migliorLatoCorto = latoCorto;
                            migliorSpazio = spazio;
                            pannelloMigliore = pannello;
                            rotazioneMigliore = false;
                            indiceSpazioMigliore = i;
                        }
                    }

                    // Prova ruotato
                    if (pezzo.puoRuotare && pezzo.altezza <= spazio.w && pezzo.larghezza <= spazio.h) {
                        double area = (spazio.w * spazio.h) - (pezzo.altezza * pezzo.larghezza);
                        double latoCorto = Math.min(spazio.w - pezzo.altezza, spazio.h - pezzo.larghezza);

                        if (migliorSpazio == null || area < migliorAreaResidua || (area == migliorAreaResidua && latoCorto < migliorLatoCorto)) {
                            migliorAreaResidua = area;
                            migliorLatoCorto = latoCorto;
                            migliorSpazio = spazio;
                            pannelloMigliore = pannello;
                            rotazioneMigliore = true;
                            indiceSpazioMigliore = i;
                        }
                    }
                }
            }

            // 2. SE NON ENTRA, APRIAMO UN NUOVO PANNELLO
            if (migliorSpazio == null) {
                PannelloAperto nuovoPannello = new PannelloAperto(pw, ph, margine);
                pannelliAperti.add(nuovoPannello);
                ScartoDTO spazio = nuovoPannello.spaziLiberi.get(0);

                boolean entraDritto = pezzo.larghezza <= spazio.w && pezzo.altezza <= spazio.h;
                boolean entraRuotato = pezzo.puoRuotare && pezzo.altezza <= spazio.w && pezzo.larghezza <= spazio.h;
                if (!entraDritto && !entraRuotato) {
                    pezzo.posizionato = false;
                    nuovoPannello.nonPosizionabili.add(pezzo);
                    continue; // Salta al prossimo pezzo
                }

                pannelloMigliore = nuovoPannello;
                migliorSpazio = spazio;
                indiceSpazioMigliore = 0;

                // Calcolo orientamento ideale nel nuovo pannello vuoto
                if (entraDritto && entraRuotato) {
                    double areaD = (spazio.w * spazio.h) - (pezzo.larghezza * pezzo.altezza);
                    double lcD = Math.min(spazio.w - pezzo.larghezza, spazio.h - pezzo.altezza);
                    double areaR = (spazio.w * spazio.h) - (pezzo.altezza * pezzo.larghezza);
                    double lcR = Math.min(spazio.w - pezzo.altezza, spazio.h - pezzo.larghezza);
                    rotazioneMigliore = (areaR < areaD || (areaR == areaD && lcR < lcD));
                } else {
                    rotazioneMigliore = !entraDritto;
                }
            }

            // 3. ESECUZIONE TAGLIO
            double larghezzaT = rotazioneMigliore ? pezzo.altezza : pezzo.larghezza;
            double altezzaT = rotazioneMigliore ? pezzo.larghezza : pezzo.altezza;

            pezzo.x = migliorSpazio.x;
            pezzo.y = migliorSpazio.y;
            pezzo.larghezzaTaglio = larghezzaT;
            pezzo.altezzaTaglio = altezzaT;
            pezzo.ruotato = rotazioneMigliore;
            pezzo.posizionato = true;

            pannelloMigliore.pezzi.add(pezzo);

            // --- SPLIT DINAMICO (Shorter Axis Rule) ---
            double wDestra = migliorSpazio.w - larghezzaT - spessoreLama;
            double hSopra = migliorSpazio.h - altezzaT - spessoreLama;

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

            pannelloMigliore.spaziLiberi.remove(indiceSpazioMigliore);
        }

        // 4. FORMATTAZIONE DEL RISULTATO FINALE PER IL FRONTEND
        RisultatoOttimizzazioneDTO res = new RisultatoOttimizzazioneDTO();
        res.pannelli = new ArrayList<>();
        double areaUsata = 0;

        for (PannelloAperto pa : pannelliAperti) {
            if (pa.pezzi.isEmpty() && pa.nonPosizionabili.isEmpty()) continue;

            RisultatoPannelloDTO rp = new RisultatoPannelloDTO();
            rp.pannelloLarghezza = pw;
            rp.pannelloAltezza = ph;
            rp.pezzi = pa.pezzi;

            // Pulizia degli scarti microscopici per non "sporcare" la UI
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

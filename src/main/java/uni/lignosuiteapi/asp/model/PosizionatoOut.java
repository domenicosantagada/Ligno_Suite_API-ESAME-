package uni.lignosuiteapi.asp.model;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import lombok.Data;
import lombok.NoArgsConstructor;

// Classe che rappresenta l'output del pezzo posizionato, con i suoi parametri: idScarto e rotazione (0 se dritto, 1 se ruotato).
// idScarto è l'identificativo dello scarto su cui è posizionato il pezzo, e rotazione indica se il pezzo è stato ruotato o meno rispetto alla sua posizione originale.

@Data
@NoArgsConstructor
@Id("posizionato")
public class PosizionatoOut {
    @Param(0)
    private String idScarto;
    @Param(1)
    private int rotazione; // 0: posizione originale 1: ruotato

    // Un metodo di utility (ignorato da embASP ma comodo per Java)
    public boolean isRuotato() {
        return rotazione == 1;
    }
}

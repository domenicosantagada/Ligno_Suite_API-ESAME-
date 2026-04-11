package uni.lignosuiteapi.asp.model;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Id("posizionato")
public class PosizionatoOut {
    @Param(0)
    private String idScarto;
    @Param(1)
    private int rotazione; // 0 se dritto, 1 se ruotato

    // Un metodo di utility (ignorato da embASP ma comodo per Java)
    public boolean isRuotato() {
        return rotazione == 1;
    }
}

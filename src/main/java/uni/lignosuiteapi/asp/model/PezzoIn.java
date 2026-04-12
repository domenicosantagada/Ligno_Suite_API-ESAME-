package uni.lignosuiteapi.asp.model;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Classe che rappresenta l'input per un pezzo da tagliare, con i suoi parametri: id, larghezza, altezza e se può essere ruotato o meno.

@Data
@NoArgsConstructor
@AllArgsConstructor
@Id("pezzo")
public class PezzoIn {
    @Param(0)
    private String id;
    @Param(1)
    private int w;
    @Param(2)
    private int h;
    @Param(3)
    private int puoRuotare; // 0 per false, 1 per true
}

package uni.lignosuiteapi.asp.model;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// Classe che rappresenta l'input per uno scarto, con i suoi parametri: id, larghezza e altezza.

@Data
@NoArgsConstructor
@AllArgsConstructor
@Id("scarto_libero")
public class ScartoIn {
    @Param(0)
    private String id;
    @Param(1)
    private int w; // larghezza dello scarto, che è la dimensione orizzontale disponibile per posizionare i pezzi
    @Param(2)
    private int h; // altezza dello scarto, che è la dimensione verticale disponibile per posizionare i pezzi
}

package uni.lignosuiteapi.asp.model;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Classe che rappresenta l'input per la lama, con un solo parametro: lo spessore.

@Data
@NoArgsConstructor
@AllArgsConstructor
@Id("lama")
public class LamaIn {
    @Param(0)
    private int spessore;
}

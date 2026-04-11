package uni.lignosuiteapi.asp.model;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Id("lama")
public class LamaIn {
    @Param(0)
    private int spessore;
}

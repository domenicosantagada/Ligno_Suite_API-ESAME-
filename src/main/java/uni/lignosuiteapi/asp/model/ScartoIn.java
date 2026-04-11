package uni.lignosuiteapi.asp.model;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Id("scarto_libero")
public class ScartoIn {
    @Param(0)
    private String id;
    @Param(1)
    private int w;
    @Param(2)
    private int h;
}

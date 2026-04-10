package uni.lignosuiteapi.dto;

public class PezzoDTO {
    public String id;
    public String nome;
    public double larghezza;
    public double altezza;
    public int quantita;
    public boolean puoRuotare;
    public Integer indiceColore;

    // Campi calcolati dal server
    public Double x;
    public Double y;
    public Double larghezzaTaglio;
    public Double altezzaTaglio;
    public Boolean ruotato;
    public Boolean posizionato;
}

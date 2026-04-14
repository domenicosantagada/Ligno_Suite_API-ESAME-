package uni.lignosuiteapi.dto;

/**
 * DTO per rappresentare un pezzo da tagliare.
 * Contiene sia i dati di input forniti dal client che i campi calcolati dal server dopo il processo di ottimizzazione del taglio.
 */
public class PezzoDTO {

    // Campi forniti dal client
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

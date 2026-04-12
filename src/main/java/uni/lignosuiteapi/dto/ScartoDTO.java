package uni.lignosuiteapi.dto;

/**
 * DTO per rappresentare lo scarto di un pannello dopo l'ottimizzazione del taglio.
 */
public class ScartoDTO {
    public double x, y, w, h;

    public ScartoDTO(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
}

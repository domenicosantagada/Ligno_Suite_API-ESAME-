package uni.lignosuiteapi.dto;

/**
 * DTO per rappresentare lo scarto di un pannello dopo l'ottimizzazione del taglio.
 */
public class ScartoDTO {
    public double x, y, w, h;

    public ScartoDTO(double x, double y, double w, double h) {
        this.x = x; // Coordinate X del punto in alto a sinistra dello scarto
        this.y = y; // Coordinate Y del punto in alto a sinistra dello scarto
        this.w = w; // Larghezza dello scarto
        this.h = h; // Altezza dello scarto
    }
}

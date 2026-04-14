package uni.lignosuiteapi.dto;

import java.util.List;

/**
 * DTO per rappresentare i dati necessari per calcolare il taglio di un pannello.
 */
public class PannelloRequestDTO {
    public double pannelloLarghezza;
    public double pannelloAltezza;
    public double spessoreLama;
    public double margine;
    public List<PezzoDTO> pezzi;
}

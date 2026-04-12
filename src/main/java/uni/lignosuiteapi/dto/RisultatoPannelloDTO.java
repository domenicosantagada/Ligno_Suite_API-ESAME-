package uni.lignosuiteapi.dto;

import java.util.List;

/**
 * DTO per rappresentare il risultato del taglio di un pannello.
 */
public class RisultatoPannelloDTO {
    public double pannelloLarghezza;
    public double pannelloAltezza;
    public List<PezzoDTO> pezzi;
    public List<ScartoDTO> scarti;
    public List<PezzoDTO> nonPosizionabili;
}

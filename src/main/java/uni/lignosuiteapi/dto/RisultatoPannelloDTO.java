package uni.lignosuiteapi.dto;

import java.util.List;

public class RisultatoPannelloDTO {
    public double pannelloLarghezza;
    public double pannelloAltezza;
    public List<PezzoDTO> pezzi;
    public List<ScartoDTO> scarti;
    public List<PezzoDTO> nonPosizionabili;
}

package uni.lignosuiteapi.dto;

import java.util.List;

/**
 * DTO per rappresentare il risultato dell'ottimizzazione del taglio dei pannelli.
 */
public class RisultatoOttimizzazioneDTO {
    public List<RisultatoPannelloDTO> pannelli;
    public double efficienza;
    public double areaUsata;
    public double areaScarto;
}

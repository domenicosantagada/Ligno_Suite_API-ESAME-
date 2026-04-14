package uni.lignosuiteapi.dto;

import java.time.LocalDate;

/**
 * DTO per rappresentare un articolo.
 */
public class ArticoloDTO {
    public Long id;
    public String nome;
    public String descrizione;
    public Double prezzoAcquisto;
    public String fornitore;
    public String unitaMisura;
    public LocalDate dataAcquisto;
}

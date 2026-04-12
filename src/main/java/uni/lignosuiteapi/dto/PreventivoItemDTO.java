package uni.lignosuiteapi.dto;

/**
 * DTO per rappresentare un item di un preventivo.
 */
public class PreventivoItemDTO {
    public Long id;
    public String description;
    public Integer quantity;
    public String unitaMisura;
    public Double rate;
    public Double amount;
}

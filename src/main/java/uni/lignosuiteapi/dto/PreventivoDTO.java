package uni.lignosuiteapi.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO per rappresentare un preventivo.
 */
public class PreventivoDTO {
    public Long id;
    public LocalDate date;
    public Long invoiceNumber;
    public String fromName;
    public String fromPiva;
    public String fromEmail;
    public String fromLogo;

    public String toName;
    public String toPiva;
    public String toEmail;
    public Double subtotal;
    public Double taxRate;
    public Double taxAmount;
    public Double discount;
    public Double total;

    // Lista delle righe del preventivo, ognuna rappresentata da un PreventivoItemDTO
    public List<PreventivoItemDTO> items;
}

package uni.lignosuiteapi.dto;

import java.time.LocalDate;
import java.util.List;

public class PreventivoDTO {
    public Long id;
    public LocalDate date;
    public Long invoiceNumber;
    public String fromName;
    public String fromPiva;
    public String fromEmail;
    public String toName;
    public String toPiva;
    public String toEmail;
    public Double subtotal;
    public Double taxRate;
    public Double taxAmount;
    public Double discount;
    public Double total;

    // Lista delle righe (usando il nostro DTO sicuro)
    public List<PreventivoItemDTO> items;
}

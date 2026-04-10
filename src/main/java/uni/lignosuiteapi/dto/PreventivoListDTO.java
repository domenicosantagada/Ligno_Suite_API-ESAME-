package uni.lignosuiteapi.dto;

import java.time.LocalDate;

public class PreventivoListDTO {
    public Long id;
    public LocalDate date;
    public String invoiceNumber;
    public String toName; // Il nome del cliente a cui è intestato
    public Double total;
}

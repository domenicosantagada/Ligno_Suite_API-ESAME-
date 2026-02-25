package uni.lignosuiteapi.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity // <-- QUESTO E' IL PEZZO CHE MANCA O NON E' RICONOSCIUTO
@Data
public class Preventivo {
    @Id
    private Long invoiceNumber; // Collegato a invoiceNumber in Angular
    private Long utenteId;
    private String date;

    private String fromName;
    private String fromEmail;
    private String fromPiva;

    private String toName;
    private String toEmail;
    private String toPiva;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PreventivoItem> items; // Le righe del preventivo

    private Double taxRate;
    private Double subtotal;
    private Double taxAmount;
    private Double total;
}

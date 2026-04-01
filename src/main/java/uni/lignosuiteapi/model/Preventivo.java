package uni.lignosuiteapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "preventivo")
public class Preventivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long invoiceNumber;

    // === Riferimento all'utente ===
    // Sostituisce "private Long utenteId;"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    private String date;

    private String fromName;
    private String fromEmail;
    private String fromPiva;

    private String toName;
    private String toEmail;
    private String toPiva;

    // === MAGIA HIBERNATE: Relazione con gli Items ===
    // cascade = CascadeType.ALL: se salvo/cancello il preventivo, salva/cancella anche gli item.
    // orphanRemoval = true: se tolgo un item dalla lista in Java, lo cancella dal DB.
    @OneToMany(mappedBy = "preventivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreventivoItem> items = new ArrayList<>();

    private Double taxRate;
    private Double subtotal;
    private Double taxAmount;
    private Double discount;
    private Double total;
}

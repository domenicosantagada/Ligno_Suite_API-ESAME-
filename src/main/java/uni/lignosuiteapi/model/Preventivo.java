package uni.lignosuiteapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "preventivo")
public class Preventivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @EqualsAndHashCode.Include: include solo l'id nel calcolo di equals() e hashCode()
    @EqualsAndHashCode.Include
    private Long id;

    private Long invoiceNumber;

    // === Riferimento all'utente ===
    // Sostituisce "private Long utenteId;"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    // Usiamo LocalDate e forziamo la formattazione come String per non rompere il frontend Angular
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

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

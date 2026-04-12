package uni.lignosuiteapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entità JPA collegata alla tabella "preventivo" nel database.
 * Rappresenta un preventivo creato dal falegname per un cliente.
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "preventivo")
public class Preventivo {

    // DATI PRINCIPALI
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private Long invoiceNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String fromName;
    private String fromEmail;
    private String fromPiva;


    private String toName;
    private String toEmail;
    private String toPiva;


    private Double taxRate;

    private Double subtotal;

    private Double taxAmount;

    private Double discount;

    private Double total;

    // Relazione ManyToOne con Utente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    // Relazione OneToMany con PreventivoItem
    @OneToMany(mappedBy = "preventivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreventivoItem> items = new ArrayList<>();
}

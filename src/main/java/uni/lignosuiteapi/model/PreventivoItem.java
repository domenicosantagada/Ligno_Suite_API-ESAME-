package uni.lignosuiteapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "preventivo_item")
public class PreventivoItem {

    // Cambiamo String in Long per sfruttare l'auto-incremento del Database.
    // Se il frontend invia una stringa casuale, Spring la ignorerà per i nuovi inserimenti
    // e restituirà il VERO Id numerico generato dal database.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // === RELAZIONE BIDIREZIONALE: L'Item sa a quale preventivo appartiene ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preventivo_id", nullable = false)
    @JsonIgnore // FONDAMENTALE! Senza questo, andresti in Loop Infinito (StackOverflow) durante le chiamate API!
    private Preventivo preventivo;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal quantity;

    private String unitaMisura;

    @Column(nullable = false)
    private BigDecimal rate;

    private BigDecimal amount;

    // === TRUCCHETTO HIBERNATE: Calcolo automatico ===
    // Calcoliamo automaticamente il prezzo totale (quantità * prezzo unitario)
    // prima di salvare o aggiornare la riga nel database.
    @PrePersist
    @PreUpdate
    public void calcolaAmount() {
        if (this.quantity != null && this.rate != null) {
            // Con BigDecimal si usa multiply() per le moltiplicazioni
            this.amount = this.quantity.multiply(this.rate);
        } else {
            this.amount = BigDecimal.ZERO;
        }
    }
}

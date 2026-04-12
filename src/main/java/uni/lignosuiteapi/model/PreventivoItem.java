package uni.lignosuiteapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entità JPA collegata alla tabella "preventivo_item" nel database.
 * Rappresenta un singolo item (riga) all'interno di un preventivo.
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "preventivo_item")
public class PreventivoItem {

    // DATI PRINCIPALI

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    @Column(nullable = false, columnDefinition = "TEXT") // Permette descrizioni più lunghe
    private String description;

    @Column(nullable = false)
    private BigDecimal quantity;

    private String unitaMisura;

    @Column(nullable = false)
    private BigDecimal rate;

    private BigDecimal amount;

    // Relazione ManyToOne con Preventivo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preventivo_id", nullable = false)
    @JsonIgnore
    private Preventivo preventivo;

    // METODI DI UTILITY

    /**
     * Metodo che viene chiamato automaticamente da Hibernate prima di salvare (PrePersist) o aggiornare (PreUpdate) un item del preventivo.
     * Si occupa di calcolare il campo "amount" come il prodotto di "quantity" e "rate". Se uno dei due è null, "amount" viene impostato a zero.
     */
    @PrePersist
    @PreUpdate
    public void calcolaAmount() {

        if (this.quantity != null && this.rate != null) {

            this.amount = this.quantity.multiply(this.rate);

        } else {

            this.amount = BigDecimal.ZERO;

        }
    }
}

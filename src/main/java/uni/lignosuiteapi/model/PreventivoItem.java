package uni.lignosuiteapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

/*
 * @Entity: Indica a Spring Data JPA (Hibernate) che questa classe Java corrisponde
 * a una tabella nel database relazionale. Il nome della tabella sarà, di default, "preventivo_item".
 * @Data: indica a Lombok di generare automaticamente i metodi Getter, Setter, toString(), equals() e hashCode().
 */
@Entity
@Data
public class PreventivoItem {

    /*
     * In questo caso non abbiamo @GeneratedValue perchè lato frontend (Angular) generiamo un ID univoco per ogni riga del preventivo.
     * */
    @Id
    private String id;

    /**
     * @Column(columnDefinition = "TEXT"): La descrizione di un articolo potrebbe superare i 255 caratteri.
     * Diciamo al DB di allocare più spazio usando il tipo TEXT.
     */
    @Column(name = "description", columnDefinition = "TEXT")

    private String description;
    private Double quantity;
    private String unitaMisura;
    private Double rate;
    private Double amount;
}


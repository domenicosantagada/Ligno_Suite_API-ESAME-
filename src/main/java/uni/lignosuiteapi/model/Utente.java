package uni.lignosuiteapi.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @Entity: Indica a Spring Data JPA (Hibernate) che questa classe Java corrisponde
 * a una tabella nel database relazionale. Il nome della tabella sarà, di default, "utente".
 * * @Data: È un'annotazione della libreria "Lombok". Durante la compilazione, genera
 * automaticamente tutti i metodi Getter, Setter, toString(), equals() e hashCode(),
 * mantenendo il codice pulito e compatto.
 */
@Entity
@Data
public class Utente {

    /**
     * @Id: Segnala che questo campo è la Chiave Primaria (Primary Key) della tabella.
     * @GeneratedValue: Specifica come viene generato l'ID. L'opzione "IDENTITY" indica
     * che deleghiamo la generazione al database (corrisponde all'AUTO_INCREMENT in SQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column(unique = true): Aggiunge un vincolo (constraint) nel database.
     * Impedisce che due righe della tabella abbiano lo stesso valore per l'email.
     */
    @Column(unique = true)
    private String email;
    private String password;

    private String nome;
    private String nomeAzienda;
    private String nomeTitolare;
    private String cognomeTitolare;
    private String telefono;
    private String partitaIva;
    private String codiceFiscale;
    private String indirizzo;
    private String citta;
    private String cap;
    private String provincia;

    /**
     * Il logo convertito in stringa Base64 può essere molto lungo (spesso supera i 255 caratteri
     * consentiti dai VARCHAR standard).
     *
     * @Column(columnDefinition = "TEXT"): Forza la creazione della colonna nel database
     * come tipo TEXT (o LONGTEXT a seconda del dialetto SQL), permettendo di salvare stringhe molto lunghe.
     */
    @Column(columnDefinition = "TEXT")
    private String logoBase64;
}

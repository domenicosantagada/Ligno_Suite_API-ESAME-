package uni.lignosuiteapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Entità che rappresenta un Cliente all'interno della rubrica di un falegname (utente).
 */
@Entity
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Chiave primaria generata dal DB (Auto Increment)

    private Long utenteId; // ID dell'utente (falegname) a cui questo cliente è associato
    private String nome;
    private String email;
    private String telefono;
    private String partitaIva;
}

package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Preventivo;

import java.util.List;

/**
 * Repository JPA per l'entità Preventivo.
 */
@Repository
public interface PreventivoRepository extends JpaRepository<Preventivo, Long> {

    // Spring Data JPA legge il nome di questo metodo e genera in automatico la query:
    // SELECT * FROM preventivo WHERE utente_id = ?
    List<Preventivo> findByUtenteId(Long utenteId);

    // Verifica se esiste già un preventivo con lo stesso numero fattura per lo stesso utente.
    boolean existsByUtenteIdAndInvoiceNumber(Long utenteId, Long invoiceNumber);

    // Spring Data JPA legge il nome di questo metodo e genera in automatico la query:
    // SELECT * FROM preventivo WHERE to_email = ?
    List<Preventivo> findByToEmail(String toEmail);

    // Query personalizzata per ottenere il prossimo numero di fattura disponibile per un utente specifico.
    @Query("SELECT COALESCE(MAX(p.invoiceNumber), 0) + 1 FROM Preventivo p WHERE p.utente.id = :utenteId")
    Long getNextInvoiceNumber(@Param("utenteId") Long utenteId);
}

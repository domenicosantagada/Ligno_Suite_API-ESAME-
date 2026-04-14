package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Preventivo;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA per l'entità Preventivo.
 */
@Repository
public interface PreventivoRepository extends JpaRepository<Preventivo, Long> {

    /// --- PER LA TABELLA (List DTO) ---
    // Spring Data JPA genera: SELECT * FROM preventivo WHERE utente_id = ?
    // Ottimo per recuperare le informazioni base senza pesare sul DB.
    List<Preventivo> findByUtenteId(Long utenteId);

    // --- PER IL DETTAGLIO (Full DTO) E LA SICUREZZA ---
    // Risolve l'errore LazyInitialization caricando gli items con JOIN FETCH.
    // Garantisce che un utente non possa aprire il preventivo di un altro tramite ID.
    @Query("SELECT p FROM Preventivo p LEFT JOIN FETCH p.items WHERE p.id = :preventivoId AND p.utente.id = :utenteId")
    Optional<Preventivo> findByIdAndUtenteIdWithItems(@Param("preventivoId") Long preventivoId, @Param("utenteId") Long utenteId);

    // Verifica se esiste già un preventivo con lo stesso numero fattura per lo stesso utente.
    boolean existsByUtenteIdAndInvoiceNumber(Long utenteId, Long invoiceNumber);

    // Spring Data JPA legge il nome di questo metodo e genera in automatico la query:
    // SELECT * FROM preventivo WHERE to_email = ?
    List<Preventivo> findByToEmail(String toEmail);

    // Query personalizzata per ottenere il prossimo numero di fattura disponibile per un utente specifico.
    @Query("SELECT COALESCE(MAX(p.invoiceNumber), 0) + 1 FROM Preventivo p WHERE p.utente.id = :utenteId")
    Long getNextInvoiceNumber(@Param("utenteId") Long utenteId);

    // --- PER IL CLIENTE: DETTAGLIO E SICUREZZA ---
    // Carica gli articoli e verifica che il preventivo sia destinato a questa email
    @Query("SELECT p FROM Preventivo p LEFT JOIN FETCH p.items WHERE p.id = :preventivoId AND p.toEmail = :email")
    Optional<Preventivo> findByIdAndToEmailWithItems(@Param("preventivoId") Long preventivoId, @Param("email") String email);
}

package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Preventivo;

import java.util.List;

@Repository
public interface PreventivoRepository extends JpaRepository<Preventivo, Long> {

    List<Preventivo> findByUtenteId(Long utenteId);

    // Fa il lavoro che prima facevi con lo .stream() nel Service, ma lo fa direttament nel Database in un millisecondo!
    boolean existsByUtenteIdAndInvoiceNumber(Long utenteId, Long invoiceNumber);

    List<Preventivo> findByToEmail(String toEmail);

    // Calcola il prossimo numero fattura per l'utente.
    // COALESCE gestisce il caso in cui l'utente non abbia ancora nessun preventivo (restituisce 0 + 1).
    @Query("SELECT COALESCE(MAX(p.invoiceNumber), 0) + 1 FROM Preventivo p WHERE p.utente.id = :utenteId")
    Long getNextInvoiceNumber(@Param("utenteId") Long utenteId);
}

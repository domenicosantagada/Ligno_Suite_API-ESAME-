package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Preventivo;

import java.util.List;

@Repository
public interface PreventivoRepository extends JpaRepository<Preventivo, Long> {

    // AGGIUNGI QUESTO METODO: Trova tutti i preventivi di un utente specifico
    List<Preventivo> findByUtenteId(Long utenteId);

    // NUOVA QUERY: Trova il preventivo con il numero più alto per questo utente
    @Query("SELECT COALESCE(MAX(p.invoiceNumber), 0) FROM Preventivo p WHERE p.utenteId = :utenteId")
    Long findMaxInvoiceNumberByUtenteId(@Param("utenteId") Long utenteId);
}

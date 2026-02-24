package uni.toolfalegnameriaapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.toolfalegnameriaapi.model.Preventivo;

import java.util.List;

@Repository
public interface PreventivoRepository extends JpaRepository<Preventivo, String> {

    // AGGIUNGI QUESTO METODO: Trova tutti i preventivi di un utente specifico
    List<Preventivo> findByUtenteId(Long utenteId);
}

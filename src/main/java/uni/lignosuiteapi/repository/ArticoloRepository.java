package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Articolo;

import java.util.List;

/**
 * Repository JPA per l'entità Articolo
 */
@Repository
public interface ArticoloRepository extends JpaRepository<Articolo, Long> {

    // Spring Data JPA legge il nome di questo metodo e genera in automatico la query:
    // SELECT * FROM articolo WHERE utente_id = ?
    List<Articolo> findByUtenteId(Long utenteId);
}

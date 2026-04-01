package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Utente;

import java.util.Optional;

/**
 * Repository JPA per l'entità Utente
 */
@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    /**
     * Recupera un utente tramite email
     */
    Optional<Utente> findByEmail(String email);
}

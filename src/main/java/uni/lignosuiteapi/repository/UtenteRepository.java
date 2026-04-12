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

    // Spring Data JPA legge il nome di questo metodo e genera in automatico la query:
    // SELECT * FROM utente WHERE email = ?
    // Optional è usato per indicare che il risultato potrebbe essere presente o meno (es. se l'email non esiste).
    Optional<Utente> findByEmail(String email);
}

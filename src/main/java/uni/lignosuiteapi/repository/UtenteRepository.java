package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Utente;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {
    // Metodo per controllare se un'email esiste già
    boolean existsByEmail(String email);

    // Metodo per trovare un utente tramite email e password (per il login)
    Utente findByEmailAndPassword(String email, String password);
}

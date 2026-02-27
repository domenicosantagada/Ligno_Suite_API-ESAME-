package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Utente;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    /**
     * Metodo per controllare se un'email esiste già nel database.
     * La parola chiave 'existsBy' genera una query SQL ottimizzata molto veloce:
     * "SELECT COUNT(*) FROM utente WHERE email = ?" che restituisce true o false.
     * Viene usato nel metodo di Registrazione dell'AuthController per evitare duplicati.
     */
    boolean existsByEmail(String email);

    /**
     * Metodo per trovare un utente combinando due campi con l'operatore AND logico.
     * Spring capisce dai nomi 'Email' e 'Password' che deve generare:
     * "SELECT * FROM utente WHERE email = ? AND password = ?"
     */
    Utente findByEmailAndPassword(String email, String password);
}

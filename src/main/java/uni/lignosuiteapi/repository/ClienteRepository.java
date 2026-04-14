package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Cliente;

import java.util.List;

/**
 * Repository JPA per l'entità Cliente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Spring Data JPA legge il nome di questo metodo e genera in automatico la query:
    // SELECT * FROM cliente WHERE utente_id = ?
    List<Cliente> findByUtenteId(Long utenteId);

}

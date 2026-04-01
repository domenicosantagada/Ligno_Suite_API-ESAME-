package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Cliente;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Spring Data JPA genera automaticamente la query: SELECT * FROM cliente WHERE utente_id = ?
    List<Cliente> findByUtenteId(Long utenteId);

}

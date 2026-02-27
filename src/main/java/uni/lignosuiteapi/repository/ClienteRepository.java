package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.Cliente;

import java.util.List;

/**
 * @Repository: Indica a Spring che questa interfaccia è un componente dedicato
 * all'accesso ai dati (database). Spring la "inietterà" (Dependency Injection)
 * nei controller o nei service che ne hanno bisogno tramite @Autowired.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /* *
     * JpaRepository<Cliente, Long>: Estendendo questa interfaccia, ereditiamo GRATIS
     * tutti i metodi CRUD standard: save(), findById(), findAll(), deleteById(), ecc.
     * I generici indicano:
     * 1. Cliente: L'entità (tabella) su cui operare.
     * 2. Long: Il tipo di dato della Chiave Primaria (l'ID) dell'entità.
     */

    /**
     * QUERY METHOD (Metodo derivato dal nome)
     * Spring Data JPA analizza il nome di questo metodo e genera automaticamente
     * la query SQL corrispondente: "SELECT * FROM cliente WHERE utente_id = ?"
     * È essenziale per il nostro approccio Multi-Tenant: un utente recupera solo i SUOI clienti.
     */
    List<Cliente> findByUtenteId(Long utenteId);
}

/**
 * Spring Data JPA usa:
 * Reflection
 * Metadata dell’entità JPA
 * Un parser interno dei nomi metodo
 * Il parser riconosce parole chiave standard:
 * Keyword	Significato
 * findBy	SELECT con WHERE
 * And	AND logico
 * Or	OR logico
 * Between	BETWEEN
 * LessThan	<
 * GreaterThan	>
 * Like	LIKE
 * OrderBy	ORDER BY
 */

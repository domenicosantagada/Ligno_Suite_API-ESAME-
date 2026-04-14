package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.PreventivoItem;

/**
 * Repository JPA per l'entità PreventivoItem.
 */
@Repository
public interface PreventivoItemRepository extends JpaRepository<PreventivoItem, Long> {

    // Vuoto. JPA fornisce già tutti i metodi CRUD di base.
}

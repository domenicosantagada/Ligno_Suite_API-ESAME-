package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.lignosuiteapi.model.PreventivoItem;

@Repository
public interface PreventivoItemRepository extends JpaRepository<PreventivoItem, Long> {

    // Attualmente vuoto: non ti serve alcun metodo per le operazioni classiche!

}

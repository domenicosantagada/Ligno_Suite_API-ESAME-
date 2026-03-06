package uni.lignosuiteapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.lignosuiteapi.model.Articolo;

import java.util.List;

public interface ArticoloRepository extends JpaRepository<Articolo, Long> {

    // Trova tutti gli articoli appartenenti all'utente loggato
    List<Articolo> findByUtenteId(Long utenteId);
}

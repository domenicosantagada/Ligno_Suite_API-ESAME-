package uni.lignosuiteapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.model.Articolo;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.ArticoloRepository;
import uni.lignosuiteapi.repository.UtenteRepository;

import java.util.List;

@Service
public class ArticoloService {

    private final ArticoloRepository articoloRepository;
    private final UtenteRepository utenteRepository;

    // CONSTRUCTOR INJECTION
    public ArticoloService(ArticoloRepository articoloRepository, UtenteRepository utenteRepository) {
        this.articoloRepository = articoloRepository;
        this.utenteRepository = utenteRepository;
    }

    // Metodo per ottenere tutti gli articoli di un utente.
    public List<Articolo> getArticoliByUtenteId(Long utenteId) {

        return articoloRepository.findByUtenteId(utenteId);
    }

    // Metodo per ottenere tutti gli articoli.
    public List<Articolo> getAllArticoli() {

        return articoloRepository.findAll();
    }

    // Metodo per creare un nuovo articolo.
    public Articolo createArticolo(Long utenteId, Articolo articolo) {
        // 1. In JPA, dobbiamo prima recuperare l'oggetto Utente dal database
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // 2. Invece di settare l'ID, assegniamo l'intero oggetto Utente all'articolo
        articolo.setUtente(utente);

        // 3. Salviamo. Hibernate farà la INSERT creando in automatico la foreign key (utente_id)
        return articoloRepository.save(articolo);
    }

    // Metodo per aggiornare un articolo esistente (Aggiunto utenteId)
    public Articolo updateArticolo(Long id, Articolo dettagli, Long utenteId) {
        Articolo articoloEsistente = articoloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato"));

        // CONTROLLO SICUREZZA (IDOR): Verifichiamo che l'articolo appartenga a chi lo vuole modificare
        if (!articoloEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato. L'articolo non ti appartiene.");
        }

        // Aggiorniamo i campi
        articoloEsistente.setNome(dettagli.getNome());
        articoloEsistente.setDescrizione(dettagli.getDescrizione());
        articoloEsistente.setPrezzoAcquisto(dettagli.getPrezzoAcquisto());
        articoloEsistente.setFornitore(dettagli.getFornitore());
        articoloEsistente.setDataAcquisto(dettagli.getDataAcquisto());
        articoloEsistente.setUnitaMisura(dettagli.getUnitaMisura());

        return articoloRepository.save(articoloEsistente);
    }

    // Metodo per eliminare un articolo (Aggiunto utenteId e controllo sicurezza)
    public void deleteArticolo(Long id, Long utenteId) {
        Articolo articoloEsistente = articoloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato"));

        // CONTROLLO SICUREZZA (IDOR): Verifichiamo che l'articolo appartenga a chi lo vuole eliminare
        if (!articoloEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato. L'articolo non ti appartiene.");
        }

        // Usiamo delete() passando l'entità anziché deleteById()
        articoloRepository.delete(articoloEsistente);
    }
}

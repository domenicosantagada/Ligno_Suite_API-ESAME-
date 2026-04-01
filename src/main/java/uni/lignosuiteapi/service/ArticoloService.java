package uni.lignosuiteapi.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ArticoloRepository articoloRepository; // Nome corretto della variabile

    @Autowired
    private UtenteRepository utenteRepository; // Ci serve per recuperare l'utente prima di salvare!

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

    // Metodo per aggiornare un articolo esistente.
    public Articolo updateArticolo(Long id, Articolo dettagli) {
        // In JPA findById restituisce un Optional, ecco come gestirlo in modo pulito in una sola riga:
        Articolo articoloEsistente = articoloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato"));

        // Aggiorniamo i campi desiderati (Logica di Business)
        articoloEsistente.setNome(dettagli.getNome());
        articoloEsistente.setDescrizione(dettagli.getDescrizione());
        articoloEsistente.setPrezzoAcquisto(dettagli.getPrezzoAcquisto());
        articoloEsistente.setFornitore(dettagli.getFornitore());
        articoloEsistente.setDataAcquisto(dettagli.getDataAcquisto());
        articoloEsistente.setUnitaMisura(dettagli.getUnitaMisura());

        // Al posto di .update() usiamo .save()
        // Hibernate sa già che l'articolo ha un ID, quindi farà automaticamente una UPDATE
        return articoloRepository.save(articoloEsistente);
    }

    // Metodo per eliminare un articolo.
    public void deleteArticolo(Long id) {
        if (!articoloRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato");
        }
        articoloRepository.deleteById(id);
    }
}

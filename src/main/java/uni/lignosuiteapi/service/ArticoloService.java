package uni.lignosuiteapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.dto.ArticoloDTO;
import uni.lignosuiteapi.dto.mapper.ArticoloMapper;
import uni.lignosuiteapi.model.Articolo;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.ArticoloRepository;
import uni.lignosuiteapi.repository.UtenteRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service che si occupa di gestire la logica di business per gli Articoli.
 * Tutti i metodi accettano e restituiscono DTO, e usano il Mapper per tradurre tra DTO ed Entity.
 * Il Service si occupa anche di controllare che l'utente abbia il permesso di modificare/eliminare un articolo (IDOR).
 */
@Service
public class ArticoloService {

    private final ArticoloRepository articoloRepository;
    private final UtenteRepository utenteRepository;
    private final ArticoloMapper articoloMapper; // Aggiunto il Mapper

    // CONSTRUCTOR INJECTION
    public ArticoloService(ArticoloRepository articoloRepository, UtenteRepository utenteRepository, ArticoloMapper articoloMapper) {
        this.articoloRepository = articoloRepository;
        this.utenteRepository = utenteRepository;
        this.articoloMapper = articoloMapper;
    }

    /**
     * Metodo che ritorna la lista degli articoli di un utente specifico, identificato dal suo ID.
     */
    public List<ArticoloDTO> getArticoliByUtenteId(Long utenteId) {
        return articoloRepository.findByUtenteId(utenteId).stream()
                .map(articoloMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Metodo che ritorna la lista di tutti gli articoli presenti nel database, indipendentemente dall'utente.
     */
    public List<ArticoloDTO> getAllArticoli() {
        return articoloRepository.findAll().stream()
                .map(articoloMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Metodo che crea un nuovo articolo associato a un utente specifico, identificato dal suo ID.
     */
    public ArticoloDTO createArticolo(Long utenteId, ArticoloDTO articoloDTO) {

        // 1. Verifichiamo che l'utente esista (altrimenti non possiamo associare l'articolo a nessuno)
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // 2. Convertiamo il DTO in un'Entity usando il Mapper
        Articolo articolo = articoloMapper.toEntity(articoloDTO);

        // 3. Associare l'articolo all'utente (impostando la foreign key)
        articolo.setUtente(utente);

        // 4. Salviamo l'articolo nel database
        Articolo salvato = articoloRepository.save(articolo);

        // 5. Convertiamo l'articolo salvato (con ID generato) in un DTO da restituire al controller
        return articoloMapper.toDTO(salvato);
    }

    /**
     * Metodo che aggiorna un articolo esistente, identificato dal suo ID, con i nuovi dettagli forniti nel DTO.
     */
    public ArticoloDTO updateArticolo(Long id, ArticoloDTO dettagli, Long utenteId) {

        // 1. Verifichiamo che l'articolo esista (altrimenti non possiamo aggiornarlo)
        Articolo articoloEsistente = articoloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato"));

        // 2. CONTROLLO SICUREZZA (IDOR): Verifichiamo che l'articolo appartenga a chi lo vuole modificare
        if (!articoloEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato. L'articolo non ti appartiene.");
        }

        // 3. Aggiorniamo i campi dell'articolo esistente con i nuovi dettagli (tranne l'ID e l'utente, che non cambiano)
        articoloEsistente.setNome(dettagli.nome);
        articoloEsistente.setDescrizione(dettagli.descrizione);
        articoloEsistente.setPrezzoAcquisto(dettagli.prezzoAcquisto);
        articoloEsistente.setFornitore(dettagli.fornitore);
        articoloEsistente.setDataAcquisto(dettagli.dataAcquisto);
        articoloEsistente.setUnitaMisura(dettagli.unitaMisura);

        // 4. Salviamo l'articolo aggiornato nel database
        Articolo aggiornato = articoloRepository.save(articoloEsistente);

        // 5. Convertiamo l'articolo aggiornato in un DTO da restituire al controller
        return articoloMapper.toDTO(aggiornato);
    }

    /**
     * Metodo che elimina un articolo esistente, identificato dal suo ID.
     */
    public void deleteArticolo(Long id, Long utenteId) {

        // 1. Verifichiamo che l'articolo esista (altrimenti non possiamo eliminarlo)
        Articolo articoloEsistente = articoloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato"));

        // 2. CONTROLLO SICUREZZA (IDOR): Verifichiamo che l'articolo appartenga a chi lo vuole eliminare
        if (!articoloEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato. L'articolo non ti appartiene.");
        }

        // 3. Eliminiamo l'articolo dal database
        articoloRepository.delete(articoloEsistente);
    }
}

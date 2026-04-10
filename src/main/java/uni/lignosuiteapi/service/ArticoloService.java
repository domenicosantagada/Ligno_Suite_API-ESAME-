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

    // MODIFICA: Ritorna List<ArticoloDTO>
    public List<ArticoloDTO> getArticoliByUtenteId(Long utenteId) {
        return articoloRepository.findByUtenteId(utenteId).stream()
                .map(articoloMapper::toDTO) // Traduzione automatica
                .collect(Collectors.toList());
    }

    // MODIFICA: Ritorna List<ArticoloDTO> (Endpoint pericoloso, sconsigliato in prod, ma lo mappiamo per coerenza)
    public List<ArticoloDTO> getAllArticoli() {
        return articoloRepository.findAll().stream()
                .map(articoloMapper::toDTO)
                .collect(Collectors.toList());
    }

    // MODIFICA: Riceve e Ritorna ArticoloDTO
    public ArticoloDTO createArticolo(Long utenteId, ArticoloDTO articoloDTO) {
        // 1. In JPA, dobbiamo prima recuperare l'oggetto Utente dal database
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // 2. Mappiamo il DTO in Entity per il DB
        Articolo articolo = articoloMapper.toEntity(articoloDTO);

        // 3. Assegniamo l'utente
        articolo.setUtente(utente);

        // 4. Salviamo. Hibernate farà la INSERT creando in automatico la foreign key (utente_id)
        Articolo salvato = articoloRepository.save(articolo);

        // 5. Ritorniamo il DTO pulito
        return articoloMapper.toDTO(salvato);
    }

    // MODIFICA: Riceve e Ritorna ArticoloDTO
    public ArticoloDTO updateArticolo(Long id, ArticoloDTO dettagli, Long utenteId) {
        Articolo articoloEsistente = articoloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato"));

        // CONTROLLO SICUREZZA (IDOR): Verifichiamo che l'articolo appartenga a chi lo vuole modificare
        if (!articoloEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato. L'articolo non ti appartiene.");
        }

        // Aggiorniamo i campi estraendoli dal DTO
        articoloEsistente.setNome(dettagli.nome);
        articoloEsistente.setDescrizione(dettagli.descrizione);
        articoloEsistente.setPrezzoAcquisto(dettagli.prezzoAcquisto);
        articoloEsistente.setFornitore(dettagli.fornitore);
        articoloEsistente.setDataAcquisto(dettagli.dataAcquisto);
        articoloEsistente.setUnitaMisura(dettagli.unitaMisura);

        Articolo aggiornato = articoloRepository.save(articoloEsistente);
        return articoloMapper.toDTO(aggiornato);
    }

    // NESSUNA MODIFICA ALLE FIRME: l'eliminazione usa solo gli ID
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

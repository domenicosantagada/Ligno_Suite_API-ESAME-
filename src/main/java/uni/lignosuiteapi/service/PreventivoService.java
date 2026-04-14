package uni.lignosuiteapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.dto.PreventivoDTO;
import uni.lignosuiteapi.dto.PreventivoItemDTO;
import uni.lignosuiteapi.dto.PreventivoListDTO;
import uni.lignosuiteapi.dto.mapper.PreventivoMapper;
import uni.lignosuiteapi.model.Preventivo;
import uni.lignosuiteapi.model.PreventivoItem;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.PreventivoRepository;
import uni.lignosuiteapi.repository.UtenteRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service che si occupa di gestire la logica di business per i Preventivi.
 * Tutti i metodi accettano e restituiscono DTO, e usano il Mapper per tradurre tra DTO ed Entity.
 * Il Service si occupa anche di controllare che l'utente abbia il permesso di modificare/eliminare un preventivo (IDOR).
 */
@Service
public class PreventivoService {

    private final PreventivoRepository preventivoRepository;
    private final UtenteRepository utenteRepository;
    private final PreventivoMapper preventivoMapper; // Iniettiamo il Mapper

    // CONSTRUCTOR INJECTION
    public PreventivoService(PreventivoRepository preventivoRepository, UtenteRepository utenteRepository, PreventivoMapper preventivoMapper) {
        this.preventivoRepository = preventivoRepository;
        this.utenteRepository = utenteRepository;
        this.preventivoMapper = preventivoMapper;
    }

    /**
     * Metodo che ritorna la lista dei preventivi di un utente specifico, identificato dal suo ID.
     */
    public List<PreventivoListDTO> getAllPreventivi(Long utenteId) {
        return preventivoRepository.findByUtenteId(utenteId).stream()
                .map(preventivoMapper::toListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Metodo che ritorna un preventivo specifico, identificato dal suo ID, solo se appartiene all'utente specificato (IDOR).
     */
    public PreventivoDTO getPreventivoById(Long id, Long utenteId) {

        // 1. Recupera il preventivo dal database, o lancia un 404 se non esiste
        Preventivo preventivo = preventivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preventivo non trovato"));

        // 2. Controlla che il preventivo appartenga all'utente che sta facendo la richiesta (IDOR), altrimenti lancia un 403
        if (!preventivo.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        // 3. Traduce il preventivo in DTO e lo ritorna
        return preventivoMapper.toDTO(preventivo);
    }

    /**
     * Metodo che crea un nuovo preventivo associato a un utente specifico, identificato dal suo ID.
     */
    public PreventivoDTO createPreventivo(Long utenteId, PreventivoDTO preventivoDTO) {

        // 1. Verifichiamo che l'utente esista (altrimenti non possiamo associare il preventivo a nessuno)
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // 2. Traduce il DTO in Entity (il Mapper si occupa di questo)
        Preventivo preventivo = preventivoMapper.toEntity(preventivoDTO);
        preventivo.setUtente(utente);

        // 3. Salva il preventivo nel database (il cascade salverà anche i figli, se ci sono)
        if (preventivo.getItems() != null) {
            for (PreventivoItem item : preventivo.getItems()) {
                item.setPreventivo(preventivo);
            }
        }

        // 4. Traduce l'Entity salvata in DTO e la ritorna
        Preventivo salvato = preventivoRepository.save(preventivo);

        return preventivoMapper.toDTO(salvato);
    }

    /**
     * Metodo che aggiorna un preventivo esistente, identificato dal suo ID, con i nuovi dettagli forniti nel DTO.
     */
    public PreventivoDTO updatePreventivo(Long id, PreventivoDTO datiAggiornati, Long utenteId) {

        // 1. Verifichiamo che il preventivo esista (altrimenti non possiamo aggiornarlo)
        Preventivo esistente = preventivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preventivo non trovato"));

        // 2. CONTROLLO SICUREZZA (IDOR): Verifichiamo che il preventivo appartenga a chi lo vuole modificare
        if (!esistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        // 3. Aggiorniamo i campi del preventivo esistente con i nuovi dettagli (tranne l'ID e l'utente, che non cambiano)
        esistente.setDate(datiAggiornati.date);
        esistente.setInvoiceNumber(datiAggiornati.invoiceNumber);
        esistente.setFromName(datiAggiornati.fromName);
        esistente.setFromPiva(datiAggiornati.fromPiva);
        esistente.setFromEmail(datiAggiornati.fromEmail);
        esistente.setToName(datiAggiornati.toName);
        esistente.setToPiva(datiAggiornati.toPiva);
        esistente.setToEmail(datiAggiornati.toEmail);
        esistente.setSubtotal(datiAggiornati.subtotal);
        esistente.setTaxRate(datiAggiornati.taxRate);
        esistente.setTaxAmount(datiAggiornati.taxAmount);
        esistente.setDiscount(datiAggiornati.discount);
        esistente.setTotal(datiAggiornati.total);

        // 4. Gestione degli items: rimuoviamo tutti i vecchi items e aggiungiamo quelli nuovi (orphanRemoval si occuperà di eliminare quelli rimossi)
        esistente.getItems().clear();

        if (datiAggiornati.items != null) {
            for (PreventivoItemDTO itemDTO : datiAggiornati.items) {
                PreventivoItem newItem = preventivoMapper.itemToEntity(itemDTO);
                newItem.setPreventivo(esistente); // Collega il nuovo figlio al padre
                esistente.getItems().add(newItem);
            }
        }

        // 5. Salviamo il preventivo aggiornato nel database (il cascade si occuperà di salvare anche i nuovi figli e rimuovere quelli eliminati)
        Preventivo aggiornato = preventivoRepository.save(esistente);

        // 6. Traduce l'Entity aggiornata in DTO e la ritorna
        return preventivoMapper.toDTO(aggiornato);
    }

    /**
     * Metodo che elimina un preventivo esistente, identificato dal suo ID.
     */
    public void deletePreventivo(Long id, Long utenteId) {

        // 1. Verifichiamo che il preventivo esista (altrimenti non possiamo eliminarlo)
        Preventivo esistente = preventivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preventivo non trovato"));

        // 2. CONTROLLO SICUREZZA (IDOR): Verifichiamo che il preventivo appartenga a chi lo vuole eliminare
        if (!esistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        // 3. Eliminiamo il preventivo dal database
        preventivoRepository.delete(esistente);
    }

    /**
     * Metodo che calcola il prossimo numero fattura disponibile per un utente, basandosi sui preventivi esistenti.
     */
    public Long getNextInvoiceNumber(Long utenteId) {

        // Recupera tutti i preventivi dell'utente, estrae i numeri fattura non nulli, trova il massimo e ritorna il successivo (max + 1). Se non ci sono numeri fattura, ritorna 1.
        return preventivoRepository.findByUtenteId(utenteId).stream()
                .map(Preventivo::getInvoiceNumber)
                .filter(java.util.Objects::nonNull)
                .max(Long::compareTo)
                .orElse(0L) + 1L;
    }
}

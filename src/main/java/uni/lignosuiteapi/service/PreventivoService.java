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

@Service
public class PreventivoService {

    private final PreventivoRepository preventivoRepository;
    private final UtenteRepository utenteRepository;
    private final PreventivoMapper preventivoMapper; // Iniettiamo il Mapper

    public PreventivoService(PreventivoRepository preventivoRepository, UtenteRepository utenteRepository, PreventivoMapper preventivoMapper) {
        this.preventivoRepository = preventivoRepository;
        this.utenteRepository = utenteRepository;
        this.preventivoMapper = preventivoMapper;
    }

    // MODIFICA: Restituisce la versione super-leggera
    public List<PreventivoListDTO> getAllPreventivi(Long utenteId) {
        return preventivoRepository.findByUtenteId(utenteId).stream()
                .map(preventivoMapper::toListDTO)
                .collect(Collectors.toList());
    }

    // MODIFICA: Restituisce il DTO completo quando se ne apre uno specifico
    public PreventivoDTO getPreventivoById(Long id, Long utenteId) {
        Preventivo preventivo = preventivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preventivo non trovato"));

        if (!preventivo.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }
        return preventivoMapper.toDTO(preventivo);
    }

    public PreventivoDTO createPreventivo(Long utenteId, PreventivoDTO preventivoDTO) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        Preventivo preventivo = preventivoMapper.toEntity(preventivoDTO);
        preventivo.setUtente(utente);

        // PASSAGGIO FONDAMENTALE: Riagganciamo il Padre ai Figli generati dal Mapper
        if (preventivo.getItems() != null) {
            for (PreventivoItem item : preventivo.getItems()) {
                item.setPreventivo(preventivo);
            }
        }

        Preventivo salvato = preventivoRepository.save(preventivo);
        return preventivoMapper.toDTO(salvato);
    }

    public PreventivoDTO updatePreventivo(Long id, PreventivoDTO datiAggiornati, Long utenteId) {
        Preventivo esistente = preventivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preventivo non trovato"));

        if (!esistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        // Travaso Dati principali
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

        // GESTIONE LISTA FIGLI (Svuotiamo e Riempiamo)
        esistente.getItems().clear(); // Rimuove le vecchie righe dal DB (orphanRemoval)

        if (datiAggiornati.items != null) {
            for (PreventivoItemDTO itemDTO : datiAggiornati.items) {
                PreventivoItem newItem = preventivoMapper.itemToEntity(itemDTO);
                newItem.setPreventivo(esistente); // Collega il nuovo figlio al padre
                esistente.getItems().add(newItem);
            }
        }

        Preventivo aggiornato = preventivoRepository.save(esistente);
        return preventivoMapper.toDTO(aggiornato);
    }

    public void deletePreventivo(Long id, Long utenteId) {
        Preventivo esistente = preventivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preventivo non trovato"));

        if (!esistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }
        preventivoRepository.delete(esistente);
    }
}

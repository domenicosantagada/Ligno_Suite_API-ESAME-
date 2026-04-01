package uni.lignosuiteapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.model.Preventivo;
import uni.lignosuiteapi.model.PreventivoItem;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.PreventivoRepository;
import uni.lignosuiteapi.repository.UtenteRepository;

import java.util.List;

@Service
public class PreventivoService {

    @Autowired
    private PreventivoRepository preventivoRepository;

    @Autowired
    private UtenteRepository utenteRepository; // Ci serve per associare l'utente!

    private boolean existsInvoiceNumber(Long utenteId, Long invoiceNumber) {
        // Usa la query super veloce del repository
        return preventivoRepository.existsByUtenteIdAndInvoiceNumber(utenteId, invoiceNumber);
    }

    public List<Preventivo> getAllPreventivi(Long utenteId) {
        return preventivoRepository.findByUtenteId(utenteId);
    }

    // Passiamo l'utenteId dal controller per sicurezza e architettura
    public Preventivo createPreventivo(Long utenteId, Preventivo preventivo) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        if (existsInvoiceNumber(utenteId, preventivo.getInvoiceNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Numero preventivo già in uso.");
        }

        preventivo.setUtente(utente);

        // Fondamentale: per ogni item, dobbiamo dire che appartiene a questo preventivo
        if (preventivo.getItems() != null) {
            for (PreventivoItem item : preventivo.getItems()) {
                item.setPreventivo(preventivo);
            }
        }

        // Salva il Preventivo e in automatico salva anche tutti gli Item nella tabella preventivo_item!
        return preventivoRepository.save(preventivo);
    }

    public Preventivo updatePreventivo(Long id, Preventivo datiAggiornati, Long utenteId) {
        Preventivo preventivoEsistente = preventivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preventivo non trovato."));

        // Controllo permessi
        if (!preventivoEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        if (!preventivoEsistente.getInvoiceNumber().equals(datiAggiornati.getInvoiceNumber()) &&
                existsInvoiceNumber(utenteId, datiAggiornati.getInvoiceNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Numero preventivo già in uso.");
        }

        // Travaso Dati anagrafici e totali
        preventivoEsistente.setInvoiceNumber(datiAggiornati.getInvoiceNumber());
        preventivoEsistente.setDate(datiAggiornati.getDate());
        preventivoEsistente.setFromName(datiAggiornati.getFromName());
        preventivoEsistente.setFromEmail(datiAggiornati.getFromEmail());
        preventivoEsistente.setFromPiva(datiAggiornati.getFromPiva());
        preventivoEsistente.setToName(datiAggiornati.getToName());
        preventivoEsistente.setToEmail(datiAggiornati.getToEmail());
        preventivoEsistente.setToPiva(datiAggiornati.getToPiva());
        preventivoEsistente.setTaxRate(datiAggiornati.getTaxRate());
        preventivoEsistente.setSubtotal(datiAggiornati.getSubtotal());
        preventivoEsistente.setTaxAmount(datiAggiornati.getTaxAmount());
        preventivoEsistente.setDiscount(datiAggiornati.getDiscount());
        preventivoEsistente.setTotal(datiAggiornati.getTotal());

        // Travaso degli Items! Grazie a orphanRemoval=true, Spring farà da solo la comparazione
        // per capire quali item vanno aggiornati, quali inseriti e quali cancellati.
        preventivoEsistente.getItems().clear(); // Svuota i vecchi
        if (datiAggiornati.getItems() != null) {
            for (PreventivoItem item : datiAggiornati.getItems()) {
                item.setPreventivo(preventivoEsistente); // Riassocia il nuovo item
                preventivoEsistente.getItems().add(item);
            }
        }

        return preventivoRepository.save(preventivoEsistente);
    }

    public void deletePreventivo(Long id, Long utenteId) {
        Preventivo preventivoEsistente = preventivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preventivo non trovato."));

        if (!preventivoEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        // Questo cancellerà in automatico dal DB anche tutti i PreventivoItem collegati!
        preventivoRepository.delete(preventivoEsistente);
    }

    public Long getNextInvoiceNumber(Long utenteId) {
        return preventivoRepository.getNextInvoiceNumber(utenteId);
    }

    public List<Preventivo> getPreventiviPerCliente(String email) {
        return preventivoRepository.findByToEmail(email);
    }
}

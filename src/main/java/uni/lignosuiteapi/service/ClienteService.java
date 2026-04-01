package uni.lignosuiteapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.model.Cliente;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.ClienteRepository;
import uni.lignosuiteapi.repository.UtenteRepository;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UtenteRepository utenteRepository; // Serve per associare il cliente all'utente

    public List<Cliente> getAllClientiDb() {
        return clienteRepository.findAll();
    }

    public List<Cliente> getAllClienti(Long utenteId) {
        return clienteRepository.findByUtenteId(utenteId);
    }

    // Aggiunto parametro utenteId per creare la relazione in modo corretto
    public Cliente createCliente(Long utenteId, Cliente cliente) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        cliente.setUtente(utente);
        return clienteRepository.save(cliente);
    }

    // Aggiunto parametro utenteId per i controlli di sicurezza
    public Cliente updateCliente(Long id, Cliente datiAggiornati, Long utenteId) {
        Cliente clienteEsistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossibile aggiornare: cliente non trovato."));

        // LOGICA DI BUSINESS: Verifichiamo che l'utente stia modificando un SUO cliente
        if (!clienteEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        // Travaso dei dati
        clienteEsistente.setNome(datiAggiornati.getNome());
        clienteEsistente.setEmail(datiAggiornati.getEmail());
        clienteEsistente.setTelefono(datiAggiornati.getTelefono());
        clienteEsistente.setPartitaIva(datiAggiornati.getPartitaIva());

        // La formattaDati() avverrà in automatico grazie a @PreUpdate
        return clienteRepository.save(clienteEsistente);
    }

    public void deleteCliente(Long id, Long utenteId) {
        Cliente clienteEsistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente non trovato."));

        // LOGICA DI BUSINESS: Verifichiamo prima di eliminare
        if (!clienteEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        clienteRepository.delete(clienteEsistente);
    }
}

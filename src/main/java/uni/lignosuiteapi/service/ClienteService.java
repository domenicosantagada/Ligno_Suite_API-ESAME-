package uni.lignosuiteapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.dto.ClienteDTO;
import uni.lignosuiteapi.dto.mapper.ClienteMapper;
import uni.lignosuiteapi.model.Cliente;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.ClienteRepository;
import uni.lignosuiteapi.repository.UtenteRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service che si occupa di gestire la logica di business per i Clienti.
 * Tutti i metodi accettano e restituiscono DTO, e usano il Mapper per tradurre tra DTO ed Entity.
 * Il Service si occupa anche di controllare che l'utente abbia il permesso di modificare/eliminare un cliente (IDOR).
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UtenteRepository utenteRepository;
    private final ClienteMapper clienteMapper; // Aggiunto il Mapper

    // CONSTRUCTOR INJECTION
    public ClienteService(ClienteRepository clienteRepository, UtenteRepository utenteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.utenteRepository = utenteRepository;
        this.clienteMapper = clienteMapper;
    }

    /**
     * Metodo che ritorna la lista dei clienti di un utente specifico, identificato dal suo ID.
     */
    public List<ClienteDTO> getAllClienti(Long utenteId) {
        return clienteRepository.findByUtenteId(utenteId).stream()
                .map(clienteMapper::toDTO) // Traduce ogni Entity in DTO
                .collect(Collectors.toList());
    }

    /**
     * Metodo che crea un nuovo cliente associato a un utente specifico, identificato dal suo ID.
     */
    public ClienteDTO createCliente(Long utenteId, ClienteDTO clienteDTO) {

        // 1. Verifichiamo che l'utente esista (altrimenti non possiamo associare il cliente a nessuno)
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // 2. Traduce il DTO in Entity (il Mapper si occupa di questo)
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        cliente.setUtente(utente);

        // 3. Salva il cliente nel database
        Cliente salvato = clienteRepository.save(cliente);

        // 4. Traduce l'Entity salvata in DTO e la ritorna
        return clienteMapper.toDTO(salvato);
    }

    /**
     * Metodo che aggiorna un cliente esistente, identificato dal suo ID, con i dati forniti nel DTO.
     */
    public ClienteDTO updateCliente(Long id, ClienteDTO datiAggiornati, Long utenteId) {

        // 1. Verifichiamo che il cliente esista (altrimenti non possiamo aggiornarlo)
        Cliente clienteEsistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossibile aggiornare: cliente non trovato."));

        // 2. LOGICA DI BUSINESS: Verifichiamo che l'utente abbia il permesso di aggiornare questo cliente (IDOR)
        if (!clienteEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        // 3. Aggiorniamo i campi del cliente esistente con i nuovi dati
        clienteEsistente.setNome(datiAggiornati.nome);
        clienteEsistente.setEmail(datiAggiornati.email);
        clienteEsistente.setTelefono(datiAggiornati.telefono);
        clienteEsistente.setPartitaIva(datiAggiornati.partitaIva);

        // 4. Salviamo il cliente aggiornato nel database
        Cliente aggiornato = clienteRepository.save(clienteEsistente);

        // 5. Traduce l'Entity aggiornata in DTO e la ritorna
        return clienteMapper.toDTO(aggiornato);
    }

    /**
     * Metodo che elimina un cliente esistente, identificato dal suo ID.
     */
    public void deleteCliente(Long id, Long utenteId) {

        // 1. Verifichiamo che il cliente esista (altrimenti non possiamo eliminarlo)
        Cliente clienteEsistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente non trovato."));

        // 2. LOGICA DI BUSINESS: Verifichiamo che l'utente abbia il permesso di eliminare questo cliente (IDOR)
        if (!clienteEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        // 3. Eliminiamo il cliente dal database
        clienteRepository.delete(clienteEsistente);
    }
}

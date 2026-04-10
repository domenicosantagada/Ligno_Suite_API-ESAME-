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

    // MODIFICA: Ritorna List<ClienteDTO>
    public List<ClienteDTO> getAllClienti(Long utenteId) {
        return clienteRepository.findByUtenteId(utenteId).stream()
                .map(clienteMapper::toDTO) // Traduce ogni Entity in DTO
                .collect(Collectors.toList());
    }

    // MODIFICA: Riceve e Ritorna ClienteDTO
    public ClienteDTO createCliente(Long utenteId, ClienteDTO clienteDTO) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // Traduce il DTO in Entity per il database
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        cliente.setUtente(utente);

        Cliente salvato = clienteRepository.save(cliente);

        // Ritraduce in DTO per il frontend
        return clienteMapper.toDTO(salvato);
    }

    // MODIFICA: Riceve e Ritorna ClienteDTO
    public ClienteDTO updateCliente(Long id, ClienteDTO datiAggiornati, Long utenteId) {
        Cliente clienteEsistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossibile aggiornare: cliente non trovato."));

        // LOGICA DI BUSINESS: Verifichiamo che l'utente stia modificando un SUO cliente
        if (!clienteEsistente.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso negato.");
        }

        // Travaso dei dati (usando i campi del DTO)
        clienteEsistente.setNome(datiAggiornati.nome);
        clienteEsistente.setEmail(datiAggiornati.email);
        clienteEsistente.setTelefono(datiAggiornati.telefono);
        clienteEsistente.setPartitaIva(datiAggiornati.partitaIva);

        Cliente aggiornato = clienteRepository.save(clienteEsistente);
        return clienteMapper.toDTO(aggiornato);
    }

    // NESSUNA MODIFICA: L'eliminazione richiede solo gli ID
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

package uni.lignosuiteapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.dto.ClienteDTO;
import uni.lignosuiteapi.service.ClienteService;

import java.util.List;

/**
 * Controller REST per la gestione dei clienti (Rubrica).
 */
@RestController
@RequestMapping("/api/clienti")
public class ClienteController {

    private final ClienteService clienteService;

    // CONSTRUCTOR INJECTION: Sostituisce @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * =========================
     * OTTENERE TUTTI I CLIENTI DI UN UTENTE
     * =========================
     */
    @GetMapping
    public List<ClienteDTO> getAllClienti(Authentication authentication) {
        // Estraiamo l'ID utente in modo sicuro dal Token JWT
        Long utenteId = (Long) authentication.getPrincipal();
        return clienteService.getAllClienti(utenteId);
    }

    /**
     * =========================
     * CREARE UN NUOVO CLIENTE
     * =========================
     */
    @PostMapping
    public ClienteDTO createCliente(Authentication authentication, @RequestBody ClienteDTO clienteDTO) {
        Long utenteId = (Long) authentication.getPrincipal();
        return clienteService.createCliente(utenteId, clienteDTO);
    }

    /**
     * =========================
     * AGGIORNARE UN CLIENTE
     * =========================
     */
    @PutMapping("/{id}")
    public ClienteDTO updateCliente(@PathVariable Long id, Authentication authentication, @RequestBody ClienteDTO clienteDTO) {
        Long utenteId = (Long) authentication.getPrincipal();
        return clienteService.updateCliente(id, clienteDTO, utenteId);
    }

    /**
     * =========================
     * ELIMINARE UN CLIENTE
     * =========================
     */
    @DeleteMapping("/{id}")
    public void deleteCliente(@PathVariable Long id, Authentication authentication) {
        Long utenteId = (Long) authentication.getPrincipal();
        clienteService.deleteCliente(id, utenteId);
    }

    /**
     * ===============================
     * OTTENERE TUTTI I CLIENTI DEL DATABASE (PERICOLO!)
     * ===============================
     * Questo endpoint restituiva i clienti di TUTTI.
     * In un sistema multi-utente è un gravissimo Data Leak.
     * Commentato per sicurezza.
     */
    /*
    @GetMapping("/all")
    private List<ClienteDTO> getAllClientiDb() {
        return clienteService.getAllClientiDb();
    }
    */
}

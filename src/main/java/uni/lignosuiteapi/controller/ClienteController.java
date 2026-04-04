package uni.lignosuiteapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.model.Cliente;
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
    public List<Cliente> getAllClienti(Authentication authentication) {
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
    public Cliente createCliente(Authentication authentication, @RequestBody Cliente cliente) {
        Long utenteId = (Long) authentication.getPrincipal();
        return clienteService.createCliente(utenteId, cliente);
    }

    /**
     * =========================
     * AGGIORNARE UN CLIENTE
     * =========================
     */
    @PutMapping("/{id}")
    public Cliente updateCliente(@PathVariable Long id, Authentication authentication, @RequestBody Cliente cliente) {
        Long utenteId = (Long) authentication.getPrincipal();
        return clienteService.updateCliente(id, cliente, utenteId);
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
    private List<Cliente> getAllClientiDb() {
        return clienteService.getAllClientiDb();
    }
    */
}

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

    // CONSTRUCTOR INJECTION
    public ClienteController(ClienteService clienteService) {

        this.clienteService = clienteService;
    }

    /**
     * Endpoint GET per recuperare tutti i clienti dell'utente loggato.
     * Chiamata: GET /api/clienti
     */
    @GetMapping
    public List<ClienteDTO> getAllClienti(Authentication authentication) {

        Long utenteId = (Long) authentication.getPrincipal();

        return clienteService.getAllClienti(utenteId);
    }

    /**
     * Endpoint POST per creare un nuovo cliente. Il cliente sarà associato all'utente loggato.
     * Chiamata: POST /api/clienti
     */
    @PostMapping
    public ClienteDTO createCliente(Authentication authentication, @RequestBody ClienteDTO clienteDTO) {

        Long utenteId = (Long) authentication.getPrincipal();

        return clienteService.createCliente(utenteId, clienteDTO);
    }

    /**
     * Endpoint PUT per aggiornare un cliente esistente. L'utente può aggiornare solo I SUOI clienti.
     * Chiamata: PUT /api/clienti/{id}
     */
    @PutMapping("/{id}")
    public ClienteDTO updateCliente(@PathVariable Long id, Authentication authentication, @RequestBody ClienteDTO clienteDTO) {

        Long utenteId = (Long) authentication.getPrincipal();

        return clienteService.updateCliente(id, clienteDTO, utenteId);
    }

    /**
     * Endpoint DELETE per eliminare un cliente esistente. L'utente può eliminare solo I SUOI clienti.
     * Chiamata: DELETE /api/clienti/{id}
     */
    @DeleteMapping("/{id}")
    public void deleteCliente(@PathVariable Long id, Authentication authentication) {

        Long utenteId = (Long) authentication.getPrincipal();

        clienteService.deleteCliente(id, utenteId);
    }

    /**
     * Endpoint GET per recuperare TUTTI i clienti di TUTTI gli utenti.
     * Utilizzato solo in fase di test e chiamate con Postman
     */
    /*
    @GetMapping("/all")
    private List<ClienteDTO> getAllClientiDb() {
        return clienteService.getAllClientiDb();
    }
    */
}

package uni.lignosuiteapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.dto.PreventivoDTO;
import uni.lignosuiteapi.dto.PreventivoListDTO;
import uni.lignosuiteapi.service.PreventivoService;

import java.util.List;

/**
 * Controller REST per la gestione dei preventivi.
 */
@RestController
@RequestMapping("/api/preventivi")
public class PreventiviController {

    private final PreventivoService preventivoService;

    // CONSTRUCTOR INJECTION
    public PreventiviController(PreventivoService preventivoService) {

        this.preventivoService = preventivoService;
    }

    /**
     * Endpoint GET per recuperare tutti i preventivi dell'utente loggato.
     * Chiamata: GET /api/preventivi
     */
    @GetMapping
    public List<PreventivoListDTO> getAllPreventivi(Authentication authentication) {

        Long utenteId = (Long) authentication.getPrincipal();

        return preventivoService.getAllPreventivi(utenteId);
    }

    /**
     * Endpoint GET per recuperare un preventivo specifico per ID. L'utente può accedere solo ai SUOI preventivi.
     * Chiamata: GET /api/preventivi/{id}
     */
    @GetMapping("/{id}")
    public PreventivoDTO getPreventivoById(@PathVariable Long id, Authentication authentication) {

        Long utenteId = (Long) authentication.getPrincipal();

        return preventivoService.getPreventivoById(id, utenteId);
    }

    /**
     * Endpoint POST per creare un nuovo preventivo. Il preventivo sarà associato all'utente loggato.
     * Chiamata: POST /api/preventivi
     */
    @PostMapping
    public PreventivoDTO createPreventivo(Authentication authentication, @RequestBody PreventivoDTO preventivoDTO) {

        Long utenteId = (Long) authentication.getPrincipal();

        return preventivoService.createPreventivo(utenteId, preventivoDTO);
    }

    /**
     * Endpoint PUT per aggiornare un preventivo esistente. L'utente può aggiornare solo I SUOI preventivi.
     * Chiamata: PUT /api/preventivi/{id}
     */
    @PutMapping("/{id}")
    public PreventivoDTO updatePreventivo(@PathVariable Long id, Authentication authentication, @RequestBody PreventivoDTO preventivoDTO) {

        Long utenteId = (Long) authentication.getPrincipal();

        return preventivoService.updatePreventivo(id, preventivoDTO, utenteId);
    }

    /**
     * Endpoint DELETE per eliminare un preventivo esistente. L'utente può eliminare solo I SUOI preventivi.
     * Chiamata: DELETE /api/preventivi/{id}
     */
    @DeleteMapping("/{id}")
    public void deletePreventivo(@PathVariable Long id, Authentication authentication) {

        Long utenteId = (Long) authentication.getPrincipal();

        preventivoService.deletePreventivo(id, utenteId);
    }

    /**
     * Endpoint GET per recuperare il prossimo numero di preventivo disponibile per l'utente loggato.
     * Chiamata: GET /api/preventivi/next-number
     */
    @GetMapping("/next-number")
    public Long getNextNumber(Authentication authentication) {

        Long utenteId = (Long) authentication.getPrincipal();

        return preventivoService.getNextInvoiceNumber(utenteId);
    }
}

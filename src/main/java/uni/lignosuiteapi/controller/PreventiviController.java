package uni.lignosuiteapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.model.Preventivo;
import uni.lignosuiteapi.service.PreventivoService;

import java.util.List;

/**
 * Controller REST per la gestione dei preventivi.
 */
@RestController
@RequestMapping("/api/preventivi")
public class PreventiviController {

    private final PreventivoService preventivoService;

    // CONSTRUCTOR INJECTION: Sostituisce @Autowired. Rende il codice più testabile e sicuro.
    public PreventiviController(PreventivoService preventivoService) {
        this.preventivoService = preventivoService;
    }

    @PostMapping
    public Preventivo createPreventivo(Authentication authentication, @RequestBody Preventivo preventivo) {
        // Estraiamo l'ID utente in modo sicuro dal Token JWT
        Long utenteId = (Long) authentication.getPrincipal();
        return preventivoService.createPreventivo(utenteId, preventivo);
    }

    @PutMapping("/{id}")
    public Preventivo updatePreventivo(@PathVariable Long id, Authentication authentication, @RequestBody Preventivo preventivo) {
        // Estraiamo l'ID utente in modo sicuro dal Token JWT
        Long utenteId = (Long) authentication.getPrincipal();
        return preventivoService.updatePreventivo(id, preventivo, utenteId);
    }

    @GetMapping
    public List<Preventivo> getAllPreventivi(Authentication authentication) {
        // Estraiamo l'ID utente in modo sicuro dal Token JWT
        Long utenteId = (Long) authentication.getPrincipal();
        return preventivoService.getAllPreventivi(utenteId);
    }

    @DeleteMapping("/{id}")
    public void deletePreventivo(@PathVariable Long id, Authentication authentication) {
        // Estraiamo l'ID utente in modo sicuro dal Token JWT
        Long utenteId = (Long) authentication.getPrincipal();
        preventivoService.deletePreventivo(id, utenteId);
    }

    @GetMapping("/next-number")
    public Long getNextInvoiceNumber(Authentication authentication) {
        // Estraiamo l'ID utente in modo sicuro dal Token JWT
        Long utenteId = (Long) authentication.getPrincipal();
        return preventivoService.getNextInvoiceNumber(utenteId);
    }

    @GetMapping("/cliente")
    public List<Preventivo> getPreventiviPerCliente(@RequestParam String email) {
        return preventivoService.getPreventiviPerCliente(email);
    }
}

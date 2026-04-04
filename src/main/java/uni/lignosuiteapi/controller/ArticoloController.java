package uni.lignosuiteapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.model.Articolo;
import uni.lignosuiteapi.service.ArticoloService;

import java.util.List;

/**
 * Controller REST per la gestione degli articoli (Prezzario).
 */
@RestController
@RequestMapping("/api/articoli")
// RIMOSSO @CrossOrigin: ora è gestito globalmente da SecurityConfig!
public class ArticoloController {

    private final ArticoloService articoloService;

    // CONSTRUCTOR INJECTION: Sostituisce @Autowired.
    public ArticoloController(ArticoloService articoloService) {
        this.articoloService = articoloService;
    }

    /**
     * Recupera tutti gli articoli dell'utente loggato.
     * N.B: Abbiamo rimosso "/utente/{utenteId}" dal path. Ora basta chiamare GET /api/articoli
     */
    @GetMapping
    public List<Articolo> getArticoliPersonali(Authentication authentication) {
        // Estraiamo l'ID utente in modo sicuro dal Token JWT
        Long utenteId = (Long) authentication.getPrincipal();
        return articoloService.getArticoliByUtenteId(utenteId);
    }

    /**
     * Crea un nuovo articolo per l'utente loggato.
     * N.B: Abbiamo rimosso "/utente/{utenteId}" dal path. Ora basta chiamare POST /api/articoli
     */
    @PostMapping
    public Articolo createArticolo(Authentication authentication, @RequestBody Articolo articolo) {
        // Estraiamo l'ID utente in modo sicuro dal Token JWT
        Long utenteId = (Long) authentication.getPrincipal();
        return articoloService.createArticolo(utenteId, articolo);
    }

    /**
     * Aggiorna un articolo esistente.
     */
    @PutMapping("/{id}")
    public Articolo updateArticolo(@PathVariable Long id, Authentication authentication, @RequestBody Articolo dettagli) {
        Long utenteId = (Long) authentication.getPrincipal();
        // NOTA: Passiamo anche l'utenteId al Service per assicurarci che l'utente stia
        // modificando un SUO articolo e non quello di qualcun altro!
        return articoloService.updateArticolo(id, dettagli, utenteId);
    }

    /**
     * Elimina un articolo specifico.
     */
    @DeleteMapping("/{id}")
    public void deleteArticolo(@PathVariable Long id, Authentication authentication) {
        Long utenteId = (Long) authentication.getPrincipal();
        // NOTA: Passiamo anche l'utenteId al Service per assicurarci che l'utente stia
        // eliminando un SUO articolo e non quello di qualcun altro!
        articoloService.deleteArticolo(id, utenteId);
    }

    /**
     * ATTENZIONE: Metodo utilizzato solo in fase di debug per testare le chiamate api
     * Non espsoto nel frontend
     @GetMapping("/all") public List<Articolo> getAllArticoli() {
     return articoloService.getAllArticoli();
     }
     */
}

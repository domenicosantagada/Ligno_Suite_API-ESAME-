package uni.lignosuiteapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.dto.ArticoloDTO;
import uni.lignosuiteapi.service.ArticoloService;

import java.util.List;

/**
 * Controller REST per la gestione degli articoli (Prezzario).
 */
@RestController
@RequestMapping("/api/articoli")
public class ArticoloController {

    private final ArticoloService articoloService;

    // CONSTRUCTOR INJECTION
    public ArticoloController(ArticoloService articoloService) {

        this.articoloService = articoloService;
    }

    /**
     * Endpoint GET per recuperare tutti gli articoli dell'utente loggato.
     * Chiamata: GET /api/articoli
     */
    @GetMapping
    public List<ArticoloDTO> getArticoliPersonali(Authentication authentication) {

        Long utenteId = (Long) authentication.getPrincipal();

        return articoloService.getArticoliByUtenteId(utenteId);
    }

    /**
     * Endpoint POST per creare un nuovo articolo. L'articolo sarà associato all'utente loggato.
     * Chiamata: POST /api/articoli
     */
    @PostMapping
    public ArticoloDTO createArticolo(Authentication authentication, @RequestBody ArticoloDTO articoloDTO) {

        Long utenteId = (Long) authentication.getPrincipal();

        // Passiamo al service anche utenteId per associare l'articolo al corretto utente e per un controllo di sicurezza.
        return articoloService.createArticolo(utenteId, articoloDTO);
    }

    /**
     * Endpoint PUT per aggiornare un articolo esistente. L'utente può aggiornare solo i SUOI articoli.
     * Chiamata: PUT /api/articoli/{id}
     */
    @PutMapping("/{id}")
    public ArticoloDTO updateArticolo(@PathVariable Long id, Authentication authentication, @RequestBody ArticoloDTO dettagli) {

        Long utenteId = (Long) authentication.getPrincipal();

        // Passiamo al service anche utenteId per un controllo di sicurezza.
        // L'utente può aggiornare solo i SUOI articoli, non quelli di altri.
        return articoloService.updateArticolo(id, dettagli, utenteId);
    }

    /**
     * Endpoint DELETE per eliminare un articolo esistente. L'utente può eliminare solo i SUOI articoli.
     * Chiamata: DELETE /api/articoli/{id}
     */
    @DeleteMapping("/{id}")
    public void deleteArticolo(@PathVariable Long id, Authentication authentication) {

        Long utenteId = (Long) authentication.getPrincipal();

        // Passiamo al service anche utenteId per un controllo di sicurezza.
        // L'utente può eliminare solo i SUOI articoli, non quelli di altri
        articoloService.deleteArticolo(id, utenteId);
    }

    /**
     * Endpoint GET per recuperare TUTTI gli articoli di TUTTI gli utenti.
     * Utilizzato solo in fase di test e chiamate con Postman
     */
    /*
     @GetMapping("/all")
     public List<ArticoloDTO> getAllArticoli() {
        return articoloService.getAllArticoli();
     }
     */
}

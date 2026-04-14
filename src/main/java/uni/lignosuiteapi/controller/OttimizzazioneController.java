package uni.lignosuiteapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.dto.PannelloRequestDTO;
import uni.lignosuiteapi.dto.RisultatoOttimizzazioneDTO;
import uni.lignosuiteapi.service.OttimizzazioneService;

/**
 * Controller REST per la gestione dell'ottimizzazione del taglio dei pannelli.
 */
@RestController
@RequestMapping("/api/ottimizzazione")
@CrossOrigin(origins = "*") // Permette ad Angular di leggere la risposta
public class OttimizzazioneController {

    private final OttimizzazioneService ottimizzazioneService;


    // CONSTRUCTOR INJECTION
    public OttimizzazioneController(OttimizzazioneService ottimizzazioneService) {

        this.ottimizzazioneService = ottimizzazioneService;
    }

    /**
     * Endpoint POST per calcolare l'ottimizzazione del taglio dei pannelli.
     * Chiamata: POST /api/ottimizzazione/calcola
     */
    @PostMapping("/calcola")
    public ResponseEntity<RisultatoOttimizzazioneDTO> calcolaTaglio(@RequestBody PannelloRequestDTO request) {

        // Esegue l'algoritmo di ottimizzazione tramite ASP oppure un algoritmo euristico in Java
        RisultatoOttimizzazioneDTO risultato = ottimizzazioneService.ottimizzaTaglio(request);

        return ResponseEntity.ok(risultato);
    }
}

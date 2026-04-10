package uni.lignosuiteapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.dto.PannelloRequestDTO;
import uni.lignosuiteapi.dto.RisultatoOttimizzazioneDTO;
import uni.lignosuiteapi.service.OttimizzazioneService;

@RestController
@RequestMapping("/api/ottimizzazione")
@CrossOrigin(origins = "*") // Permette ad Angular di leggere la risposta
public class OttimizzazioneController {

    @Autowired
    private OttimizzazioneService ottimizzazioneService;

    @PostMapping("/calcola")
    public ResponseEntity<RisultatoOttimizzazioneDTO> calcolaTaglio(@RequestBody PannelloRequestDTO request) {
        
        // Esegue il motore di ottimizzazione in Java puro (equivalente a TypeScript)
        RisultatoOttimizzazioneDTO risultato = ottimizzazioneService.ottimizzaTaglio(request);
        return ResponseEntity.ok(risultato);
    }
}

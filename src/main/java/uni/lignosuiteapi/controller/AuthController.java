package uni.lignosuiteapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.security.JwtUtil; // Importiamo il nostro generatore di Token
import uni.lignosuiteapi.service.UtenteService;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST per l'autenticazione e la gestione del profilo utente.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtenteService utenteService;
    private final JwtUtil jwtUtil;

    // CONSTRUCTOR INJECTION
    public AuthController(UtenteService utenteService, JwtUtil jwtUtil) {
        this.utenteService = utenteService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * REGISTRAZIONE: Pubblico.
     */
    @PostMapping("/register")
    public Utente register(@RequestBody Utente utente) {
        return utenteService.registerUser(utente);
    }

    /**
     * LOGIN: Pubblico. Restituisce l'utente e il TOKEN JWT!
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Utente credenziali) {
        // 1. Esegue il normale login tramite il service (lancia eccezione se credenziali errate)
        Utente utenteLoggato = utenteService.loginUser(credenziali);

        // 2. Genera il Token JWT crittografato basato sull'ID dell'utente
        String token = jwtUtil.generateToken(utenteLoggato.getId());

        // 3. Impacchettiamo sia i dati dell'utente che il token per il frontend
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("utente", utenteLoggato);

        return response;
    }

    /**
     * AGGIORNA PROFILO: Protetto.
     * N.B: Rimosso {id} dall'URL, usiamo "/update" e prendiamo l'ID dal token in sicurezza.
     */
    @PutMapping("/update")
    public Utente updateProfilo(Authentication authentication, @RequestBody Utente datiAggiornati) {
        Long utenteId = (Long) authentication.getPrincipal();
        return utenteService.updateUser(utenteId, datiAggiornati);
    }

    /**
     * OTTIENI PROFILO: Protetto.
     * N.B: Rimosso {id} dall'URL. Lo standard REST è chiamarlo "/me".
     */
    @GetMapping("/me")
    public Utente getUtente(Authentication authentication) {
        Long utenteId = (Long) authentication.getPrincipal();
        return utenteService.getUtenteById(utenteId);
    }
}

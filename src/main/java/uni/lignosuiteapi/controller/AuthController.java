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
     * Endpoint POST pubblico per la registrazione di un nuovo utente.
     * Chiamata: POST /api/auth/register
     */
    @PostMapping("/register")
    public Utente register(@RequestBody Utente utente) {

        return utenteService.registerUser(utente);
    }

    /**
     * Endpoint POST pubblico per il login di un utente.
     * Chiamata: POST /api/auth/login
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Utente credenziali) {

        // 1. Verifica le credenziali e ottieni i dati dell'utente loggato
        Utente utenteLoggato = utenteService.loginUser(credenziali);

        // 2. Genera un token JWT usando l'ID dell'utente come payload
        String token = jwtUtil.generateToken(utenteLoggato.getId());

        // 3. Ritorna il token e i dati dell'utente in una mappa
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("utente", utenteLoggato);

        return response;
    }

    /**
     * Endpoint PUT per aggiornare il profilo dell'utente loggato.
     * Chiamata: /api/auth/update
     */
    @PutMapping("/update")
    public Utente updateProfilo(Authentication authentication, @RequestBody Utente datiAggiornati) {

        Long utenteId = (Long) authentication.getPrincipal();

        return utenteService.updateUser(utenteId, datiAggiornati);
    }

    /**
     * Endpoint GET per recuperare i dati dell'utente loggato.
     * Chiamata: GET /api/auth/me
     */
    @GetMapping("/me")
    public Utente getUtente(Authentication authentication) {
        Long utenteId = (Long) authentication.getPrincipal();
        return utenteService.getUtenteById(utenteId);
    }
}

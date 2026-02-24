package uni.lignosuiteapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.UtenteRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UtenteRepository utenteRepository;

    @PostMapping("/register")
    public Utente register(@RequestBody Utente utente) {
        if (utenteRepository.existsByEmail(utente.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già registrata.");
        }
        return utenteRepository.save(utente);
    }

    @PostMapping("/login")
    public Utente login(@RequestBody Utente credenziali) {
        Utente utente = utenteRepository.findByEmailAndPassword(credenziali.getEmail(), credenziali.getPassword());
        if (utente == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o password errati.");
        }
        return utente; // Restituisce i dati dell'utente se il login ha successo
    }
}

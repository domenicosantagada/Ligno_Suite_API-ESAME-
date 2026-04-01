package uni.lignosuiteapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.service.UtenteService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    // Adesso inietta il Service corretto e non direttamente il Repository!
    @Autowired
    private UtenteService utenteService;

    @PostMapping("/register")
    public Utente register(@RequestBody Utente utente) {
        return utenteService.registerUser(utente);
    }

    @PostMapping("/login")
    public Utente login(@RequestBody Utente credenziali) {
        return utenteService.loginUser(credenziali);
    }

    @PutMapping("/update/{id}")
    public Utente updateProfilo(@PathVariable Long id, @RequestBody Utente datiAggiornati) {
        return utenteService.updateUser(id, datiAggiornati);
    }

    @GetMapping("/{id}")
    public Utente getUtente(@PathVariable Long id) {
        return utenteService.getUtenteById(id);
    }
}

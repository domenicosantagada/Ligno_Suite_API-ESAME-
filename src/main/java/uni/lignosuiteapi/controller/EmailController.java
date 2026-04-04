package uni.lignosuiteapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uni.lignosuiteapi.service.EmailService;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    // CONSTRUCTOR INJECTION
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // Endpoint per applicare la metodologia di replay-to
    @PostMapping("/invia-preventivo")
    public ResponseEntity<String> inviaPreventivoEmail(
            Authentication authentication, // SICUREZZA: Richiede un Token JWT valido per bloccare i bot da spam!
            @RequestParam("file") MultipartFile file,
            @RequestParam("destinatario") String destinatario,
            @RequestParam("oggetto") String oggetto,
            @RequestParam("testo") String testo,
            @RequestParam("nomeMittente") String nomeMittente,
            @RequestParam("emailMittente") String emailMittente) {

        try {
            // Nota: Non stiamo estraendo (Long) authentication.getPrincipal()
            // perché non ci serve fare query al DB, ma la sola presenza
            // di Authentication assicura che la richiesta provenga da un utente fidato.

            emailService.inviaPreventivoConAllegato(destinatario, oggetto, testo, file, nomeMittente, emailMittente);
            return ResponseEntity.ok("{\"messaggio\": \"Email inviata con successo\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"errore\": \"Impossibile inviare l'email\"}");
        }
    }
}

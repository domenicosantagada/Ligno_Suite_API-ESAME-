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

/**
 * Controller REST per la gestione dell'invio di email.
 */
@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    // CONSTRUCTOR INJECTION
    public EmailController(EmailService emailService) {

        this.emailService = emailService;
    }

    /**
     * Endpoint POST per inviare un preventivo via email.
     * Chiamata: POST /api/email/invia-preventivo
     */
    @PostMapping("/invia-preventivo")
    public ResponseEntity<String> inviaPreventivoEmail(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestParam("destinatario") String destinatario,
            @RequestParam("oggetto") String oggetto,
            @RequestParam("testo") String testo,
            @RequestParam("nomeMittente") String nomeMittente,
            @RequestParam("emailMittente") String emailMittente) {

        try {

            emailService.inviaPreventivoConAllegato(destinatario, oggetto, testo, file, nomeMittente, emailMittente);

            return ResponseEntity.ok("{\"messaggio\": \"Email inviata con successo\"}");

        } catch (Exception e) {

            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"errore\": \"Impossibile inviare l'email\"}");
        }
    }
}

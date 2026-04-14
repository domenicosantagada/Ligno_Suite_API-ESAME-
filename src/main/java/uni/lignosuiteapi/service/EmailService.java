package uni.lignosuiteapi.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

/**
 * Service che si occupa di inviare email con allegati PDF.
 * Utilizza JavaMailSender per costruire e inviare le email, e legge l'indirizzo email di sistema (da cui partono le email) dalle properties di Spring (@Value).
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String emailDiSistema;

    // CONSTRUCTOR INJECTION
    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String emailDiSistema) {
        this.mailSender = mailSender;
        this.emailDiSistema = emailDiSistema;
    }

    /**
     * Metodo che invia un'email con un allegato PDF.
     * Il parametro "destinatario" è l'indirizzo email del cliente a cui inviare il preventivo.
     * Il parametro "oggetto" è l'oggetto dell'email.
     * Il parametro "testo" è il corpo dell'email.
     * Il parametro "allegatoPdf" è il file PDF da allegare all'email (il preventivo).
     * Il parametro "nomeArtigiano" è il nome dell'artigiano che invia il preventivo, usato sia come alias che come nome del reply-to.
     * Il parametro "emailArtigiano" è l'indirizzo email dell'artigiano, usato come reply-to (così se il cliente clicca "Rispondi", la mail va all'artigiano e non al sistema).
     */
    public void inviaPreventivoConAllegato(String destinatario, String oggetto, String testo, MultipartFile allegatoPdf, String nomeArtigiano, String emailArtigiano)
            throws MessagingException, UnsupportedEncodingException {

        // Crea un'email di tipo MIME (che supporta allegati) e usa MimeMessageHelper per costruirla in modo semplice.
        MimeMessage messaggio = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(messaggio, true);

        // Imposta i campi dell'email: destinatario, oggetto, testo, mittente (con alias) e reply-to.
        helper.setTo(destinatario);
        helper.setSubject(oggetto);
        helper.setText(testo);

        // 1. MITTENTE: L'email di sistema, ma con un alias che mostra il nome dell'artigiano (es. "Mario Rossi <emailDiSistema>")
        helper.setFrom(emailDiSistema, nomeArtigiano);

        // 2. REPLY-TO: L'email dell'artigiano, così se il cliente clicca "Rispondi" la mail va all'artigiano e non al sistema
        helper.setReplyTo(emailArtigiano, nomeArtigiano);

        // 3. ALLEGATO: Aggiunge il PDF come allegato all'email, usando il nome originale del file come nome dell'allegato.
        helper.addAttachment(allegatoPdf.getOriginalFilename(), allegatoPdf);

        // 4. INVIA: Invia l'email costruita.
        mailSender.send(messaggio);
    }
}

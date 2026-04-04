package uni.lignosuiteapi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Genera una chiave crittografica sicura. In produzione si usa una stringa fissa salvata in application.properties
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 86400000; // 1 giorno in millisecondi

    // 1. Crea il token inserendo l'ID dell'utente al suo interno (come 'Subject')
    public String generateToken(Long utenteId) {
        return Jwts.builder()
                .setSubject(String.valueOf(utenteId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // 2. Estrae l'ID dell'utente dal token
    public Long extractUtenteId(String token) {
        String subject = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        return Long.parseLong(subject);
    }

    // 3. Controlla se il token è valido e non scaduto
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

package uni.lignosuiteapi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Component che si occupa di generare, validare ed estrarre informazioni dai token JWT.
 * In questo caso, il token contiene solo l'ID dell'utente (come "Subject") e una data di scadenza.
 */
@Component
public class JwtUtil {

    // La chiave segreta usata per firmare i token.
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // La durata del token (in questo caso, 1 giorno = 86400000 millisecondi)
    private final long EXPIRATION_TIME = 86400000;

    // 1. Genera un token JWT a partire dall'ID dell'utente
    public String generateToken(Long utenteId) {
        return Jwts.builder()
                .setSubject(String.valueOf(utenteId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // 2. Estrae l'ID dell'utente dal token JWT
    public Long extractUtenteId(String token) {
        String subject = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        return Long.parseLong(subject);
    }

    // 3. Valida il token JWT (controlla firma e scadenza)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

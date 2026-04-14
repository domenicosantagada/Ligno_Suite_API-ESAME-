package uni.lignosuiteapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Component che serve a intercettare ogni richiesta HTTP, controllare se c'è un token JWT
 * valido nell'header "Authorization" e, se sì, autorizzare l'utente per quella singola richiesta.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // CONSTRUCTOR INJECTION
    public JwtFilter(JwtUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Cerca l'header "Authorization"
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Rimuove la parola "Bearer "

            if (jwtUtil.validateToken(token)) {
                // Se il token è valido, estraiamo l'ID
                Long utenteId = jwtUtil.extractUtenteId(token);

                // Diciamo a Spring Security: "L'utente con questo ID è autorizzato per questa singola richiesta"
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(utenteId, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Passa la richiesta al controller
        filterChain.doFilter(request, response);
    }
}

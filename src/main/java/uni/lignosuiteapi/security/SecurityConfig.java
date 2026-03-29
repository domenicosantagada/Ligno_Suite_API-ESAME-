package uni.lignosuiteapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Bean per criptare le password (usato quando si salvano nel DB)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        http
                // Disabilitiamo il CSRF perché è una REST API
                // (altrimenti le richieste POST possono essere bloccate)
                .csrf(AbstractHttpConfigurer::disable)

                // Permettiamo tutte le richieste senza autenticazione
                // (non c'è controllo di sicurezza lato backend)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        // Costruisce e restituisce la configurazione di sicurezza
        return http.build();
    }

}

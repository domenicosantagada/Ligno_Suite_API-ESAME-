package uni.lignosuiteapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.dao.UtenteDao;
import uni.lignosuiteapi.model.Utente;

/**
 * Service per la gestione degli utenti.
 */
@Service
public class UtenteService {

    // Il Service chiama il DAO per accedere ai dati.
    @Autowired
    private UtenteDao utenteDao;

    // Metodo per registrare un nuovo utente.
    public Utente registerUser(Utente utente) {

        // Controlla se nel database esiste già un utente con la stessa email
        if (utenteDao.findByEmail(utente.getEmail()) != null) {

            /**
             * Se l'email esiste già, viene lanciata un'eccezione HTTP.
             *
             * HttpStatus.CONFLICT (409)
             * Indica che c'è un conflitto con lo stato attuale della risorsa.
             *
             * Il messaggio verrà inviato al frontend.
             */
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già registrata.");
        }

        // Salva l'utente nel database e restituisce l'utente appena creato.
        return utenteDao.save(utente);
    }

    // Metodo per autenticare utente (Login)
    public Utente loginUser(Utente utenteCredenziali) {

        Utente utente = utenteDao.findByEmail(utenteCredenziali.getEmail());

        /**
         * Verifica due condizioni:
         *
         * 1) L'utente non esiste
         * 2) La password inserita non corrisponde a quella salvata
         *
         * Se una delle due è vera, il login fallisce.
         */
        if (utente == null || !utente.getPassword().equals(utenteCredenziali.getPassword())) {

            /**
             * HttpStatus.UNAUTHORIZED (401)
             * Indica che le credenziali fornite non sono valide.
             */
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o password errati.");
        }

        return utente;
    }
}

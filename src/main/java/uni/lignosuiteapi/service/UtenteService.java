package uni.lignosuiteapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.UtenteRepository;

import java.util.List;

/**
 * Service per gestione utenti
 */
@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * Registrazione utente
     */
    public Utente registerUser(Utente utente) {
        if (utenteRepository.findByEmail(utente.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già registrata.");
        }

        utente.setPassword(passwordEncoder.encode(utente.getPassword()));
        return utenteRepository.save(utente);
    }

    /**
     * Autenticazione utente
     */
    public Utente loginUser(Utente utenteCredenziali) {
        Utente utente = utenteRepository
                .findByEmail(utenteCredenziali.getEmail())
                .orElse(null);

        if (utente == null ||
                !passwordEncoder.matches(utenteCredenziali.getPassword(), utente.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o password errati.");
        }

        return utente;
    }

    /**
     * Aggiornamento profilo utente
     */
    public Utente updateUser(Long id, Utente datiAggiornati) {

        Utente utenteEsistente = utenteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // Verifica unicità email
        if (!utenteEsistente.getEmail().equals(datiAggiornati.getEmail()) &&
                utenteRepository.findByEmail(datiAggiornati.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già in uso.");
        }

        utenteEsistente.setNomeAzienda(datiAggiornati.getNomeAzienda());
        utenteEsistente.setNome(datiAggiornati.getNome());
        utenteEsistente.setNomeTitolare(datiAggiornati.getNomeTitolare());
        utenteEsistente.setCognomeTitolare(datiAggiornati.getCognomeTitolare());
        utenteEsistente.setTelefono(datiAggiornati.getTelefono());
        utenteEsistente.setPartitaIva(datiAggiornati.getPartitaIva());
        utenteEsistente.setCodiceFiscale(datiAggiornati.getCodiceFiscale());
        utenteEsistente.setIndirizzo(datiAggiornati.getIndirizzo());
        utenteEsistente.setCitta(datiAggiornati.getCitta());
        utenteEsistente.setCap(datiAggiornati.getCap());
        utenteEsistente.setProvincia(datiAggiornati.getProvincia());
        utenteEsistente.setLogoBase64(datiAggiornati.getLogoBase64());
        utenteEsistente.setEmail(datiAggiornati.getEmail());

        // Aggiorna password solo se presente
        if (datiAggiornati.getPassword() != null && !datiAggiornati.getPassword().isEmpty()) {
            utenteEsistente.setPassword(passwordEncoder.encode(datiAggiornati.getPassword()));
        }

        return utenteRepository.save(utenteEsistente);
    }

    /**
     * Lista utenti
     */
    public List<Utente> getAllUtenti() {
        return utenteRepository.findAll();
    }

    /**
     * Recupero utente per ID
     */
    public Utente getUtenteById(Long id) {
        return utenteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));
    }
}

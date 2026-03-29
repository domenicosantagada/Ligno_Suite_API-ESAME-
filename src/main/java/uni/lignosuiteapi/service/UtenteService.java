package uni.lignosuiteapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.dao.UtenteDao;
import uni.lignosuiteapi.model.Utente;

import java.util.List;

/**
 * Service per la gestione degli utenti.
 */
@Service
public class UtenteService {

    // Il Service chiama il DAO per accedere ai dati.
    @Autowired
    private UtenteDao utenteDao;

    // Il Service usa PasswordEncoder per criptare le password prima di salvarle e per verificare le password durante il login.
    @Autowired
    private PasswordEncoder passwordEncoder;


    // Metodo per registrare un nuovo utente
    public Utente registerUser(Utente utente) {

        // Non permettiamo a due utenti di usare la stessa email
        if (utenteDao.findByEmail(utente.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già registrata.");
        }

        // Hashing della password
        String pwVisibile = utente.getPassword();
        String pwHash = passwordEncoder.encode(pwVisibile);
        utente.setPassword(pwHash);

        return utenteDao.save(utente);
    }

    // Metodo per loggare un utente
    public Utente loginUser(Utente utenteCredenziali) {

        // Mi creo un utente di tipoligia UtenteDao popolato con i dati del database
        // quindi in questo caso faccio un findbyemail per cercare nel db l'utente con quell'email
        // recupero i dati dal db e popolo l'untente temporaneo
        Utente utente = utenteDao.findByEmail(utenteCredenziali.getEmail());

        // Se l'utente non esiste, o se la password NON matcha, lanciamo subito errore 401!
        if (utente == null || !passwordEncoder.matches(utenteCredenziali.getPassword(), utente.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o password errati.");
        }

        // Se arriva a questa riga, significa che utente esiste e la password è corretta
        return utente;
    }

    // Metodo per aggiornare un profilo utente
    public Utente updateUser(Long id, Utente datiAggiornati) {

        Utente utenteEsistente = utenteDao.findById(id);

        if (utenteEsistente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato");
        }

        if (!utenteEsistente.getEmail().equals(datiAggiornati.getEmail())) {
            if (utenteDao.findByEmail(datiAggiornati.getEmail()) != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Questa email è già in uso da un altro utente.");
            }
        }

        utenteEsistente.setNomeAzienda(datiAggiornati.getNomeAzienda());
        utenteEsistente.setNome(datiAggiornati.getNome()); // <- CORRETTO IL REFUSO QUI
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

        // Se l'utente decide di cambiare password durante l'aggiornamento, la criptiamo!
        if (datiAggiornati.getPassword() != null && !datiAggiornati.getPassword().isEmpty()) {
            String nuovaPwHash = passwordEncoder.encode(datiAggiornati.getPassword());
            utenteEsistente.setPassword(nuovaPwHash);
        }

        return utenteDao.update(utenteEsistente);
    }

    // Metodo per recuperare tutti gli utenti
    public List<Utente> getAllUtenti() {
        return utenteDao.findAll();
    }

    // Metodo per recuperare un singolo utente dal suo ID
    public Utente getUtenteById(Long id) {
        return utenteDao.findById(id);
    }
}

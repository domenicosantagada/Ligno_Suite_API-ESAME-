package uni.lignosuiteapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.UtenteRepository;

import java.util.List;

/**
 * Service che si occupa di gestire la logica di business per gli Utenti.
 * Si occupa di registrazione, login, aggiornamento profilo e recupero dati utente.
 */
@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    // CONSTRUCTOR INJECTION
    public UtenteService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Metodo che si occupa di registrare un nuovo utente.
     */
    public Utente registerUser(Utente utente) {

        // 1. Verifica che l'email non sia già registrata (unicità email)
        if (utenteRepository.findByEmail(utente.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già registrata.");
        }

        // 2. Cripta la password prima di salvarla nel database
        utente.setPassword(passwordEncoder.encode(utente.getPassword()));

        // 3. Salva l'utente nel database e ritorna l'utente salvato (con ID generato)
        return utenteRepository.save(utente);
    }

    /**
     * Metodo che si occupa di autenticare un utente e restituire i suoi dati se le credenziali sono corrette.
     */
    public Utente loginUser(Utente utenteCredenziali) {

        // 1. Normalizza l'email (rimuove spazi e converte in minuscolo) per evitare problemi di case sensitivity o spazi accidentali
        String email = utenteCredenziali.getEmail().trim().toLowerCase();

        // 2. Recupera l'utente dal database usando l'email normalizzata
        Utente utente = utenteRepository.findByEmail(email).orElse(null);

        // 3. Verifica che l'utente esista e che la password fornita corrisponda a quella salvata (usando passwordEncoder.matches() per confrontare la password in chiaro con quella criptata)
        if (utente == null || !passwordEncoder.matches(utenteCredenziali.getPassword(), utente.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o password errati.");
        }

        // 4. Se le credenziali sono corrette, ritorna i dati dell'utente (escludendo la password)
        return utente;
    }

    /**
     * Metodo che si occupa di aggiornare i dati di un utente esistente, identificato dal suo ID, con i dati forniti nel DTO.
     */
    public Utente updateUser(Long id, Utente datiAggiornati) {

        // 1. Verifichiamo che l'utente esista (altrimenti non possiamo aggiornarlo)
        Utente utenteEsistente = utenteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // 2. LOGICA DI BUSINESS: Verifichiamo che l'utente abbia il permesso di aggiornare questo profilo (IDOR)
        if (!utenteEsistente.getEmail().equals(datiAggiornati.getEmail()) &&
                utenteRepository.findByEmail(datiAggiornati.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già in uso.");
        }

        // 3. Aggiorniamo i campi dell'utente esistente con i nuovi dati (esclusa la password, che gestiamo a parte)
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

        // 4. Se è stata fornita una nuova password (non vuota), la criptiamo e la aggiorniamo. Altrimenti lasciamo quella esistente.
        if (datiAggiornati.getPassword() != null && !datiAggiornati.getPassword().isEmpty()) {
            // Se la password NON inizia per "$2a$" (il prefisso di BCrypt), significa che l'utente
            // ha digitato una password nuova in chiaro. Allora la criptiamo.
            // Altrimenti la ignoriamo perché è già criptata!
            if (!datiAggiornati.getPassword().startsWith("$2a$")) {
                utenteEsistente.setPassword(passwordEncoder.encode(datiAggiornati.getPassword()));
            }
        }

        // 5. Salviamo l'utente aggiornato nel database e ritorniamo i dati aggiornati (escludendo la password)
        return utenteRepository.save(utenteEsistente);
    }

    /**
     * Metodo che ritorna la lista di tutti gli utenti registrati nel sistema.
     */
    public List<Utente> getAllUtenti() {

        return utenteRepository.findAll();
    }

    /**
     * Metodo che ritorna i dati di un utente specifico, identificato dal suo ID.
     */
    public Utente getUtenteById(Long id) {

        // Recupera l'utente dal database usando il suo ID, o lancia un 404 se non esiste
        return utenteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));
    }
}

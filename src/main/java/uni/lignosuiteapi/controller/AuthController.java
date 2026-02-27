package uni.lignosuiteapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.UtenteRepository;

/**
 * Controller per la gestione dell'autenticazione (Login, Registrazione) e del profilo utente.
 * * @RestController: Indica a Spring Boot che questa classe è un controller REST.
 * Ogni metodo restituirà direttamente i dati nel corpo della risposta (solitamente in formato JSON),
 * senza dover renderizzare una vista HTML.
 * * @RequestMapping("/api/auth"): Definisce il prefisso URL per tutte le rotte di questo controller.
 * * @CrossOrigin: Permette le richieste Cross-Origin (CORS). Essendo il frontend (Angular) in esecuzione
 * sulla porta 4200 e il backend su un'altra, il browser bloccherebbe le richieste per sicurezza.
 * Questa annotazione autorizza il frontend a comunicare con il backend.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    /**
     * @Autowired: Implementa la "Dependency Injection" (Iniezione delle dipendenze).
     * Spring Boot crea automaticamente un'istanza di UtenteRepository e la assegna a questa variabile.
     * Ci permette di comunicare con il database senza dover scrivere query SQL manuali.
     */
    @Autowired
    private UtenteRepository utenteRepository;

    /**
     * Endpoint per la REGISTRAZIONE di un nuovo utente.
     * Metodo HTTP: POST (utilizzato per creare nuove risorse).
     * * @RequestBody: Prende il JSON inviato dal client (frontend) nel corpo della richiesta HTTP
     * e lo converte (deserializza) automaticamente in un oggetto Java di tipo 'Utente'.
     */
    @PostMapping("/register")
    public Utente register(@RequestBody Utente utente) {
        // Controllo validità: Verifica se nel database esiste già un utente con questa email
        if (utenteRepository.existsByEmail(utente.getEmail())) {
            // Se l'email esiste, blocca l'operazione e lancia un'eccezione con codice 409 (CONFLICT)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già registrata.");
        }
        // Se i controlli sono superati, salva l'utente nel database e restituisce l'utente salvato (id assegnato automaticamente dal db)
        return utenteRepository.save(utente);
    }

    /**
     * Endpoint per il LOGIN dell'utente.
     * Metodo HTTP: POST (si usa POST e non GET per motivi di sicurezza, affinché le credenziali
     * viaggino nel corpo della richiesta e non nell'URL in chiaro).
     */
    @PostMapping("/login")
    public Utente login(@RequestBody Utente credenziali) {
        // Cerca nel database un utente che faccia match esatto sia per email che per password
        Utente utente = utenteRepository.findByEmailAndPassword(credenziali.getEmail(), credenziali.getPassword());

        // Se non viene trovato nulla, significa che l'email o la password sono errate
        if (utente == null) {
            // Lancia un'eccezione con codice HTTP 401 (UNAUTHORIZED - Non autorizzato)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o password errati.");
        }
        // Restituisce i dati dell'utente se il login ha successo (il frontend di solito usa questi
        // dati per mantenere la sessione attiva, ad esempio salvandoli nel LocalStorage)
        return utente;
    }

    /**
     * Endpoint per l'AGGIORNAMENTO dei dati del profilo aziendale.
     * Metodo HTTP: PUT (utilizzato convenzionalmente per aggiornare/sostituire una risorsa esistente).
     * * @PathVariable: Estrae il valore '{id}' dall'URL (es. /api/auth/update/1 estrarrà l'ID 1).
     */
    @PutMapping("/update/{id}")
    public Utente updateProfilo(@PathVariable Long id, @RequestBody Utente datiAggiornati) {

        // 1. RECUPERO UTENTE: Cerca l'utente nel DB tramite il suo ID.
        // Se l'ID non esiste, lancia un'eccezione 404 (NOT_FOUND).
        Utente utenteEsistente = utenteRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato")
        );

        // Controlla se l'utente sta cercando di cambiare la sua email.
        // Se la vecchia email (nel DB) è diversa da quella nuova (inviata dal frontend)...
        if (!utenteEsistente.getEmail().equals(datiAggiornati.getEmail())) {

            // ...controlla che la nuova email non sia già utilizzata da qualcun altro!
            // Se la nuova email esiste già nel DB, blocchiamo tutto con un errore 409 (CONFLICT).
            if (utenteRepository.existsByEmail(datiAggiornati.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Questa email è già in uso da un altro utente.");
            }
        }

        // 2. AGGIORNAMENTO DATI: Sovrascrive i vecchi dati con quelli nuovi ricevuti dal frontend.
        // Si aggiornano solo campi specifici per evitare che l'utente modifichi dati sensibili non previsti.
        utenteEsistente.setNomeAzienda(datiAggiornati.getNomeAzienda());

        // Se l'utente cambia il nome azienda, aggiorniamo anche il 'nome' generico dell'utente
        utenteEsistente.setNome(datiAggiornati.getNomeAzienda());

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

        // 3. SALVATAGGIO: Salva l'entità modificata nel database.
        return utenteRepository.save(utenteEsistente);
    }
}

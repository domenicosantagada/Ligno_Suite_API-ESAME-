package uni.lignosuiteapi.controller;

// Import delle classi necessarie al funzionamento del controller

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.model.Preventivo;
import uni.lignosuiteapi.service.PreventivoService;

import java.util.List;

/**
 * Controller REST per la gestione dei preventivi.
 * <p>
 * Questo controller espone le API che permettono al frontend
 * di eseguire operazioni CRUD sui preventivi:
 * <p>
 * - Recuperare tutti i preventivi
 * - Creare un nuovo preventivo
 * - Aggiornare un preventivo
 * - Eliminare un preventivo
 * - Ottenere il prossimo numero preventivo disponibile
 *
 * @RestController Indica a Spring Boot che questa classe è un controller REST.
 * I metodi restituiscono direttamente dati JSON come risposta HTTP.
 * @RequestMapping("/api/preventivi") Definisce il prefisso di tutte le rotte di questo controller.
 * Tutti gli endpoint saranno accessibili tramite /api/preventivi.
 * @CrossOrigin Permette al frontend Angular (che gira su localhost:4200)
 * di effettuare richieste HTTP verso questo backend.
 */
@RestController
@RequestMapping("/api/preventivi")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost"})
public class PreventiviController {

    /**
     * @Autowired Permette a Spring di iniettare automaticamente l'oggetto PreventivoDao.
     * In questo modo possiamo usare il DAO per accedere al database
     */
    @Autowired
    private PreventivoService preventivoService;

    @PostMapping
    public Preventivo createPreventivo(@RequestParam Long utenteId, @RequestBody Preventivo preventivo) {
        return preventivoService.createPreventivo(utenteId, preventivo);
    }

    @PutMapping("/{id}")
    public Preventivo updatePreventivo(@PathVariable Long id, @RequestParam Long utenteId, @RequestBody Preventivo preventivo) {
        return preventivoService.updatePreventivo(id, preventivo, utenteId);
    }

    /**
     * =========================
     * OTTENERE TUTTI I PREVENTIVI
     * =========================
     * <p>
     * Endpoint per recuperare tutti i preventivi
     * appartenenti ad uno specifico utente.
     *
     * @GetMapping Gestisce richieste HTTP GET all'URL:
     * /api/preventivi
     * @RequestParam Permette di leggere un parametro dalla query dell'URL.
     * <p>
     * Esempio:
     * GET /api/preventivi?utenteId=1
     */
    @GetMapping
    public List<Preventivo> getAllPreventivi(@RequestParam Long utenteId) {

        /**
         * Il Service chiama il DAO per recuperare i preventivi dal database.
         */
        return preventivoService.getAllPreventivi(utenteId);
    }


    /**
     * =========================
     * ELIMINARE UN PREVENTIVO
     * =========================
     * <p>
     * Endpoint per eliminare un preventivo dal database.
     *
     * @DeleteMapping("/{id}") Gestisce richieste HTTP DELETE all'URL:
     * /api/preventivi/{id}
     */
    @DeleteMapping("/{id}")
    public void deletePreventivo(@PathVariable Long id, @RequestParam Long utenteId) {

        /**
         * Il Service chiama il DAO per eliminare il preventivo dal database.
         */
        preventivoService.deletePreventivo(id, utenteId);
    }

    /**
     * =========================
     * OTTENERE IL PROSSIMO NUMERO PREVENTIVO
     * =========================
     * <p>
     * Endpoint che restituisce il prossimo numero
     * di preventivo disponibile per un utente.
     *
     * @GetMapping("/next-number") Esempio chiamata:
     * GET /api/preventivi/next-number?utenteId=1
     */
    @GetMapping("/next-number")
    public Long getNextInvoiceNumber(@RequestParam Long utenteId) {
        /**
         * Il Service chiama il DAO per ottenere il prossimo numero preventivo.
         */
        return preventivoService.getNextInvoiceNumber(utenteId);
    }

    /**
     * =========================
     * OTTENERE PREVENTIVI PER IL CLIENTE
     * =========================
     * Endpoint per recuperare tutti i preventivi destinati a una specifica email.
     * Esempio: GET /api/preventivi/cliente?email=mario@email.it
     */
    @GetMapping("/cliente")
    public List<Preventivo> getPreventiviPerCliente(@RequestParam String email) {
        return preventivoService.getPreventiviPerCliente(email);
    }
}

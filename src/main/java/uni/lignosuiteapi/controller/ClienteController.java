package uni.lignosuiteapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.model.Cliente;
import uni.lignosuiteapi.repository.ClienteRepository;

import java.util.List;

/**
 * Controller REST per la gestione della rubrica clienti.
 * Gestisce le operazioni CRUD (Creazione, Lettura, Aggiornamento, Cancellazione).
 * * @RestController: Indica che i metodi di questa classe restituiscono i dati direttamente
 * nel corpo della risposta HTTP (formato JSON), senza usare viste HTML.
 *
 * @RequestMapping("/api/clienti"): Definisce il percorso di base per tutti gli endpoint di questa classe.
 * @CrossOrigin: Abilita la comunicazione con il frontend (Angular su porta 4200), evitando blocchi CORS.
 */
@RestController
@RequestMapping("/api/clienti")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    /**
     * @Autowired: Spring si occupa di "iniettare" automaticamente l'istanza del repository.
     * In questo modo possiamo usare i metodi per interagire col database (save, delete, find, ecc.)
     * senza dover scrivere a mano le query SQL.
     */
    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Endpoint per la LETTURA (Read) dei clienti associati a un utente specifico.
     * Metodo HTTP: GET
     * * @RequestParam Long utenteId: Legge il parametro dalla stringa di interrogazione dell'URL (Query String).
     */
    @GetMapping
    public List<Cliente> getAllClienti(@RequestParam Long utenteId) {
        // Usa un metodo personalizzato del repository per filtrare i clienti in base all'ID dell'utente loggato
        return clienteRepository.findByUtenteId(utenteId);
    }

    /**
     * Endpoint per la CREAZIONE (Create) di un nuovo cliente.
     * Metodo HTTP: POST
     * * @RequestBody Cliente cliente: Converte il JSON ricevuto dal frontend in un oggetto Java 'Cliente'.
     */
    @PostMapping
    public Cliente createCliente(@RequestBody Cliente cliente) {
        // Salva il nuovo cliente nel database. L'ID verrà generato e assegnato automaticamente (Auto Increment).
        // Restituisce l'oggetto appena salvato completo di ID.
        return clienteRepository.save(cliente);
    }

    /**
     * Endpoint per l'AGGIORNAMENTO (Update) di un cliente esistente.
     * Metodo HTTP: PUT (o PATCH in alcuni contesti, ma PUT è standard per sostituire interamente l'oggetto)
     * * @PathVariable Long id: Cattura l'ID del cliente passato direttamente nel percorso URL (es. /api/clienti/5).
     *
     * @RequestBody Cliente cliente: Contiene i nuovi dati del cliente modificati dall'utente nel frontend.
     */
    @PutMapping("/{id}")
    public Cliente updateCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        // Sicurezza/Coerenza: Ci assicuriamo che l'ID dell'oggetto da salvare sia esattamente
        // quello passato nell'URL. In questo modo Spring Data JPA capisce che deve fare un UPDATE
        // di un record esistente e non un INSERT di un nuovo record.
        cliente.setId(id);

        // Salva le modifiche nel database e restituisce il cliente aggiornato
        return clienteRepository.save(cliente);
    }

    /**
     * Endpoint per l'ELIMINAZIONE (Delete) di un cliente.
     * Metodo HTTP: DELETE
     * * @PathVariable Long id: Prende l'ID dall'URL (es. /api/clienti/5) per capire quale record eliminare.
     */
    @DeleteMapping("/{id}")
    public void deleteCliente(@PathVariable Long id) {
        // Elimina fisicamente il record del cliente dal database corrispondente a quell'ID.
        // Poiché il tipo di ritorno è 'void', il backend risponderà semplicemente con uno status HTTP 200 (OK)
        // senza alcun corpo (body) nella risposta, confermando l'avvenuta eliminazione.
        clienteRepository.deleteById(id);
    }
}

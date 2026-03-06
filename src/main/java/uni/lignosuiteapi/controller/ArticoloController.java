package uni.lignosuiteapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.model.Articolo;
import uni.lignosuiteapi.model.Utente;
import uni.lignosuiteapi.repository.ArticoloRepository;
import uni.lignosuiteapi.repository.UtenteRepository;

import java.util.List;


/**
 * Controller per la gestione degli articoli.
 */

@RestController
@RequestMapping("/api/articoli")
@CrossOrigin(origins = "http://localhost:4200")
public class ArticoloController {

    // @Autowired: Inietta l'istanza del repository per interagire con il database senza dover scrivere query SQL manuali.
    @Autowired
    private ArticoloRepository articoloRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    /**
     * Metodo HTTP GET per recuperare tutti gli articoli creati da un utente specifico.
     *
     * @param utenteId: ID dell'utente che ha creato l'articolo.
     * @return: Restituisce tutti gli articoli creati da un utente specifico.
     */
    @GetMapping("/utente/{utenteId}")
    public List<Articolo> getArticoliByUtenteId(@PathVariable Long utenteId) {
        return articoloRepository.findByUtenteId(utenteId);
    }

    /**
     * Metodo HTTP POST per creare un nuovo articolo associato a un utente specifico.
     *
     * @param utenteId: ID dell'utente che ha creato l'articolo.
     * @param articolo: Dettagli del nuovo articolo da creare.
     */
    @PostMapping("/utente/{utenteId}")
    public Articolo createArticolo(@PathVariable Long utenteId, @RequestBody Articolo articolo) {
        Utente utente = utenteRepository.findById(utenteId).orElseThrow(() -> new RuntimeException("Utente non trovato"));
        articolo.setUtente(utente);
        return articoloRepository.save(articolo);
    }

    /**
     * Metodo HTTP PUT per aggiornare i dettagli di un articolo esistente.
     *
     * @param id:       ID dell'articolo da aggiornare.
     * @param dettagli: Nuovi dettagli dell'articolo.
     * @return: Restituisce l'articolo aggiornato.
     */
    @PutMapping("/{id}")
    public Articolo updateArticolo(@PathVariable Long id, @RequestBody Articolo dettagli) {

        // Creiamo un articolo temporaneo con i dettagli aggiornati
        // In caso non troviamo l'articolo, lanciamo un'eccezione
        Articolo articolo = articoloRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Articolo non trovato"));

        articolo.setNome(dettagli.getNome());
        articolo.setPrezzoAcquisto(dettagli.getPrezzoAcquisto());
        articolo.setFornitore(dettagli.getFornitore());
        articolo.setDataAcquisto(dettagli.getDataAcquisto());
        articolo.setUnitaMisura(dettagli.getUnitaMisura());

        return articoloRepository.save(articolo);
    }

    /**
     * Metodo HTTP DELETE per eliminare un articolo specifico.
     *
     * @param id: ID dell'articolo da eliminare.
     */
    @DeleteMapping("/{id}")
    public void deleteArticolo(@PathVariable Long id) {
        articoloRepository.deleteById(id);
    }
}

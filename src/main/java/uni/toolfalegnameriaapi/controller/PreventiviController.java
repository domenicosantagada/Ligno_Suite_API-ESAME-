package uni.toolfalegnameriaapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uni.toolfalegnameriaapi.model.Preventivo;
import uni.toolfalegnameriaapi.repository.PreventivoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/preventivi")
@CrossOrigin(origins = "http://localhost:4200")
// Fondamentale: permette ad Angular di chiamare queste API senza errori CORS
public class PreventiviController {

    @Autowired
    private PreventivoRepository preventivoRepository;

    // Metodo per RECUPERARE tutti i preventivi (GET)
    @GetMapping
    public List<Preventivo> getAllPreventivi() {
        return preventivoRepository.findAll();
    }

    // Metodo per SALVARE o AGGIORNARE un preventivo (POST)
    @PostMapping
    public Preventivo savePreventivo(@RequestBody Preventivo invoice) {
        // Il metodo save() fa sia la INSERT (se non esiste) che l'UPDATE (se esiste già)
        return preventivoRepository.save(invoice);
    }

    // Metodo per ELIMINARE un preventivo (DELETE)
    @DeleteMapping("/{id}")
    public void deletePreventivo(@PathVariable String id) {
        preventivoRepository.deleteById(id);
    }
}
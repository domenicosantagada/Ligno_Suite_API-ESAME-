package uni.lignosuiteapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uni.lignosuiteapi.dto.PreventivoDTO;
import uni.lignosuiteapi.dto.PreventivoListDTO;
import uni.lignosuiteapi.service.PreventivoService;

import java.util.List;

@RestController
@RequestMapping("/api/preventivi")
public class PreventiviController {

    private final PreventivoService preventivoService;

    public PreventiviController(PreventivoService preventivoService) {
        this.preventivoService = preventivoService;
    }

    // LISTA LEGGERA PER LA TABELLA
    @GetMapping
    public List<PreventivoListDTO> getAllPreventivi(Authentication authentication) {
        Long utenteId = (Long) authentication.getPrincipal();
        return preventivoService.getAllPreventivi(utenteId);
    }

    // PREVENTIVO COMPLETO
    @GetMapping("/{id}")
    public PreventivoDTO getPreventivoById(@PathVariable Long id, Authentication authentication) {
        Long utenteId = (Long) authentication.getPrincipal();
        return preventivoService.getPreventivoById(id, utenteId);
    }

    @PostMapping
    public PreventivoDTO createPreventivo(Authentication authentication, @RequestBody PreventivoDTO preventivoDTO) {
        Long utenteId = (Long) authentication.getPrincipal();
        return preventivoService.createPreventivo(utenteId, preventivoDTO);
    }

    @PutMapping("/{id}")
    public PreventivoDTO updatePreventivo(@PathVariable Long id, Authentication authentication, @RequestBody PreventivoDTO preventivoDTO) {
        Long utenteId = (Long) authentication.getPrincipal();
        return preventivoService.updatePreventivo(id, preventivoDTO, utenteId);
    }

    @DeleteMapping("/{id}")
    public void deletePreventivo(@PathVariable Long id, Authentication authentication) {
        Long utenteId = (Long) authentication.getPrincipal();
        preventivoService.deletePreventivo(id, utenteId);
    }
}

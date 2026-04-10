package uni.lignosuiteapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST per utilizzare API Google Gemini
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final String geminiApiKey;
    private final RestTemplate restTemplate;

    // CONSTRUCTOR INJECTION: Iniettiamo sia la chiave API che un'istanza riutilizzabile di RestTemplate
    public AiController(@Value("${gemini.api.key}") String geminiApiKey) {
        this.geminiApiKey = geminiApiKey;
        // Creiamo un'unica istanza di RestTemplate da riutilizzare per tutte le richieste
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/genera-descrizione")
    public ResponseEntity<Map<String, String>> generaDescrizione(@RequestBody Map<String, String> request) {
        String inputTesto = request.get("testo");

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "Sei un falegname esperto. L'utente ti darà una breve nota. " +
                "Trasformala in una descrizione professionale ed elegante da inserire in un preventivo. " +
                "Sii conciso ma tecnico. Evita asterischi o formattazioni markdown, scrivi solo testo normale. " +
                "Testo utente: " + inputTesto;

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> parts = Map.of("parts", List.of(textPart));
        Map<String, Object> body = Map.of("contents", List.of(parts));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> resParts = (List<Map<String, Object>>) content.get("parts");
            String testoGenerato = (String) resParts.get(0).get("text");

            Map<String, String> result = new HashMap<>();
            result.put("descrizioneMigliorata", testoGenerato.trim());

            return ResponseEntity.ok(result);

        } catch (HttpServerErrorException.ServiceUnavailable e) {
            // Se Google è sovraccarico, restituiamo un errore 503 formattato in JSON
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "I server dell'Intelligenza Artificiale sono momentaneamente sovraccarichi. Riprova tra un minuto!");
            return ResponseEntity.status(503).body(errorResponse);

        } catch (Exception e) {
            // Per altri errori, restituiamo un 500 generico formattato in JSON
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Errore durante la generazione AI: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

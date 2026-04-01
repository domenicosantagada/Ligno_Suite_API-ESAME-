package uni.lignosuiteapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "articolo")
public class Articolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === Attributi principali dell'articolo ===

    @Column(nullable = false) // Assicura che il nome non sia mai nullo nel DB
    private String nome;

    @Column(columnDefinition = "TEXT") // Permette descrizioni lunghe
    private String descrizione;

    @Column(nullable = false)
    private Double prezzoAcquisto;

    private String fornitore;

    private String unitaMisura;

    private LocalDate dataAcquisto;

    // === Relazione con Utente (Molti a Uno) ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    // === Metodi di utilità (Automatizzati tramite Lifecycle Callbacks) ===

    /**
     * Normalizza i dati testuali dell'articolo.
     * Verrà richiamato IN AUTOMATICO da Hibernate!
     */
    @PrePersist // Lanciato automaticamente prima di una INSERT (nuovo salvataggio)
    @PreUpdate  // Lanciato automaticamente prima di una UPDATE (modifica)
    public void formattaDati() {
        if (this.nome != null) {
            this.nome = capitalizzaParole(this.nome);
        }
        if (this.fornitore != null) {
            this.fornitore = capitalizzaParole(this.fornitore);
        }
    }

    /**
     * Converte una stringa in formato "Title Case".
     * Rimane invariato!
     */
    private String capitalizzaParole(String str) {
        str = str.trim();
        if (str.isEmpty()) return str;

        String[] parole = str.split("\\s+");
        StringBuilder risultato = new StringBuilder();

        for (String parola : parole) {
            if (!parola.isEmpty()) {
                risultato.append(Character.toUpperCase(parola.charAt(0)))
                        .append(parola.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return risultato.toString().trim();
    }
}

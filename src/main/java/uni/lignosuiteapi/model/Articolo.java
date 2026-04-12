package uni.lignosuiteapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Entità JPA collegata alla tabella "articolo" nel database.
 * Rappresenta un articolo acquistato dal falegname
 */
@Data
@Entity
@Table(name = "articolo")
public class Articolo {

    // DATTA PRINCIPALI

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(nullable = false)
    private Double prezzoAcquisto;

    private String fornitore;

    private String unitaMisura;

    private LocalDate dataAcquisto;

    // Relazione ManyToOne con Utente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;


    // METODI DI UTILITY

    /**
     * Metodo che viene chiamato automaticamente da Hibernate prima di salvare (PrePersist) o aggiornare (PreUpdate) un articolo.
     * Si occupa di formattare i campi "nome" e "fornitore" in formato "Title Case" (prima lettera maiuscola, resto minuscolo).
     */
    @PrePersist
    @PreUpdate
    public void formattaDati() {
        if (this.nome != null) {
            this.nome = capitalizzaParole(this.nome);
        }
        if (this.fornitore != null) {
            this.fornitore = capitalizzaParole(this.fornitore);
        }
    }

    /**
     * Metodo per capitalizzare ogni parola in una stringa, mettendo la prima lettera in maiuscolo e il resto in minuscolo.
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

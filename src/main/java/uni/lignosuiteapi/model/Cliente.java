package uni.lignosuiteapi.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entità JPA collegata alla tabella "cliente" nel database.
 * Rappresenta un cliente del falegname.
 */
@Data
@Entity
@Table(name = "cliente")
public class Cliente {


    // DATI PRINCIPALI

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String email;

    private String telefono;

    private String partitaIva;


    // Relazione ManyToOne con Utente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    // METODI DI UTILITY


    /**
     * Metodo che viene chiamato automaticamente da Hibernate prima di salvare (PrePersist) o aggiornare (PreUpdate) un cliente.
     * Si occupa di formattare i campi "nome" in formato "Title Case" (prima lettera maiuscola, resto minuscolo), "email" in minuscolo e senza spazi, "partitaIva" in maiuscolo e senza spazi, e "telefono" senza spazi.
     */
    @PrePersist
    @PreUpdate
    public void formattaDati() {
        if (this.nome != null)
            this.nome = capitalizzaParole(this.nome);

        if (this.email != null)
            this.email = this.email.trim().toLowerCase();

        if (this.partitaIva != null)
            this.partitaIva = this.partitaIva.trim().toUpperCase();

        if (this.telefono != null)
            this.telefono = this.telefono.trim();
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

package uni.lignosuiteapi.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entità JPA collegata alla tabella "utente" nel database.
 * Rappresenta un utente del sistema, che può essere un falegname o un cliente (a seconda del ruolo).
 */
@Data
@Entity
@Table(name = "utente")
public class Utente {

    // DATI PRINCIPALI

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ruolo;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;


    private String nome;
    private String nomeAzienda;
    private String nomeTitolare;
    private String cognomeTitolare;
    private String telefono;
    private String partitaIva;
    private String codiceFiscale;
    private String indirizzo;
    private String citta;
    private String cap;
    private String provincia;

    @Column(columnDefinition = "TEXT")
    private String logoBase64;

    // METODI DI UTILITY

    /**
     * Metodo che viene chiamato automaticamente da Hibernate prima di salvare (PrePersist) o aggiornare (PreUpdate) un utente.
     * Si occupa di formattare i campi "ruolo" in maiuscolo e senza spazi, "email" in minuscolo e senza spazi, i campi anagrafici e
     * di indirizzo in formato "Title Case" (prima lettera maiuscola, resto minuscolo), "partitaIva" e "codiceFiscale" in maiuscolo e
     * senza spazi, e "telefono" e "cap" senza spazi. Se il campo "ruolo" è vuoto o null, viene impostato a "FALEGNAME" di default.
     */
    @PrePersist
    @PreUpdate
    public void formattaDati() {

        // Ruolo
        if (this.ruolo == null || this.ruolo.trim().isEmpty()) {
            this.ruolo = "FALEGNAME";
        } else {
            this.ruolo = this.ruolo.trim().toUpperCase();
        }

        // Email
        if (this.email != null)
            this.email = this.email.trim().toLowerCase();

        // Anagrafica
        if (this.nome != null)
            this.nome = capitalizzaParole(this.nome);

        if (this.nomeAzienda != null)
            this.nomeAzienda = capitalizzaParole(this.nomeAzienda);

        if (this.nomeTitolare != null)
            this.nomeTitolare = capitalizzaParole(this.nomeTitolare);

        if (this.cognomeTitolare != null)
            this.cognomeTitolare = capitalizzaParole(this.cognomeTitolare);

        // Indirizzo
        if (this.indirizzo != null)
            this.indirizzo = capitalizzaParole(this.indirizzo);

        if (this.citta != null)
            this.citta = capitalizzaParole(this.citta);

        // Dati fiscali
        if (this.partitaIva != null)
            this.partitaIva = this.partitaIva.trim().toUpperCase();

        if (this.codiceFiscale != null)
            this.codiceFiscale = this.codiceFiscale.trim().toUpperCase();

        // Provincia
        if (this.provincia != null)
            this.provincia = this.provincia.trim().toUpperCase();

        // Altri campi
        if (this.telefono != null)
            this.telefono = this.telefono.trim();

        if (this.cap != null)
            this.cap = this.cap.trim();
    }

    /**
     * Metodo per capitalizzare ogni parola in una stringa, mettendo la prima lettera in maiuscolo e il resto in minuscolo.
     */
    private String capitalizzaParole(String str) {

        str = str.trim();
        if (str.isEmpty())
            return str;

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

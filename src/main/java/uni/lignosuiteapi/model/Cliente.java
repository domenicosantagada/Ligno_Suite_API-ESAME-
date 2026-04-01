package uni.lignosuiteapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === Riferimento all'utente (Relazione JPA) ===
    // Sostituisce il vecchio "private Long utenteId;"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    // === Dati principali del cliente ===
    @Column(nullable = false)
    private String nome;

    private String email;
    private String telefono;
    private String partitaIva;

    // === Formattazione Automatica ===
    @PrePersist // Eseguito prima della INSERT
    @PreUpdate  // Eseguito prima della UPDATE
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

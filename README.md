# LignoSuite API - Backend

Questo repository contiene il codice sorgente del backend per il progetto **LignoSuite**, sviluppato in occasione
dell'esame di Web Applications.

**STUDENTE:** Santagada Domenico  
**MATRICOLA:** 213544

Il sistema espone i servizi necessari alla gestione degli utenti, della rubrica clienti e dei preventivi per una
falegnameria, comunicando in formato JSON con l'applicazione client (Frontend in Angular).

## Tecnologie Utilizzate

* **Linguaggio:** Java 21
* **Framework:** Spring Boot
* **Accesso ai dati:** Spring JDBC Template / Pattern DAO
* **Database:** PostgreSQL
* **Build System:** Gradle Wrapper
* **Integrazioni Esterne:** Google Gemini API (AI Generativa), JavaMailSender (Invio PDF)

---

## Prerequisiti

Per eseguire il progetto correttamente sul proprio ambiente locale, è necessario:

* **Java Development Kit (JDK) 21** o superiore.
* **PostgreSQL** installato e in esecuzione sulla porta di default `5432`.

*(Non è necessario avere Gradle installato sul sistema, in quanto il progetto include il Gradle Wrapper).*

---

## Configurazione del Progetto

### 1. Ripristino del Database

Il progetto utilizza PostgreSQL. Nella cartella principale del progetto (o dove lo hai salvato) è presente il file di
dump del database.

1. Creare un database vuoto in Postgres chiamato `lignosuitedb`.
2. Assicurarsi che le credenziali in `src/main/resources/application.properties` combacino con quelle del proprio server
   Postgres (di default `username=postgres`, `password=admin`).
3. Effettuare il restore del dump fornito nella consegna.

### 2. Configurazione API Key e Server Email

Per questioni di sicurezza, le chiavi API e le password non sono tracciate su GitHub.

1. Navigare in `src/main/resources/`.
2. Rinominare il file `application-secret.properties.example` in `application-secret.properties`.
3. Aprire il file e inserire:
    * La propria API Key di Google Gemini.
    * L'email e la password per l'SMTP di Gmail (è necessaria la "Password per le app" se si usa l'autenticazione a due
      fattori).

---

## Avvio dell'Applicazione

1. Clonare il repository o estrarre la cartella.
2. Aprire il terminale nella root del progetto.
3. Eseguire il server tramite il Gradle Wrapper:
    * Su Windows: `gradlew.bat bootRun`
    * Su Mac/Linux: `./gradlew bootRun`
4. Il backend sarà in ascolto sulla porta `8080` all'indirizzo `http://localhost:8080/`.
5. Il frontend Angular (eseguito separatamente) comunicherà con le API tramite i vari `@RestController`.

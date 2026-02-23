package uni.toolfalegnameriaapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.toolfalegnameriaapi.model.Preventivo;

@Repository
public interface PreventivoRepository extends JpaRepository<Preventivo, String> {
    // Estendendo JpaRepository abbiamo già a disposizione metodi come save(), findAll(), findById(), ecc.
    // La chiave primaria di Invoice è di tipo String (invoiceNumber).
}
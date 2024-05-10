package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Query("SELECT s " +
            "FROM Supplier s " +
            "WHERE s.deletionDate IS NULL")
    List<Supplier> findAllActives();

    @Query("SELECT s " +
            "FROM Supplier s " +
            "WHERE s.id = ?1 AND s.deletionDate IS NULL")
    Optional<Supplier> findByIdActive(Long supplierID);

    @Query("SELECT COUNT(s) > 0 " +
            "FROM Supplier s " +
            "WHERE s.id = ?1 AND s.deletionDate IS NULL")
    Boolean existsByIdActive(Long supplierID);
}

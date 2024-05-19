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
            "INNER JOIN s.products p " +
            "INNER JOIN p.business b " +
            "WHERE b.id = ?1")
    List<Supplier> findByBusiness(Long businessID);

    @Query("SELECT s " +
            "FROM Supplier s " +
            "INNER JOIN s.products p " +
            "INNER JOIN p.business b " +
            "WHERE s.deletionDate IS NULL AND b.id = ?1")
    List<Supplier> findActivesByBusiness(Long businessID);

    @Query("SELECT s " +
            "FROM Supplier s " +
            "INNER JOIN s.products p " +
            "INNER JOIN p.business b " +
            "WHERE s.id = ?1 AND s.deletionDate IS NULL AND b.id = ?2")
    Optional<Supplier> findByIdActiveAndBusiness(Long supplierID, Long businessID);

    @Query("SELECT COUNT(s) > 0 " +
            "FROM Supplier s " +
            "INNER JOIN s.products p " +
            "INNER JOIN p.business b " +
            "WHERE s.id = ?1 AND s.deletionDate IS NULL AND b.id = ?2")
    Boolean existsByIdActiveAndBusiness(Long supplierID, Long businessID);
}

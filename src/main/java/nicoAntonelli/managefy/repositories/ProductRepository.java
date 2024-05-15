package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p " +
            "FROM Product p " +
            "INNER JOIN p.business b " +
            "WHERE p.deletionDate IS NULL AND b.id = ?1")
    List<Product> findActivesByBusiness(Long businessID);

    @Query("SELECT p " +
            "FROM Product p " +
            "INNER JOIN p.business b " +
            "INNER JOIN p.supplier s " +
            "WHERE p.deletionDate IS NULL " +
            "AND s.id = ?1 AND s.id = ?2")
    List<Product> findActivesByBusinessAndSupplier(Long businessID, Long supplierID);

    @Query("SELECT p " +
            "FROM Product p " +
            "INNER JOIN p.business b " +
            "WHERE p.id = ?1 AND p.deletionDate IS NULL AND b.id = ?2")
    Optional<Product> findByIdActiveAndBusiness(Long productID, Long businessID);

    @Query("SELECT COUNT(p) > 0 " +
            "FROM Product p " +
            "INNER JOIN p.business b " +
            "WHERE p.id = ?1 AND p.deletionDate IS NULL AND b.id = ?2")
    Boolean existsByIdActiveAndBusiness(Long productID, Long businessID);
}

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
            "WHERE p.deletionDate IS NULL")
    List<Product> findAllActives();

    @Query("SELECT p " +
            "FROM Product p " +
            "WHERE p.id = ?1 AND p.deletionDate IS NULL")
    Optional<Product> findByIdActive(Long productID);

    @Query("SELECT COUNT(p) > 0 " +
            "FROM Product p " +
            "WHERE p.id = ?1 AND p.deletionDate IS NULL")
    Boolean existsByIdActive(Long productID);
}

package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT s " +
            "FROM Sale s " +
            "INNER JOIN s.business b " +
            "WHERE s.date >= ?1 AND s.date <= ?2" +
            "AND s.state <> 'Cancelled' AND b.id = ?3")
    List<Sale> findActivesByIntervalAndBusiness(Long businessID, LocalDateTime initialDate, LocalDateTime finalDate);

    @Query("SELECT s " +
            "FROM Sale s " +
            "INNER JOIN s.business b " +
            "WHERE (s.state = 'PendingPayment' OR s.state = 'PartialPayment') AND b.id = ?1")
    List<Sale> findIncompleteByBusiness(Long businessID);

    @Query("SELECT s " +
            "FROM Sale s " +
            "INNER JOIN s.business b " +
            "WHERE s.id = ?1 AND s.state <> 'Cancelled' IS NULL AND b.id = ?2")
    Optional<Sale> findByIdActiveAndBusiness(Long productID, Long businessID);

    @Query("SELECT COUNT(s) > 0 " +
            "FROM Sale s " +
            "INNER JOIN s.business b " +
            "WHERE s.id = ?1 AND s.state <> 'Cancelled' IS NULL AND b.id = ?2")
    Boolean existsByIdActiveAndBusiness(Long productID, Long businessID);
}

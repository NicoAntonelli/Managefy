package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
            "WHERE s.date >= ?1 AND s.date <= ?2 " +
            "AND s.state <> SaleState.Cancelled AND b.id = ?3 " +
            "ORDER BY s.date DESC")
    List<Sale> findActivesByIntervalAndBusiness(LocalDateTime initialDate, LocalDateTime finalDate, Long businessID);

    @Query("SELECT s " +
            "FROM Sale s " +
            "INNER JOIN s.business b " +
            "WHERE (s.state = SaleState.PendingPayment OR s.state = SaleState.PartialPayment) " +
            "AND b.id = ?1 " +
            "ORDER BY s.date DESC")
    List<Sale> findIncompleteByBusiness(Long businessID);

    @Query("SELECT s " +
            "FROM Sale s " +
            "INNER JOIN s.business b " +
            "INNER JOIN s.client c " +
            "WHERE s.state <> SaleState.Cancelled " +
            "AND b.id = ?1 AND c.id = ?2 " +
            "ORDER BY s.date DESC")
    List<Sale> findActivesByBusinessAndClient(Long businessID, Long clientID);

    @Query("SELECT s " +
            "FROM Sale s " +
            "INNER JOIN s.business b " +
            "WHERE s.id = ?1 AND s.state <> SaleState.Cancelled AND b.id = ?2 " +
            "ORDER BY s.date DESC")
    Optional<Sale> findByIdActiveAndBusiness(Long productID, Long businessID);

    @Query("SELECT COUNT(s) > 0 " +
            "FROM Sale s " +
            "INNER JOIN s.business b " +
            "WHERE s.id = ?1 AND s.state <> SaleState.Cancelled AND b.id = ?2 " +
            "ORDER BY s.date DESC")
    Boolean existsByIdActiveAndBusiness(Long productID, Long businessID);

    @Modifying
    @Query("DELETE FROM Sale s " +
            "WHERE s IN (" +
            "SELECT sub " +
            "FROM Sale sub " +
            "INNER JOIN sub.business b " +
            "WHERE b.id = ?1)")
    void deleteAllByBusiness(Long businessID);
}

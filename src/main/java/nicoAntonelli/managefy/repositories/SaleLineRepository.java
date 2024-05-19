package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.SaleLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleLineRepository extends JpaRepository<SaleLine, Long> {
    @Modifying
    @Query("DELETE FROM SaleLine sl " +
            "WHERE sl IN (" +
            "SELECT sub " +
            "FROM SaleLine sub " +
            "INNER JOIN sub.sale s " +
            "INNER JOIN s.business b " +
            "WHERE b.id = ?1)")
    void deleteAllByBusiness(Long businessID);
}

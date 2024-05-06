package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT s " +
            "FROM Sale s " +
            "WHERE s.date >= ?1 AND s.date <= ?2")
    List<Sale> findByInterval(Date initialDate, Date finalDate);
}

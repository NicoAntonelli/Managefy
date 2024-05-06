package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
    @Query("SELECT e " +
           "FROM ErrorLog e " +
           "WHERE e.origin = ?1 AND e.date >= ?2 AND e.date <= ?3")
    List<ErrorLog> findByOriginAndInterval(String origin, Date initialDate, Date finalDate);
}

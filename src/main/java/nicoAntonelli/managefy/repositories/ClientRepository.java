package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT c " +
            "FROM Client c " +
            "INNER JOIN c.sales s " +
            "INNER JOIN s.business b " +
            "WHERE b.id = ?1")
    List<Client> findByBusiness(Long businessID);

    @Query("SELECT c " +
            "FROM Client c " +
            "INNER JOIN c.sales s " +
            "INNER JOIN s.business b " +
            "WHERE c.deletionDate IS NULL AND b.id = ?1")
    List<Client> findActivesByBusiness(Long businessID);

    @Query("SELECT c " +
            "FROM Client c " +
            "INNER JOIN c.sales s " +
            "INNER JOIN s.business b " +
            "WHERE c.id = ?1 AND c.deletionDate IS NULL AND b.id = ?2")
    Optional<Client> findByIdActiveAndBusiness(Long supplierID, Long businessID);

    @Query("SELECT COUNT(c) > 0 " +
            "FROM Client c " +
            "INNER JOIN c.sales s " +
            "INNER JOIN s.business b " +
            "WHERE c.id = ?1 AND c.deletionDate IS NULL AND b.id = ?2")
    Boolean existsByIdActiveAndBusiness(Long supplierID, Long businessID);
}

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
            "WHERE c.deletionDate IS NULL")
    List<Client> findAllActives();

    @Query("SELECT c " +
            "FROM Client c " +
            "WHERE c.id = ?1 AND c.deletionDate IS NULL")
    Optional<Client> findByIdActive(Long clientID);

    @Query("SELECT COUNT(c) > 0 " +
            "FROM Client c " +
            "WHERE c.id = ?1 AND c.deletionDate IS NULL")
    Boolean existsByIdActive(Long clientID);
}

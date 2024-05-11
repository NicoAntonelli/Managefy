package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.UserValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserValidationRepository extends JpaRepository<UserValidation, Long> {
    @Query("SELECT uv FROM UserValidation uv " +
            "INNER JOIN uv.user u " +
            "WHERE u.id = ?1")
    Optional<UserValidation> findByUser(Long userID);
}

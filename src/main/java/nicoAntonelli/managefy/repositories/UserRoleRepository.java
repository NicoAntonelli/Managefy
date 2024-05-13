package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.entities.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleKey> {
    @Query("SELECT ur " +
            "FROM UserRole ur " +
            "INNER JOIN ur.user u " +
            "WHERE u.id = ?1")
    List<UserRole> findByUser(Long userID);

    @Query("SELECT ur " +
            "FROM UserRole ur " +
            "INNER JOIN ur.business b " +
            "WHERE b.id = ?1")
    List<UserRole> findByBusiness(Long businessID);
}

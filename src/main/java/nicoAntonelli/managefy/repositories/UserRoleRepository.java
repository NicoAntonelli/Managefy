package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.entities.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Modifying
    @Query("DELETE FROM UserRole ur " +
            "WHERE ur IN (" +
            "SELECT sub " +
            "FROM UserRole sub " +
            "INNER JOIN sub.business b " +
            "WHERE b.id = ?1)")
    void deleteAllByBusiness(Long businessID);
}

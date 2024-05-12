package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    @Query("SELECT b " +
            "FROM Business b " +
            "INNER JOIN b.userRoles ur " +
            "INNER JOIN ur.user u " +
            "WHERE u.id = ?1")
    List<Business> findByUser(Long userID); // Don't include public ones

    @Query("SELECT b " +
            "FROM Business b " +
            "INNER JOIN b.userRoles ur " +
            "INNER JOIN ur.user u " +
            "WHERE b.id = ?1 " +
            "AND (b.isPublic = TRUE OR u.id = ?2)")
    Optional<Business> findByIdAndUser(Long businessID, Long userID);

    @Query("SELECT COUNT(b) " +
            "FROM Business b " +
            "INNER JOIN b.userRoles ur " +
            "INNER JOIN ur.user u " +
            "WHERE b.id = ?1 " +
            "AND (b.isPublic = TRUE OR u.id = ?2)")
    Boolean existsByIdAndUser(Long businessID, Long userID);

    @Query("SELECT COUNT(b) " +
            "FROM Business b " +
            "INNER JOIN b.userRoles ur " +
            "INNER JOIN ur.user u " +
            "WHERE b.id = ?1" +
            "AND (b.isPublic = TRUE OR (u.id = ?2 AND (ur.isAdmin = TRUE OR ur.isManager = TRUE)))")
    Boolean existsByIdAndUserAdmin(Long businessID, Long userID);

    @Query("SELECT COUNT(b) " +
            "FROM Business b " +
            "INNER JOIN b.userRoles ur " +
            "INNER JOIN ur.user u " +
            "WHERE b.id = ?1" +
            "AND (b.isPublic = TRUE OR (u.id = ?2 AND ur.isManager = TRUE))")
    Boolean existsByIdAndUserManager(Long businessID, Long userID);
}

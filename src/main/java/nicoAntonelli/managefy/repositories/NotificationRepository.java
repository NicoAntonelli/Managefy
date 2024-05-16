package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // No closed, sorting: unread before read, priority-ordered, then date-ordered
    @Query("SELECT n " +
            "FROM Notification n " +
            "INNER JOIN n.user u " +
            "WHERE n.state <> NotificationState.Closed AND u.id = ?1" +
            "ORDER BY n.state DESC, n.type DESC, n.date DESC")
    List<Notification> findByUser(Long userID);

    @Query("SELECT n " +
            "FROM Notification n " +
            "INNER JOIN n.user u " +
            "WHERE n.id = ?1 AND u.id = ?2")
    Optional<Notification> findByIdAndUser(Long notificationID, Long userID);

    @Query("SELECT COUNT(n) " +
            "FROM Notification n " +
            "INNER JOIN n.user u " +
            "WHERE n.id = ?1 AND u.id = ?2")
    Boolean existsByIdAndUser(Long notificationID, Long userID);
}

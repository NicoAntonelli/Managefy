package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User WHERE u.mail = ?1")
    Optional<User> findByMail(String mail);
}

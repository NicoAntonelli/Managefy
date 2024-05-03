package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> { }

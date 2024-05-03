package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.SaleLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleLineRepository extends JpaRepository<SaleLine, Long> { }

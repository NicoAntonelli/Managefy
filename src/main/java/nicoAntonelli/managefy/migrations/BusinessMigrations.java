package nicoAntonelli.managefy.migrations;

import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.repositories.BusinessRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BusinessMigrations {
    @Bean
    CommandLineRunner businessRunner(BusinessRepository repository) {
        return args -> {
            Business business1 = new Business("Big shop", "Buy everything", "big-shop/");
            Business business2 = new Business("Small shop", "Buy something", "small-shop/");

            List<Business> clients = List.of(business1, business2);
            repository.saveAll(clients);
        };
    }
}

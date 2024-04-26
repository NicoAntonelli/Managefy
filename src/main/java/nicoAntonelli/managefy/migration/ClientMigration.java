package nicoAntonelli.managefy.migration;

import nicoAntonelli.managefy.entities.Client;
import nicoAntonelli.managefy.repository.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ClientMigration {
    @Bean
    CommandLineRunner runner(ClientRepository repository) {
        return args -> {
            Client client1 = new Client("Nick", "Food & soda", "nick@mail.com", "123456", null);
            Client client2 = new Client("Kasp", "Red meat", "kasp@mail.com", "111222", null);

            List<Client> clients = List.of(client1, client2);
            repository.saveAll(clients);
        };
    }
}

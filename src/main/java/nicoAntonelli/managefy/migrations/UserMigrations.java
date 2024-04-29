package nicoAntonelli.managefy.migrations;

import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserMigrations {
    @Bean
    CommandLineRunner userRunner(UserRepository repository) {
        return args -> {
            User user1 = new User("johndoe@mail.com", "12345678", "John Doe", true, true);
            User user2 = new User("janedoe@mail.com", "12345678", "Jane Doe", false, false);

            List<User> users = List.of(user1, user2);
            repository.saveAll(users);
        };
    }
}

package nicoAntonelli.managefy;

import nicoAntonelli.managefy.entities.*;
import nicoAntonelli.managefy.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;

@Configuration
public class DBMigrations {
    @Bean
    CommandLineRunner runner(BusinessRepository businessRepository,
                             ClientRepository clientRepository,
                             NotificationRepository notificationRepository,
                             UserRepository userRepository) {
        return args -> {
            // Business
            List<Business> businesses = generateBusinesses();
            businessRepository.saveAll(businesses);

            // Client
            List<Client> clients = generateClients();
            clientRepository.saveAll(clients);

            // User
            List<User> users = generateUsers();
            userRepository.saveAll(users);

            // Notification
            List<Notification> notifications = generateNotifications();
            notificationRepository.saveAll(notifications);
        };
    }

    private static List<Business> generateBusinesses() {
        Business business1 = new Business("Big shop", "Buy everything", "big-shop/");
        Business business2 = new Business("Small shop", "Buy something", "small-shop/");

        return List.of(business1, business2);
    }

    private static List<Client> generateClients() {
        Client client1 = new Client("Nick", "Food & soda", "nick@mail.com", "123456", null);
        Client client2 = new Client("Kasp", "Red meat", "kasp@mail.com", "111222", null);

        return List.of(client1, client2);
    }

    private static List<Notification> generateNotifications() {
        Date today = new Date();

        Notification not1 = new Notification("You made 10 sales this week, 2 more than the previous",
                Notification.NotificationType.Normal, Notification.NotificationState.Read,
                today, 1L);
        Notification not2 = new Notification("The sale has been completed successfully",
                Notification.NotificationType.Normal, Notification.NotificationState.Closed,
                today, 1L);
        Notification not3 = new Notification("An admin has removed you from the 'MinShop' business",
                Notification.NotificationType.Priority, Notification.NotificationState.Unread,
                today, 1L);
        Notification not4 = new Notification("Minimum storage for Soda reached at 'MyShop': 3 units left",
                Notification.NotificationType.Important, Notification.NotificationState.Unread,
                today, 1L);

        return List.of(not1, not2, not3, not4);
    }

    private static List<User> generateUsers() {
        User user1 = new User("johndoe@mail.com", "12345678", "John Doe", true, true);
        User user2 = new User("janedoe@mail.com", "12345678", "Jane Doe", false, false);

        return List.of(user1, user2);
    }
}

package nicoAntonelli.managefy.migrations;

import nicoAntonelli.managefy.entities.Notification;
import nicoAntonelli.managefy.repositories.NotificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;

@Configuration
public class NotificationMigrations {
    @Bean
    CommandLineRunner runner(NotificationRepository repository) {
        return args -> {
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

            List<Notification> notifications = List.of(not1, not2, not3, not4);
            repository.saveAll(notifications);
        };
    }
}

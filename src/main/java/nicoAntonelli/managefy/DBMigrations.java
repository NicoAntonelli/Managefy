package nicoAntonelli.managefy;

import nicoAntonelli.managefy.entities.*;
import nicoAntonelli.managefy.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class DBMigrations {
    @Bean
    CommandLineRunner runner(BusinessRepository businessRepository,
                             ClientRepository clientRepository,
                             ErrorLogRepository errorLogRepository,
                             NotificationRepository notificationRepository,
                             ProductRepository productRepository,
                             SaleLineRepository saleLineRepository,
                             SaleRepository saleRepository,
                             SupplierRepository supplierRepository,
                             UserRepository userRepository,
                             UserRoleRepository userRoleRepository) {
        return args -> {
            // Businesses
            List<Business> businesses = generateBusinesses();
            businessRepository.saveAll(businesses);

            // Clients
            List<Client> clients = generateClients();
            clientRepository.saveAll(clients);

            // ErrorLogs
            List<ErrorLog> errors = generateErrors();
            errorLogRepository.saveAll(errors);

            // Suppliers
            List<Supplier> suppliers = generateSuppliers();
            supplierRepository.saveAll(suppliers);

            // Products
            List<Product> products = generateProducts();
            productRepository.saveAll(products);

            // Sales & SaleLines
            List<Sale> sales = generateSalesWithLines(products);
            saleRepository.saveAll(sales);

            List<SaleLine> saleLines = new ArrayList<>();
            for (Sale sale : sales) {
                saleLines.addAll(sale.getSaleLines());
            }
            saleLineRepository.saveAll(saleLines);

            // Users
            List<User> users = generateUsers();
            userRepository.saveAll(users);

            // User roles
            List<UserRole> userRoles = generateUserRoles();
            userRoleRepository.saveAll(userRoles);

            // Notifications
            List<Notification> notifications = generateNotifications();
            notificationRepository.saveAll(notifications);
        };
    }

    private static List<Business> generateBusinesses() {
        Business business1 = new Business("Groceryfy", "Buy everything", "groceryfy/");
        Business business2 = new Business("Dean's Butchery", "The best meat", "deans-butchery/");

        return List.of(business1, business2);
    }

    private static List<Client> generateClients() {
        Client client1 = new Client("Nick A", "Regular client", "anick@mail.com", "123456");
        Client client2 = new Client("Alex R", "Regular client", "ralex@mail.com", "112233");
        Client client3 = new Client("Joseph A", "Regular client", "ajoseph@mail.com", "445566");

        return List.of(client1, client2, client3);
    }

    private static List<ErrorLog> generateErrors() {
        ErrorLog error1 = new ErrorLog("Dummy error 1: It took you 3 years to start & deliver the Java TP!");
        ErrorLog error2 = new ErrorLog("Dummy error 2: Yep, the previous error was an easter egg");

        return List.of(error1, error2);
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

    private static List<Product> generateProducts() {
        Product product1 = new Product("S01", "Soda (Big)", "Soda 2L", 100f, 130f, 10, 4, null);
        Product product2 = new Product("S02", "Soda (Medium)", "Soda 1L", 60f, 80f, 10, 4, null);
        Product product3 = new Product("S03", "Soda (Small)", "Soda 500ml", 35f, 50f, 20, 8, null);
        Product product4 = new Product("N01", "Doritos", "Pack of doritos", 60f, 80f, 20, null, null);
        Product product5 = new Product("D01", "Milk 1L", "Regular milk", 30f, 40f, 10, null, null);
        Product product6 = new Product("D02", "Diet Yogurt", "Yogurt with less sugar", 30f, 40f, 20, null, null);
        Product product7 = new Product("M01", "Egg", "Just an egg", 200f, 260f, 10, null, 6);
        Product product8 = new Product("M02", "Meat 1KG", "Roast beef", 200f, 260f, 20, 4, 2);
        Product product9 = new Product("M03", "Chicken 1KG", "Roast beef", 150f, 200f, 20, 4, 2);

        // Set supplier
        product1.setSupplierByID(1L);
        product2.setSupplierByID(1L);
        product3.setSupplierByID(1L);
        product4.setSupplierByID(1L);
        product5.setSupplierByID(3L);
        product6.setSupplierByID(3L);
        product7.setSupplierByID(2L);
        product8.setSupplierByID(2L);
        product9.setSupplierByID(2L);

        // Set business
        product1.setBusinessByID(1L);
        product2.setBusinessByID(1L);
        product3.setBusinessByID(1L);
        product4.setBusinessByID(1L);
        product5.setBusinessByID(1L);
        product6.setBusinessByID(1L);
        product7.setBusinessByID(2L);
        product8.setBusinessByID(2L);
        product9.setBusinessByID(2L);

        return List.of(product1, product2, product3, product4, product5, product6, product7, product8, product9);
    }

    private static List<Sale> generateSalesWithLines(List<Product> products) {
        // Sale 1 - Soda & Doritos
        long sale1ID = 1L;
        Sale sale1 = new Sale(sale1ID, 0f, null);
        sale1.setBusinessByID(1L);
        sale1.setClientByID(1L);

        Product productSoda = products.getFirst();
        SaleLine saleLineS1L1 = new SaleLine(sale1, 1, 1, productSoda.getUnitPrice(), null);
        saleLineS1L1.setProductByID(productSoda.getId());

        Product productDoritos = products.get(3);
        SaleLine saleLineS1L2 = new SaleLine(sale1, 2, 1, productDoritos.getUnitPrice(), null);
        saleLineS1L2.setProductByID(productDoritos.getId());

        sale1.addSaleLine(saleLineS1L1);
        sale1.addSaleLine(saleLineS1L2);
        sale1.calculateAndSetTotalPrice();

        // Sale 2 - Chicken & Eggs with 10% off
        long sale2ID = 2L;
        Sale sale2 = new Sale(sale2ID, 0f, null);
        sale2.setBusinessByID(1L);
        sale2.setClientByID(1L);

        float discount = 10f;
        float discountCoefficient = 1 - (discount/100);

        Product productChicken = products.get(8);
        SaleLine saleLineS2L1 = new SaleLine(sale2, 1, 1, productChicken.getUnitPrice(), discountCoefficient);
        saleLineS2L1.setSaleByID(sale2ID);
        saleLineS2L1.setProductByID(productChicken.getId());

        Product productEgg = products.get(6);
        SaleLine saleLineS2L2 = new SaleLine(sale2, 2, 6, productEgg.getUnitPrice(), discountCoefficient);
        saleLineS2L2.setSaleByID(sale2ID);
        saleLineS2L2.setProductByID(productEgg.getId());

        sale2.addSaleLine(saleLineS2L1);
        sale2.addSaleLine(saleLineS2L2);
        sale2.calculateAndSetTotalPrice();

        return List.of(sale1, sale2);
    }

    private static List<Supplier> generateSuppliers() {
        Supplier supplier1 = new Supplier("Pep S I", "Snacks & soda", "sipep@mail.com", "111222");
        Supplier supplier2 = new Supplier("Butch E R", "Red meat, chicken & eggs", "erbutch@mail.com", "333444");
        Supplier supplier3 = new Supplier("Dai R Y", "Milk products", "rydai@mail.com", "555666");

        return List.of(supplier1, supplier2, supplier3);
    }

    private static List<User> generateUsers() {
        User user1 = new User("johndoe@mail.com", "12345678", "John Doe", true, true);
        User user2 = new User("janedoe@mail.com", "11001100", "Jane Doe", false, false);

        return List.of(user1, user2);
    }

    private static List<UserRole> generateUserRoles() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        Business business1 = new Business();
        business1.setId(1L);
        Business business2 = new Business();
        business2.setId(2L);

        UserRole userRole1 = new UserRole(user1, business1, true, false, false);
        UserRole userRole2 = new UserRole(user2, business2, true, false, false);

        return List.of(userRole1, userRole2);
    }
}

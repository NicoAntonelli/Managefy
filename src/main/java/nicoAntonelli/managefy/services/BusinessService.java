package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.*;
import nicoAntonelli.managefy.entities.dto.BusinessCU;
import nicoAntonelli.managefy.entities.dto.NotificationC;
import nicoAntonelli.managefy.repositories.*;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;

@Service
@Transactional
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final ClientRepository clientRepository; // Dependency
    private final ProductRepository productRepository; // Dependency
    private final SaleRepository saleRepository; // Dependency
    private final SaleLineRepository saleLineRepository; // Dependency
    private final SupplierRepository supplierRepository; // Dependency
    private final UserRoleRepository userRoleRepository; // Dependency
    private final NotificationService notificationService; // Dependency

    @Autowired
    public BusinessService(BusinessRepository businessRepository,
                           ClientRepository clientRepository,
                           ProductRepository productRepository,
                           SaleRepository saleRepository,
                           SaleLineRepository saleLineRepository,
                           SupplierRepository supplierRepository,
                           UserRoleRepository userRoleRepository,
                           NotificationService notificationService) {
        this.businessRepository = businessRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.saleLineRepository = saleLineRepository;
        this.supplierRepository = supplierRepository;
        this.userRoleRepository = userRoleRepository;
        this.notificationService = notificationService;
    }

    public List<Business> GetBusinesses(User user) {
        return businessRepository.findByUser(user.getId());
    }

    public Boolean ExistsBusiness(Long businessID, User user, String minimumRole) {
        return switch (minimumRole) {
            case "admin" -> businessRepository.existsByIdAndUserAdmin(businessID, user.getId());
            case "manager" -> businessRepository.existsByIdAndUserManager(businessID, user.getId());
            default -> businessRepository.existsByIdAndUser(businessID, user.getId());
        };
    }

    public Business GetOneBusiness(Long businessID, User user) {
        Optional<Business> business = businessRepository.findByIdAndUser(businessID, user.getId());
        if (business.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneBusiness' - Business with ID: " + businessID + " doesn't exist or the user: " + user.getId() + " don't have a rol in it");
        }

        return business.get();
    }

    public Business GetOneBusinessByLink(String link, User user) {
        Optional<Business> business = businessRepository.findByLink(link, user.getId());
        if (business.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneBusinessByLink' - Business with Link: " + link + " doesn't exist or the user: " + user.getId() + " don't have a rol in it");
        }

        return business.get();
    }

    public Business GetOneBusinessByLinkPublic(String link) {
        Optional<Business> business = businessRepository.findByLinkPublic(link);
        if (business.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneBusinessByLinkPublic' - Business with Link: " + link + " doesn't exist or the business it's not public");
        }

        return business.get();
    }

    public Business CreateBusiness(BusinessCU businessCU, User user) {
        // Validate simple attributes
        if (businessCU.getName() == null || businessCU.getDescription() == null || businessCU.getLink() == null) {
            throw new Exceptions.BadRequestException("Error at 'CreateBusiness' - One or more of the required fields were not supplied");
        }

        // Link unique validation
        Optional<Business> possibleBusiness = businessRepository.findByLink(businessCU.getLink(), user.getId());
        if (possibleBusiness.isPresent()) {
            throw new Exceptions.BadRequestException("Error at 'CreateBusiness' - Link '" + businessCU.getLink() + "' already taken");
        }

        // Validate business days (Optional: has a default value if not supplied)
        ValidateBusinessDays(businessCU);

        // New client object with DTO info
        Business business = new Business(businessCU.getName(), businessCU.getDescription(), businessCU.getLink(),
                                         businessCU.getIsPublic(), businessCU.getBusinessDays());

        // Note: user role "Manager" creation it's called from controller to prevent circular dependency
        business = businessRepository.save(business);

        // Notification for new business
        NotificationC notification = new NotificationC("Your new business '" + business.getName() + "' is ready! You can load some products and sales!", "normal");
        notificationService.CreateNotification(notification, user);

        return business;
    }

    public Business UpdateBusiness(BusinessCU businessCU, User user) {
        boolean exists = ExistsBusiness(businessCU.getId(), user, "admin");
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'UpdateBusiness' - Business with ID: " + businessCU.getId() + " doesn't exist or the user: " + user.getId() + " isn't an Admin");
        }

        // Validate simple attributes
        if (businessCU.getName() == null || businessCU.getDescription() == null || businessCU.getLink() == null) {
            throw new Exceptions.BadRequestException("Error at 'UpdateBusiness' - One or more of the required fields were not supplied");
        }

        // Validate business days (Optional: has a default value if not supplied)
        ValidateBusinessDays(businessCU);

        // Link unique validation
        Optional<Business> possibleBusiness = businessRepository.findByLink(businessCU.getLink(), user.getId());
        if (possibleBusiness.isPresent()) {
            // Only fail validation if it's not the same business
            if (!Objects.equals(possibleBusiness.get().getId(), businessCU.getId())) {
                throw new Exceptions.BadRequestException("Error at 'UpdateBusiness' - Link '" + businessCU.getLink() + "' already taken");
            }
        }

        // Validate business existence and obtain it loaded from DB
        Business business = GetOneBusiness(businessCU.getId(), user);

        // Merge DTO's client with the original - Don't mess up relation with other entities
        business.setName(businessCU.getName());
        business.setDescription(businessCU.getDescription());
        business.setLink(businessCU.getLink());
        business.setIsPublic(businessCU.getIsPublic());
        business.setBusinessDays(businessCU.getBusinessDays());

        business = businessRepository.save(business);

        // Notification for update business
        NotificationC notification = new NotificationC("Your business '" + business.getName() + "' was correctly updated", "low");
        notificationService.CreateNotification(notification, user);

        return business;
    }

    // Warning: deletes everything related also!
    public Long DeleteBusiness(Long businessID, User user) {
        boolean exists = ExistsBusiness(businessID, user, "manager");
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'DeleteBusiness' - Business with ID: " + businessID + " doesn't exist or the user: " + user.getId() + " isn't the Manager");
        }

        // Get associated clients & suppliers, actives or not
        List<Client> associatedClients = clientRepository.findByBusiness(businessID);
        List<Supplier> associatedSuppliers = supplierRepository.findByBusiness(businessID);

        // Physically delete associated: saleLines, sales, products & roles
        saleLineRepository.deleteAllByBusiness(businessID);
        saleRepository.deleteAllByBusiness(businessID);
        productRepository.deleteAllByBusiness(businessID);
        userRoleRepository.deleteAllByBusiness(businessID);

        // Physically delete associated: clients & suppliers
        clientRepository.deleteAll(associatedClients);
        supplierRepository.deleteAll(associatedSuppliers);

        // Finally, physically delete business
        businessRepository.deleteById(businessID);

        // Notification for deleted business
        NotificationC notification = new NotificationC("Your business was correctly deleted, with all the associated info (sales, products, clients, suppliers, etc...)", "priority");
        notificationService.CreateNotification(notification, user);

        return businessID;
    }

    private void ValidateBusinessDays(BusinessCU businessCU) {
        SortedMap<String, Boolean> businessDays = businessCU.getBusinessDays();
        if (businessCU.getBusinessDays() != null) {
            if (businessDays.size() != 7) {
                throw new Exceptions.BadRequestException("Error at 'ValidateBusinessDays' - Business days need to have a total of 7 days");
            }

            List<String> weekDays = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
            for (String key : businessDays.keySet()) {
                if (!weekDays.contains(key)) {
                    throw new Exceptions.BadRequestException("Error at 'ValidateBusinessDays' - Business days don't contain '" + key + "'");
                }
            }
        }
    }
}

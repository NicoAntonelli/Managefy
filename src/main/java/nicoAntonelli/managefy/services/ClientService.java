package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.*;
import nicoAntonelli.managefy.repositories.ClientRepository;
import nicoAntonelli.managefy.repositories.SaleRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final BusinessService businessService; // Dependency
    private final SaleRepository saleRepository; // Dependency

    @Autowired
    public ClientService(ClientRepository clientRepository,
                         BusinessService businessService,
                         SaleRepository saleRepository) {
        this.clientRepository = clientRepository;
        this.businessService = businessService;
        this.saleRepository = saleRepository;
    }

    public List<Client> GetClients(Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return clientRepository.findActivesByBusiness(businessID);
    }

    // Note: this method needs to be called with a client with sales already-validated
    public Boolean ExistsClient(Client client, User user) {
        // Get businessID from first element (
        Long businessID = client.getSales().iterator().next().getBusiness().getId();

        return ExistsClient(client.getId(), businessID, user);
    }

    public Boolean ExistsClient(Long clientID, Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return clientRepository.existsByIdActiveAndBusiness(clientID, businessID);
    }

    public Client GetOneClient(Long clientID, Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        Optional<Client> client = clientRepository.findByIdActiveAndBusiness(clientID, businessID);
        if (client.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneClient' - Client with ID: " + clientID + " doesn't exist or it's not associated with the business: " + businessID);
        }

        return client.get();
    }

    public Client CreateClient(Client client, User user, Boolean noProductsControlled) {
        // Validate name
        if (client.getName() == null || client.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'CreateClient' - Name field was not supplied");
        }

        // Validate associated sales
        ValidateSalesForClient(client, user, noProductsControlled);

        // Forced initial state
        client.setId(null);
        client.setDeletionDate(null);

        return clientRepository.save(client);
    }

    public Client UpdateClient(Client client, User user) {
        // Validate name and logic deletion
        if (client.getName() == null || client.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'UpdateClient' - Name field was not supplied");
        }
        if (client.getDeletionDate() != null) {
            throw new Exceptions.BadRequestException("Error at 'UpdateClient' - Client can't have a deletion date");
        }

        // Validate associated sales
        ValidateSalesForClient(client, user, false);

        // Validate client existence
        boolean exists = ExistsClient(client, user);
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'UpdateClient' - Client with ID: " + client.getId() + " doesn't exist or it's not associated with the user: " + user.getId());
        }

        return clientRepository.save(client);
    }

    // Logic deletion (field: deletion date)
    public Long DeleteClient(Long clientID, Long businessID, User user) {
        // Validate admin role
        boolean exists = businessService.ExistsBusiness(businessID, user, "admin");
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'DeleteClient' - Business with ID: " + businessID + " doesn't exist or the user: " + user.getId() + " isn't an Admin or the Manager");
        }

        Client client = GetOneClient(clientID, businessID, user);
        client.setDeletionDate(LocalDateTime.now());
        clientRepository.save(client);

        return clientID;
    }

    private void ValidateSalesForClient(Client client, User user, Boolean noProductsControlled) {
        // Client created in the context of a current new sale creation
        if (noProductsControlled) {
            client.setSales(null);
            return;
        }

        // At least one sale
        Set<Sale> sales = client.getSales();
        if (sales == null || sales.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSalesForClient' - No sale was supplied");
        }

        // Sales need to exist and each sale associated with the same business
        Long businessID = 0L;
        for (Sale sale : sales) {
            // First loop
            if (businessID == 0L) businessID = sale.getBusiness().getId();

            if (!saleRepository.existsByIdActiveAndBusiness(sale.getId(), businessID)) {
                throw new Exceptions.BadRequestException("Error at 'ValidateSalesForClient' - Sale doesn't exist or it's not associated with the business: " + businessID);
            }

            // Check every sale's businessID
            if (!Objects.equals(businessID, sale.getBusiness().getId())) {
                throw new Exceptions.BadRequestException("Error at 'ValidateSalesForClient' - Each sale needs to be associated with the same business");
            }
        }

        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);
    }
}

package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.*;
import nicoAntonelli.managefy.entities.dto.ClientCU;
import nicoAntonelli.managefy.repositories.ClientRepository;
import nicoAntonelli.managefy.repositories.SaleRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    // On a new sale context
    public Client CreateClientForNewSale(Client client) {
        // Validate name
        if (client.getName() == null || client.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'CreateClientForNewSale' - Name field was not supplied");
        }

        // Forced initial state
        client.setId(null);
        client.setDeletionDate(null);
        client.setSales(null);

        return clientRepository.save(client);
    }

    public Client CreateClient(ClientCU clientCU, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(clientCU.getBusinessID(), user);

        // Validate name
        if (clientCU.getName() == null || clientCU.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'CreateClient' - Name field was not supplied");
        }

        // Validate associated sales' IDs and load each in a collection
        Set<Sale> sales = ValidateSalesForClient(clientCU, user);

        // New client object with DTO info
        Client client = new Client(clientCU.getName(), clientCU.getDescription(),
                                   clientCU.getEmail(), clientCU.getPhone());

        // Save client first (without sales as nested)
        client = clientRepository.save(client);

        // Set new client ID for every sale
        for (Sale sale : sales) {
            sale.setClientByID(client.getId());
        }

        // Save sales with client set
        saleRepository.saveAll(sales);

        return client;
    }

    public Client UpdateClient(ClientCU clientCU, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(clientCU.getBusinessID(), user);

        // Validate name
        if (clientCU.getName() == null || clientCU.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'UpdateClient' - Name field was not supplied");
        }

        // Validate associated sales' IDs and load each in a collection
        Set<Sale> sales = ValidateSalesForClient(clientCU, user);

        // Validate client existence and obtain it loaded from DB
        Client client = GetOneClient(clientCU.getId(), clientCU.getBusinessID(), user);

        // Merge DTO's client with the original - Don't mess up relation w/sales
        client.setName(clientCU.getName());
        client.setEmail(clientCU.getEmail());
        client.setPhone(clientCU.getPhone());
        client.setDescription(clientCU.getDescription());

        // Save client first (without sales as nested)
        client = clientRepository.save(client);

        // Set existent client ID for every sale
        for (Sale sale : sales) {
            sale.setClientByID(client.getId());
        }

        // Save sales with client set
        saleRepository.saveAll(sales);

        return client;
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

        // Erase client for associated sales too
        List<Sale> sales = saleRepository.findActivesByBusinessAndClient(businessID, clientID);
        for (Sale sale : sales) {
            sale.setClient(null);
        }
        saleRepository.saveAll(sales);

        return clientID;
    }

    private Set<Sale> ValidateSalesForClient(ClientCU clientCU, User user) {
        // At least one sale
        Set<Long> salesIDs = clientCU.getSalesIDs();
        if (salesIDs == null || salesIDs.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSalesForClient' - No sale was supplied");
        }

        // Validate and retrieve the sales fully-loaded from DB
        Set<Sale> sales = new HashSet<>();
        for (Long id : salesIDs) {
            // Check sale existence & business
            Sale sale = saleRepository.findByIdActiveAndBusiness(id, clientCU.getBusinessID()).orElseThrow(
                    () -> new Exceptions.BadRequestException("Error at 'ValidateSalesForClient' - Sale doesn't exist or it's not associated with the user: " + user.getId())
            );

            sales.add(sale);
        }

        return sales;
    }
}

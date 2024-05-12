package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.*;
import nicoAntonelli.managefy.repositories.ClientRepository;
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
    private final SaleService saleService; // Dependency

    @Autowired
    public ClientService(ClientRepository clientRepository,
                         BusinessService businessService,
                         SaleService saleService) {
        this.clientRepository = clientRepository;
        this.businessService = businessService;
        this.saleService = saleService;
    }

    public List<Client> GetClients() {
        return clientRepository.findAllActives();
    }

    public Boolean ExistsClient(Long clientID) {
        return clientRepository.existsByIdActive(clientID);
    }

    public Client GetOneClient(Long clientID) {
        Optional<Client> client = clientRepository.findByIdActive(clientID);
        if (client.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneClient' - Client with ID: " + clientID + " doesn't exist");
        }

        return client.get();
    }

    public Client CreateClient(Client client) {
        client.setId(null);
        client.setDeletionDate(null);

        return clientRepository.save(client);
    }

    public Client UpdateClient(Client client) {
        boolean exists = ExistsClient(client.getId());
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'UpdateClient' - Client with ID: " + client.getId() + " doesn't exist");
        }

        return clientRepository.save(client);
    }

    // Logic deletion (field: deletion date)
    public Long DeleteClient(Long clientID) {
        Client client = GetOneClient(clientID);
        client.setDeletionDate(LocalDateTime.now());
        clientRepository.save(client);

        return clientID;
    }

    private void ValidateSalesForClient(Client client, User user) {
        // At least one sale
        Set<Sale> sales = client.getSales();
        if (sales == null || sales.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSalesForClient' - Any sale was supplied");
        }

        // Sales need to exist, be associated with the user and each sale associated with the same business
        Long businessID = 0L;
        for (Product product : products) {
            if (!productService.ExistsProduct(product, user)) {
                throw new Exceptions.BadRequestException("Error at 'ValidateProductsForSupplier' - Product doesn't exist or it's not associated with the user: " + user.getId());
            }

            // First loop
            if (businessID == 0L) businessID = product.getBusiness().getId();

            // Check every product's businessID
            if (!Objects.equals(businessID, product.getBusiness().getId())) {
                throw new Exceptions.BadRequestException("Error at 'ValidateProductsForSupplier' - Each product needs to be associated with the same business");
            }
        }
    }
}

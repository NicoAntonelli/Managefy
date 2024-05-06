package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Client;
import nicoAntonelli.managefy.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public ClientService(ClientRepository clientRepository, ErrorLogService errorLogService) {
        this.clientRepository = clientRepository;
        this.errorLogService = errorLogService;
    }

    public List<Client> GetClients() {
        return clientRepository.findAll();
    }

    public Boolean ExistsClient(Long clientID) {
        return clientRepository.existsById(clientID);
    }

    public Client GetOneClient(Long clientID) {
        try {
            Optional<Client> client = clientRepository.findById(clientID);
            if (client.isEmpty()) {
                throw new IllegalStateException("Error at 'GetOneClient' - Client with ID: " + clientID + " doesn't exist");
            }

            return client.get();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Client CreateClient(Client client) {
        try {
            client.setId(null);
            client.setDeletionDate(null);

            return clientRepository.save(client);
        }
        catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Client UpdateClient(Client client) {
        try {
            boolean exists = ExistsClient(client.getId());
            if (!exists) {
                throw new IllegalStateException("Error at 'UpdateClient' - Client with ID: " + client.getId() + " doesn't exist");
            }

            return clientRepository.save(client);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    // Logic deletion (field: deletion date)
    public Client DeleteClient(Long clientID) {
        try {
            Client client = GetOneClient(clientID);
            client.setDeletionDate(new Date());
            clientRepository.save(client);

            return client;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }
}

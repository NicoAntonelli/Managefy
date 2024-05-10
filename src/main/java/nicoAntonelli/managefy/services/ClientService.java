package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Client;
import nicoAntonelli.managefy.repositories.ClientRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
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
}

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

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> GetClients() {
        return clientRepository.findAll();
    }

    public Client GetOneClient(Long clientID) {
        Optional<Client> client = clientRepository.findById(clientID);
        if (client.isEmpty()) {
            throw new IllegalStateException("Client with ID: " + clientID + " doesn't exists");
        }

        return client.get();
    }

    public Client CreateClient(Client client) {
        return clientRepository.save(client);
    }

    public Client UpdateClient(Client client) {
        boolean exists = clientRepository.existsById(client.getId());
        if (!exists) {
            throw new IllegalStateException("Client with ID: " + client.getId() + " doesn't exists");
        }

        return clientRepository.save(client);
    }

    // Logic deletion (field: deletion date)
    public Client DeleteClient(Long clientID) {
        Client client = GetOneClient(clientID);
        client.setDeletionDate(new Date());
        clientRepository.save(client);

        return client;
    }
}

package nicoAntonelli.managefy.services;

import nicoAntonelli.managefy.entities.Client;
import nicoAntonelli.managefy.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> GetClients() {
        return clientRepository.findAll();
    }
}

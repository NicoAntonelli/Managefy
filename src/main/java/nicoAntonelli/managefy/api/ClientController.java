package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Client;
import nicoAntonelli.managefy.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/clients")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<Client> GetClients() {
        return clientService.GetClients();
    }

    @GetMapping(path = "{clientID}")
    public Client GetOneClient(@PathVariable("clientID") Long clientID) {
        return clientService.GetOneClient(clientID);
    }

    @PostMapping
    public Client CreateClient(@RequestBody Client client) {
        return clientService.CreateClient(client);
    }

    @PutMapping
    public Client UpdateClient(@RequestBody Client client) {
        return clientService.UpdateClient(client);
    }

    @DeleteMapping(path = "{clientID}")
    public Client DeleteClient(@PathVariable("clientID") Long clientID) {
        return clientService.DeleteClient(clientID);
    }
}

package nicoAntonelli.managefy.service;

import nicoAntonelli.managefy.entities.Client;

import java.util.List;

public class ClientService {
    public List<Client> GetClients() {
        Client client1 = new Client(1L, "Nick", "Food & soda", "nick@mail.com", "123456", null);
        Client client2 = new Client(2L, "Kasp", "Red meat", "kasp@mail.com", "111222", null);
        return List.of(client1, client2);
    }
}

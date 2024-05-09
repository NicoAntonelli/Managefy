package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Client;
import nicoAntonelli.managefy.services.ClientService;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/clients")
public class ClientController {
    private final ClientService clientService;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public ClientController(ClientService clientService, ErrorLogService errorLogService) {
        this.clientService = clientService;
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public Result<List<Client>> GetClients() {
        try {
            List<Client> clients = clientService.GetClients();
            return new Result<>(clients);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @GetMapping(path = "{clientID}")
    public Result<Client> GetOneClient(@PathVariable("clientID") Long clientID) {
        try {
            Client client = clientService.GetOneClient(clientID);
            return new Result<>(client);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PostMapping
    public Result<Client> CreateClient(@RequestBody Client client) {
        try {
            client = clientService.CreateClient(client);
            return new Result<>(client);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PutMapping
    public Result<Client> UpdateClient(@RequestBody Client client) {
        try {
            client = clientService.UpdateClient(client);
            return new Result<>(client);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @DeleteMapping(path = "{clientID}")
    public Result<Client> DeleteClient(@PathVariable("clientID") Long clientID) {
        try {
            Client client = clientService.DeleteClient(clientID);
            return new Result<>(client);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }
}

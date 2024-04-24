package nicoAntonelli.managefy;

import java.util.List;
import nicoAntonelli.managefy.entities.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ManagefyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagefyApplication.class, args);
	}

	@GetMapping
	public List<Client> hello() {
		Client client1 = new Client(1L, "Nick", "Food & soda", "nick@mail.com", "123456", null);
		Client client2 = new Client(2L, "Kasp", "Red meat", "kasp@mail.com", "111222", null);
		return List.of(client1, client2);
	}
}

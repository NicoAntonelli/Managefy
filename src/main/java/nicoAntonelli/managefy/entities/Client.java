package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "clients")
@Data @NoArgsConstructor @AllArgsConstructor
public class Client {
    @Id
    @SequenceGenerator(name = "client_sequence", sequenceName = "client_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String mail;
    @Column(nullable = false)
    private String phone;
    private Date deletionDate; // Nullable

    public Client(String name, String description, String mail,
                  String phone, Date detelionDate) {
        this.name = name;
        this.description = description;
        this.mail = mail;
        this.phone = phone;
        this.deletionDate = detelionDate;
    }
}

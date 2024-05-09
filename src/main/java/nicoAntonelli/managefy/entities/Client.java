package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clients")
@Data @NoArgsConstructor @AllArgsConstructor
public class Client {
    @Id
    @SequenceGenerator(name = "clients_sequence", sequenceName = "clients_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clients_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String phone;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime deletionDate; // Nullable

    @JsonIgnore
    @OneToMany(mappedBy = "client", cascade = { CascadeType.ALL },
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Sale> sales = new HashSet<>();

    public Client(Long id) {
        this.id = id;
    }

    public Client(String name, String description, String email, String phone) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.deletionDate = null;
    }

    public Client(String name, String description, String email,
                  String phone, LocalDateTime deletionDate) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.deletionDate = deletionDate;
    }
}

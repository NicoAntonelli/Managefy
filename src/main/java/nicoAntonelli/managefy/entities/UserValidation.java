package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "userValidations")
@Data @NoArgsConstructor @AllArgsConstructor
public class UserValidation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(
            name = "userID",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "users_userValidations_fk")
    )
    private User user;

    @Column(nullable = false)
    private String code;
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime expiryDate;

    public UserValidation(User user) {
        this.user = user;
        this.code = String.format("%06d", new Random().nextInt(999999));
        this.expiryDate = LocalDateTime.now().plusDays(1);
    }

    public void setUserByID(Long userID) {
        user = new User();
        user.setId(userID);
    }
}

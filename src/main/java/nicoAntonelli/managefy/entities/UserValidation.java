package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Objects;

@Entity
@Table(name = "userValidations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
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

    @Override
    public String toString() {
        return "UserValidation{" +
                "id=" + id +
                ", user=" + user.toStringSafe() +
                ", code='" + code + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }

    // Identity based on ID only
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserValidation other = (UserValidation) o;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}

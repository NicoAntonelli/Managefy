package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userRoles")
@Data @NoArgsConstructor @AllArgsConstructor
public class UserRole {
    @EmbeddedId
    @Column(updatable = false)
    UserRoleKey id;

    @ManyToOne
    @MapsId("userID")
    @JoinColumn(name = "userID")
    User user;

    @ManyToOne
    @MapsId("businessID")
    @JoinColumn(name = "businessID")
    Business business;

    @Column(nullable = false)
    private Boolean isManager;
    @Column(nullable = false)
    private Boolean isAdmin;
    @Column(nullable = false)
    private Boolean isCollaborator;

    public UserRole(User user, Business business, Boolean isManager, Boolean isAdmin, Boolean isCollaborator) {
        this.id = new UserRoleKey(user.getId(), business.getId());
        this.user = user;
        this.business = business;
        this.isManager = isManager;
        this.isAdmin = isAdmin;
        this.isCollaborator = isCollaborator;
    }
}

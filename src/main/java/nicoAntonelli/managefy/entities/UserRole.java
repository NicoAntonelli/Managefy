package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(UserRoleKey.class)
@Table(name = "userRoles")
@Data @NoArgsConstructor @AllArgsConstructor
public class UserRole {
    @Id
    @ManyToOne
    @JoinColumn(
            name = "userID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "users_userRoles_fk")
    )
    User user;

    @Id
    @ManyToOne
    @JoinColumn(
            name = "businessID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "business_userRoles_fk")
    )
    Business business;

    @Column(nullable = false)
    private Boolean isManager;
    @Column(nullable = false)
    private Boolean isAdmin;
    @Column(nullable = false)
    private Boolean isCollaborator;

    public UserRole(Boolean isManager, Boolean isAdmin, Boolean isCollaborator) {
        this.isManager = isManager;
        this.isAdmin = isAdmin;
        this.isCollaborator = isCollaborator;
    }

    public UserRoleKey getId() {
        return new UserRoleKey(getUser().getId(), getBusiness().getId());
    }

    public void setBusinessByID(Long businessByID) {
        business = new Business();
        business.setId(businessByID);
    }

    public void setUserByID(Long userID) {
        user = new User();
        user.setId(userID);
    }
}

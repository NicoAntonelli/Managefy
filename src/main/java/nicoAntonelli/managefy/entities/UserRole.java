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
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(
            name = "businessID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "businesses_userRoles_fk")
    )
    private Business business;

    @Column(nullable = false)
    private Boolean isManager = false;
    @Column(nullable = false)
    private Boolean isAdmin = false;
    @Column(nullable = false)
    private Boolean isCollaborator = false;

    public UserRole(User user, Business business) {
        this.user = user;
        this.business = business;
    }

    public UserRole(Boolean isManager, Boolean isAdmin, Boolean isCollaborator) {
        this.isManager = isManager;
        this.isAdmin = isAdmin;
        this.isCollaborator = isCollaborator;
    }

    public UserRole(Long userID, Long businessID, String role) {
        setUserByID(userID);
        setBusinessByID(businessID);
        if (!setRoleByText(role)) setRoleByText("Collaborator"); // Default
    }

    public UserRoleKey getId() {
        return new UserRoleKey(getUser().getId(), getBusiness().getId());
    }

    public void setUserByID(Long userID) {
        user = new User(userID);
    }

    public void setBusinessByID(Long businessByID) {
        business = new Business(businessByID);
    }

    public Boolean setRoleByText(String role) {
        this.isManager = false;
        this.isAdmin = false;
        this.isCollaborator = false;

        switch(role.toLowerCase()) {
            case "manager" -> this.isManager = true;
            case "admin" -> this.isAdmin = true;
            case "collaborator" -> this.isCollaborator = true;
            default -> { return false; }
        }

        return true;
    }
}

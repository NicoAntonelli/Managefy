package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@IdClass(UserRoleKey.class)
@Table(name = "userRoles")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class UserRole {
    @Id
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(
            name = "userID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "users_userRoles_fk")
    )
    private User user;

    @Id
    @ToString.Exclude
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

    // Identity based on composite key (user + business)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserRole other = (UserRole) o;
        if (getUser() == null || getBusiness() == null || other.getUser() == null || other.getBusiness() == null) return false;
        return getUser().getId() != null && getBusiness().getId() != null
                && Objects.equals(getUser().getId(), other.getUser().getId())
                && Objects.equals(getBusiness().getId(), other.getBusiness().getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}

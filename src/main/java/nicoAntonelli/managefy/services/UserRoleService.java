package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.entities.UserRoleKey;
import nicoAntonelli.managefy.repositories.UserRoleRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final BusinessService businessService; // Dependency
    private final UserService userService; // Dependency

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository,
                           BusinessService businessService,
                           UserService userService) {
        this.userRoleRepository = userRoleRepository;
        this.businessService = businessService;
        this.userService = userService;
    }

    public List<UserRole> GetUserRoles(User user) {
        return userRoleRepository.findByUser(user.getId());
    }

    public List<UserRole> GetUserRolesByBusiness(Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return userRoleRepository.findByBusiness(businessID);
    }

    public UserRole GetOneUserRoleForOther(Long otherUserID, Long businessID, User user) {
        // Validate business, logged user and role
        businessService.GetOneBusiness(businessID, user);

        // Validate other user
        userService.GetOneUser(otherUserID);

        UserRoleKey userRoleKey = new UserRoleKey(otherUserID, businessID);

        Optional<UserRole> userRole = userRoleRepository.findById(userRoleKey);
        if (userRole.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneUserRoleForOther' - UserRole with ID: " + userRoleKey + " doesn't exist");
        }

        return userRole.get();
    }

    public UserRole GetOneUserRoleForLogged(Long businessID, User user) {
        // Validate business, logged user and role
        businessService.GetOneBusiness(businessID, user);

        UserRoleKey userRoleKey = new UserRoleKey(user.getId(), businessID);

        Optional<UserRole> userRole = userRoleRepository.findById(userRoleKey);
        if (userRole.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneUserRoleForLogged' - UserRole with ID: " + userRoleKey + " doesn't exist");
        }

        return userRole.get();
    }

    public UserRole CreateUserRole(Long otherUserID, Long businessID, String role, User user) {
        // Validate business and current user and other user's roles
        UserRole currentUserRole = GetOneUserRoleForLogged(businessID, user);
        userService.GetOneUser(otherUserID);

        // Validate role
        if (!List.of("collaborator", "admin", "manager").contains(role)) {
            throw new Exceptions.BadRequestException("Error at 'CreateUserRole' - Invalid role: " + role);
        }

        UserRole roleToCreate = new UserRole(otherUserID, businessID, role);

        // Validate authorization for role creation
        if (roleToCreate.getIsManager()) {
            throw new Exceptions.UnauthorizedException("Error at 'CreateUserRole' - Can't create another manager role!");
        }

        if (roleToCreate.getIsAdmin() && !currentUserRole.getIsManager()) {
            throw new Exceptions.UnauthorizedException("Error at 'CreateUserRole' - Can't create an admin role for other user if you don't have a manager role");
        }

        if (roleToCreate.getIsCollaborator() && currentUserRole.getIsCollaborator()) {
            throw new Exceptions.UnauthorizedException("Error at 'CreateUserRole' - Can't create a collaborator role for other user if you don't have an admin or manager role");
        }

        return userRoleRepository.save(roleToCreate);
    }

    public void CreateUserRoleForNewBusiness(Long userID, Long businessID) {
        // Create user role (Manager)
        UserRole userRole = new UserRole(userID, businessID, "manager");

        userRoleRepository.save(userRole);
    }

    public UserRole UpdateUserRole(Long otherUserID, Long businessID, String role, User user) {
        // Validate business and current user and other user's roles
        UserRole roleToUpdate = GetOneUserRoleForOther(otherUserID, businessID, user);
        UserRole currentUserRole = GetOneUserRoleForLogged(businessID, user);

        // Validate role
        if (!roleToUpdate.setRoleByText(role)) {
            throw new Exceptions.BadRequestException("Error at 'UpdateUserRole' - Invalid role: " + role);
        }

        // Validate authorization for role creation
        if (roleToUpdate.getIsManager() && !currentUserRole.getIsManager()) {
            throw new Exceptions.UnauthorizedException("Error at 'UpdateUserRole' - Only the manager can update it's own role!");
        }

        if (roleToUpdate.getIsAdmin() && !currentUserRole.getIsManager()) {
            throw new Exceptions.UnauthorizedException("Error at 'UpdateUserRole' - Can't update an admin role for other user if you don't have a manager role");
        }

        if (roleToUpdate.getIsCollaborator() && currentUserRole.getIsCollaborator()) {
            throw new Exceptions.UnauthorizedException("Error at 'UpdateUserRole' - Can't update a collaborator role for other user if you don't have an admin or manager role");
        }

        return userRoleRepository.save(roleToUpdate);
    }

    public UserRole TransferManagerRole(Long otherUserID, long businessID, User user) {
        // Validate business and current user and other user's roles
        UserRole otherUserRole = GetOneUserRoleForOther(otherUserID, businessID, user);
        UserRole currentUserRole = GetOneUserRoleForLogged(businessID, user);

        // Validate authorization for role creation
        if (otherUserRole.getIsManager()) {
            throw new Exceptions.UnauthorizedException("Error at 'TransferManagerRole' - The other user is the manager already!");
        }

        if (!currentUserRole.getIsManager()) {
            throw new Exceptions.UnauthorizedException("Error at 'TransferManagerRole' - You don't have a manager role to transfer");
        }

        otherUserRole.setRoleByText("manager");
        currentUserRole.setRoleByText("admin");

        userRoleRepository.save(currentUserRole);
        return userRoleRepository.save(otherUserRole);
    }

    public UserRoleKey DeleteUserRole(Long otherUserID, long businessID, User user) {
        // Validate business and current user and other user's roles
        UserRole roleToDelete = GetOneUserRoleForOther(otherUserID, businessID, user);
        UserRole currentUserRole = GetOneUserRoleForLogged(businessID, user);

        // Validate authorization for role creation
        if (roleToDelete.getIsManager()) {
            throw new Exceptions.UnauthorizedException("Error at 'DeleteUserRole' - Can't delete the manager role!");
        }

        if (roleToDelete.getIsAdmin() && !currentUserRole.getIsManager()) {
            throw new Exceptions.UnauthorizedException("Error at 'DeleteUserRole' - Can't delete an admin role for other user if you don't have a manager role");
        }

        if (roleToDelete.getIsCollaborator() && currentUserRole.getIsCollaborator()) {
            throw new Exceptions.UnauthorizedException("Error at 'DeleteUserRole' - Can't delete a collaborator role for other user if you don't have an admin or manager role");
        }

        userRoleRepository.deleteById(roleToDelete.getId());
        return roleToDelete.getId();
    }

    public UserRoleKey LeaveUserRole(long businessID, User user) {
        // Validate business and current user role
        UserRole currentUserRole = GetOneUserRoleForLogged(businessID, user);

        // You can leave a business before transfer manager role
        if (currentUserRole.getIsManager()) {
            throw new Exceptions.UnauthorizedException("Error at 'LeaveUserRole' - First you need to transfer the manager role to other user!");
        }

        userRoleRepository.deleteById(currentUserRole.getId());
        return currentUserRole.getId();
    }
}

package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.UserValidation;
import nicoAntonelli.managefy.entities.dto.*;
import nicoAntonelli.managefy.repositories.UserRepository;
import nicoAntonelli.managefy.repositories.UserValidationRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.JWTHelper;
import nicoAntonelli.managefy.utils.PasswordEncoder;
import nicoAntonelli.managefy.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserValidationRepository userValidationRepository; // Dependency
    private final BusinessService businessService; // Dependency
    private final EmailService emailService; // Dependency
    private final NotificationService notificationService; // Dependency
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserValidationRepository userValidationRepository,
                       BusinessService businessService,
                       EmailService emailService,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.userValidationRepository = userValidationRepository;
        this.businessService = businessService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.passwordEncoder = PasswordEncoder.getInstance();
    }

    public List<User> GetUsers() {
        return userRepository.findAll();
    }

    public User GetOneUser(Long userID) {
        Optional<User> user = userRepository.findById(userID);
        if (user.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneUser' - User with ID: " + userID + " doesn't exist");
        }

        return user.get();
    }

    public User GetOneUserByEmail(String email) {
        // Email format validation
        if (!Validation.email(email)) {
            throw new Exceptions.BadRequestException("Error at 'GetOneUserByEmail' - Email bad formatted: " + email);
        }

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneUserByEmail' - User with email: " + email + " doesn't exist");
        }

        return user.get();
    }

    public Token CreateUser(Registration registration) {
        // Validate fields
        String email = registration.getEmail();
        if (!Validation.email(email)) {
            throw new Exceptions.BadRequestException("Error at 'ValidateUser' - Email bad formatted: " + email);
        }

        String password = registration.getPassword();
        if (!Validation.password(password)) {
            throw new Exceptions.BadRequestException("Error at 'ValidateUser' - Password bad formatted for the attempted email: " + email);
        }

        // Email unique validation
        Optional<User> possibleUser = userRepository.findByEmail(email);
        if (possibleUser.isPresent()) {
            throw new Exceptions.BadRequestException("Error at 'CreateUser' - Email '" + email + "' already taken");
        }

        // Encode password
        password = passwordEncoder.encode(password);

        // Save new user
        User user = new User(email, password, registration.getName());
        user = userRepository.save(user);

        // Generate JWT Token
        String JWT = JWTHelper.generateToken(user.toStringSafe());

        return new Token(JWT, user.getId());
    }

    public Token Login(Login login) {
        String email = login.getEmail();
        if (!Validation.email(email)) {
            throw new Exceptions.BadRequestException("Error at 'ValidateUser' - Email bad formatted: " + email);
        }

        String password = login.getPassword();
        if (!Validation.password(password)) {
            throw new Exceptions.BadRequestException("Error at 'ValidateUser' - Password bad formatted for the attempted email: " + email);
        }

        // Encode password
        password = passwordEncoder.encode(password);

        // Email & password comparison against DB
        User user = GetOneUserByEmail(email);
        if (!Objects.equals(email, user.getEmail()) || !Objects.equals(password, user.getPassword())) {
            throw new Exceptions.UnauthorizedException("Error at 'ValidateUser' - Email or password mismatch, attempted email: " + email);
        }

        // Generate JWT Token
        String JWT = JWTHelper.generateToken(user.toStringSafe());

        return new Token(JWT, user.getId());
    }

    // Only logged-user can update itself
    public Token UpdateUser(UserU userU) {
        // Validate fields
        String email = userU.getEmail();
        if (!Validation.email(email)) {
            throw new Exceptions.BadRequestException("Error at 'UpdateUser' - Email bad formatted: " + email);
        }

        String password = userU.getPassword();
        if (!Validation.password(password)) {
            throw new Exceptions.BadRequestException("Error at 'UpdateUser' - Password bad formatted for the attempted email: " + email);
        }

        // Disable email notifications if not supplied
        if (userU.getEmailNotifications() == null) userU.setEmailNotifications(false);

        // Email unique validation
        Optional<User> possibleUser = userRepository.findByEmail(email);
        if (possibleUser.isPresent()) {
            // Only fail validation if it's not the same user
            if (!Objects.equals(possibleUser.get().getId(), userU.getId())) {
                throw new Exceptions.BadRequestException("Error at 'UpdateUser' - Email '" + email + "' already taken");
            }
        }

        // Encode password
        password = passwordEncoder.encode(password);

        // Obtain loaded user from DB
        User user = GetOneUser(userU.getId());

        // Merge DTO's user with the original - Don't mess up validation nor relation with other entities
        user.setEmail(email);
        user.setPassword(password);
        user.setName(userU.getName());
        user.setEmailNotifications(userU.getEmailNotifications());

        // Save user
        user = userRepository.save(user);

        // Notification for update user
        NotificationC notification = new NotificationC("Your user account was updated successfully", "low");
        notificationService.CreateNotification(notification, user);

        // Generate new JWT Token (with updated user)
        String JWT = JWTHelper.generateToken(user.toStringSafe());

        return new Token(JWT, user.getId());
    }

    // Only logged-user can generate its own code
    public Boolean GenerateUserValidation(User user) {
        if (user.getValidated()) {
            throw new Exceptions.BadRequestException("Error at 'GenerateUserValidation' - User with ID: " + user.getId() + " has already been validated");
        }

        // Replace any existing entity
        UserValidation userValidation = new UserValidation(user);
        userValidationRepository.save(userValidation);

        // Send an email with the code to the user
        emailService.CodeValidationEmail(user.getEmail(), userValidation.getCode());

        // Notification user validation sent
        NotificationC notification = new NotificationC("We sent you a code via email to verify your user account. Please check your inbox", "priority");
        notificationService.CreateNotification(notification, user);

        return true;
    }

    // Only logged-user can validate itself
    public Token ValidateUser(String code, User user) {
        Long userID = user.getId();
        if (user.getValidated()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateUser' - User with ID: " + user.getId() + " has already been validated");
        }

        UserValidation userValidation = userValidationRepository.findByUser(userID).orElseThrow(
                () -> new Exceptions.BadRequestException("Error at 'ValidateUser' - No validation code was generated for the User ID: " + userID)
        );

        // Code and expiry date validations
        if (!Objects.equals(userValidation.getCode(), code)) {
            throw new Exceptions.UnauthorizedException("Error at 'ValidateUser' - Validation code mismatch for the User ID: " + userID);
        }
        if (userValidation.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new Exceptions.UnauthorizedException("Error at 'ValidateUser' - Validation code has expired for the User ID: " + userID);
        }

        // Get user with all its fields loaded
        user = GetOneUser(userID);

        // Update user with validation OK
        user.setValidated(true);
        user = userRepository.save(user);

        // Notification user validation sent
        NotificationC notification = new NotificationC("Your user has been correctly verified! You can now start operating with Managefy!", "normal");
        notificationService.CreateNotification(notification, user);

        // Generate new JWT Token (with now-validated user)
        String JWT = JWTHelper.generateToken(user.toStringSafe());

        return new Token(JWT, userID);
    }

    // Only logged-user can delete its own account
    public Long DeleteUser(User user) {
        // Businesses participation
        List<Business> associatedBusinesses = businessService.GetBusinesses(user);
        if (!associatedBusinesses.isEmpty()) {
            throw new Exceptions.UnauthorizedException("Error at 'DeleteUser' - You have participation in one or more businesses. First leave or delete them.");
        }

        // Clean validation table also
        Long userID = user.getId();
        Optional<UserValidation> optionalValidation = userValidationRepository.findByUser(userID);
        optionalValidation.ifPresent(userValidationRepository::delete);

        // Delete user (also notifications by delete cascade)
        userRepository.deleteById(userID);

        return userID;
    }
}

package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.UserValidation;
import nicoAntonelli.managefy.entities.dto.Login;
import nicoAntonelli.managefy.entities.dto.Registration;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserValidationRepository userValidationRepository) {
        this.userRepository = userRepository;
        this.userValidationRepository = userValidationRepository;
        this.passwordEncoder = PasswordEncoder.getInstance();
    }

    public List<User> GetUsers() {
        return userRepository.findAll();
    }

    public Boolean ExistsUser(Long userID) {
        return userRepository.existsById(userID);
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

    public String CreateUser(Registration registration) {
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
        return JWTHelper.generateToken(user.toStringSafe());
    }

    public String Login(Login login) {
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
        return JWTHelper.generateToken(user.toStringSafe());
    }

    public User UpdateUser(User user) {
        boolean exists = ExistsUser(user.getId());
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'UpdateUser' - User with ID: " + user.getId() + " doesn't exist");
        }
        // Email unique validation
        Optional<User> possibleUser = userRepository.findByEmail(user.getEmail());
        if (possibleUser.isPresent()) {
            // Only fail validation if it's not the same user
            if (!Objects.equals(possibleUser.get().getId(), user.getId())) {
                throw new Exceptions.BadRequestException("Error at 'UpdateUser' - Email '" + user.getEmail() + "' already taken");
            }
        }

        return userRepository.save(user);
    }

    public Boolean GenerateUserValidation(Long userID) {
        User user = GetOneUser(userID);

        // Replace any existing entity
        UserValidation userValidation = new UserValidation(user);
        userValidationRepository.save(userValidation);

        return true;
    }

    public Boolean ValidateUser(Long userID, String code) {
        User user = GetOneUser(userID);
        if (user.getValidated()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateUser' - User with ID: " + user.getId() + " has already been validated");
        }

        Optional<UserValidation> optionalValidation = userValidationRepository.findByUser(userID);
        if (optionalValidation.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateUser' - No validation code was generated for the User ID: " + userID);
        }

        // Code and expiry date validations
        UserValidation userValidation = optionalValidation.get();
        if (!Objects.equals(userValidation.getCode(), code)) {
            throw new Exceptions.UnauthorizedException("Error at 'ValidateUser' - Validation code mismatch for the User ID: " + userID);
        }
        if (userValidation.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new Exceptions.UnauthorizedException("Error at 'ValidateUser' - Validation code has expired for the User ID: " + userID);
        }

        // Update user with validation OK
        user.setValidated(true);
        userRepository.save(user);

        return true;
    }

    public Long DeleteUser(Long userID) {
        boolean exists = ExistsUser(userID);
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'DeleteUser' - User with ID: " + userID + " doesn't exist");
        }

        userRepository.deleteById(userID);
        return userID;
    }
}

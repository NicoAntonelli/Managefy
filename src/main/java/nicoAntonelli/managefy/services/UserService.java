package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.dto.Login;
import nicoAntonelli.managefy.entities.dto.Registration;
import nicoAntonelli.managefy.utils.PasswordEncoder;
import nicoAntonelli.managefy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
            throw new IllegalStateException("Error at 'GetOneUser' - User with ID: " + userID + " doesn't exist");
        }

        return user.get();
    }

    public User GetOneUserByMail(String mail) {
        // Mail format validation (TO-DO: add regex)
        if (!ValidateMail(mail)) {
            throw new IllegalStateException("Error at 'GetOneUserByMail' - Mail bad formatted: " + mail);
        }

        Optional<User> user = userRepository.findByMail(mail);
        if (user.isEmpty()) {
            throw new IllegalStateException("Error at 'GetOneUserByMail' - User with mail: " + mail + " doesn't exist");
        }

        return user.get();
    }

    public User CreateUser(Registration registration) {
        // Validate fields
        String mail = registration.getMail();
        if (!ValidateMail(mail)) {
            throw new IllegalStateException("Error at 'ValidateUser' - Mail bad formatted: " + mail);
        }

        String password = registration.getPassword();
        if (!ValidatePassword(password)) {
            throw new IllegalStateException("Error at 'ValidateUser' - Password bad formatted for the attempted mail: " + mail);
        }

        // Mail unique validation
        Optional<User> possibleUser = userRepository.findByMail(mail);
        if (possibleUser.isPresent()) {
            throw new IllegalStateException("Error at 'CreateUser' - Mail '" + mail + "' already taken");
        }

        // Encode password
        password = passwordEncoder.Encode(password);

        User user = new User(mail, password, registration.getName());
        return userRepository.save(user);
    }

    public User Login(Login login) {
        String mail = login.getMail();
        if (!ValidateMail(mail)) {
            throw new IllegalStateException("Error at 'ValidateUser' - Mail bad formatted: " + mail);
        }

        String password = login.getPassword();
        if (!ValidatePassword(password)) {
            throw new IllegalStateException("Error at 'ValidateUser' - Password bad formatted for the attempted mail: " + mail);
        }

        // Encode password
        password = passwordEncoder.Encode(password);

        // Mail & password comparison against DB
        User user = GetOneUserByMail(mail);
        if (!Objects.equals(mail, user.getMail()) || !Objects.equals(password, user.getPassword())) {
            throw new SecurityException("Error at 'ValidateUser' - Mail or password mismatch, attempted mail: " + mail);
        }

        return user;
    }

    public User UpdateUser(User user) {
        boolean exists = ExistsUser(user.getId());
        if (!exists) {
            throw new IllegalStateException("Error at 'UpdateUser' - User with ID: " + user.getId() + " doesn't exist");
        }
        // Mail unique validation
        Optional<User> possibleUser = userRepository.findByMail(user.getMail());
        if (possibleUser.isPresent()) {
            // Only fail validation if it's not the same user
            if (!Objects.equals(possibleUser.get().getId(), user.getId())) {
                throw new IllegalStateException("Error at 'UpdateUser' - Mail '" + user.getMail() + "' already taken");
            }
        }

        return userRepository.save(user);
    }

    public Long DeleteUser(Long userID) {
        boolean exists = ExistsUser(userID);
        if (!exists) {
            throw new IllegalStateException("Error at 'DeleteUser' - User with ID: " + userID + " doesn't exist");
        }

        userRepository.deleteById(userID);
        return userID;
    }

    private boolean ValidateMail(String mail) {
        // Mail format validation (TO-DO: add regex)
        return mail != null && !mail.isBlank();
    }

    private boolean ValidatePassword(String password) {
        // Password format validation (TO-DO: add regex)
        return password != null && !password.isBlank();
    }
}

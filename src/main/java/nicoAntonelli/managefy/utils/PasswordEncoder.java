package nicoAntonelli.managefy.utils;

import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Wrapper over BCrypt - Each hash embeds its own random salt
public final class PasswordEncoder {
    @Getter
    private static final PasswordEncoder instance = new PasswordEncoder();

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private PasswordEncoder() {}

    public String encode(String password) {
        return encoder.encode(password);
    }

    // Compares a raw password against a stored hash
    public Boolean matches(String password, String encodedPassword) {
        if (password == null || encodedPassword == null) return false;
        return encoder.matches(password, encodedPassword);
    }
}

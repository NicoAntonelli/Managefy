package nicoAntonelli.managefy.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class PasswordEncoder {
    private static PasswordEncoder instance;

    @SuppressWarnings("SpellCheckingInspection")
    private final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    private final String secretPassword = "SecretPW:"; // To-do: set this as environment variable
    private final byte[] salt;

    private PasswordEncoder() throws NoSuchAlgorithmException {
        // Salt generation
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        this.salt = salt;
    }

    public static PasswordEncoder getInstance() {
        try {
            if (instance == null) instance = new PasswordEncoder();
            return instance;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public String encode(String password) {
        try {
            password = secretPassword + password;
            int iterationCount = 65536; // 2^16
            int hashLength = 128; // 2^7
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, hashLength);

            byte[] encodedPassword = factory.generateSecret(spec).getEncoded();
            return new String(encodedPassword, StandardCharsets.UTF_8);
        }
        catch (Exception ex) {
            return null;
        }
    }
}

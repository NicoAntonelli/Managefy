package nicoAntonelli.managefy.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public final class PasswordEncoder {
    private static PasswordEncoder instance;

    @SuppressWarnings("SpellCheckingInspection")
    private final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    private final byte[] salt;

    private PasswordEncoder() throws NoSuchAlgorithmException {
        // Salt generation
        SecureRandom random = new SecureRandom(new byte[8]);
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
            // Config spec
            int iterationCount = 65536; // 2^16
            int hashLength = 256; // 2^8
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, hashLength);

            // Encode password
            byte[] encodedPassword = factory.generateSecret(spec).getEncoded();

            // Return as Base64 string
            return Base64.getEncoder().encodeToString(encodedPassword);
        }
        catch (Exception ex) {
            return null;
        }
    }
}

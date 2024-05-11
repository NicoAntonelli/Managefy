package nicoAntonelli.managefy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class Validation {
    // Non-capturing regex for primitives
    public static final String paramString  = "[a-zA-Z]+";
    public static final String paramInteger = "[\\d]+";
    public static final String paramDecimal = "(?:[0-9]*[.])?[0-9]+";
    public static final String paramBoolean = "(?i)(?:true|false)";

    // At least one uppercase and lowercase letters, one digit and min 8 total length
    private static final Pattern passwordRegex = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", Pattern.CASE_INSENSITIVE);

    // Basic mail validation (Non-RFC822 compliant)
    private static final Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean password(String value) {
        if (value == null) return false;

        Matcher matcher = passwordRegex.matcher(value);
        return matcher.matches();
    }

    public static boolean email(String value) {
        if (value == null) return false;

        Matcher matcher = emailRegex.matcher(value);
        return matcher.matches();
    }
}

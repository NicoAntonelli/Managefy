package nicoAntonelli.managefy.entities.helpTypes;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public final class DateFormatterSingleton {
    private static DateFormatterSingleton instance;
    public DateTimeFormatter value;

    // Default value
    private DateFormatterSingleton() {
        this.value = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd[ HH:mm:ss]")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();
    }

    // Custom value
    private DateFormatterSingleton(String value) {
        this.value = DateTimeFormatter.ofPattern(value);
    }

    public static DateFormatterSingleton getInstance() {
        if (instance == null) instance = new DateFormatterSingleton();
        return instance;
    }

    public static DateFormatterSingleton getInstance(String value) {
        if (instance == null) instance = new DateFormatterSingleton(value);

        // Replace unique instance value before return
        instance.value = DateTimeFormatter.ofPattern(value);
        return instance;
    }
}

package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "errorLogs")
@Data @NoArgsConstructor @AllArgsConstructor
public class ErrorLog {
    // Backend-originated Error Log
    public static final String SERVER = "Managefy-server";

    // Client-originated Error Log
    public static final String CLIENT = "Managefy-client";

    @Id
    @SequenceGenerator(name = "errorLogs_sequence", sequenceName = "errorLogs_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "errorLogs_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime date;
    @Column(nullable = false)
    private String origin;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String innerException; // Nullable
    private String httpCode; // Nullable
    private String browser; // Nullable
    private String userIPAddress; // Nullable

    public ErrorLog(Long id) {
        this.id = id;
    }

    public ErrorLog(String description, String origin, String httpCode, String innerException) {
        this.date = LocalDateTime.now();
        this.origin = origin;
        this.description = description;
        if (innerException != null && !innerException.isEmpty()) this.innerException = innerException;
        this.httpCode = httpCode;
    }

    public ErrorLog(String description, String origin, String httpCode) {
        this.date = LocalDateTime.now();
        this.origin = origin;
        this.description = description;
        this.httpCode = httpCode;
    }

    // Frontend errors
    public ErrorLog(String description, String origin, LocalDateTime date, String browser, String userIPAddress) {
        this.date = date;
        this.origin = origin;
        this.description = description;
        this.browser = browser;
        this.userIPAddress = userIPAddress;
    }
}

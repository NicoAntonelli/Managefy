package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.ErrorLog;
import nicoAntonelli.managefy.utils.DateFormatterSingleton;
import nicoAntonelli.managefy.repositories.ErrorLogRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ErrorLogService {
    private final ErrorLogRepository errorLogRepository;
    private final DateFormatterSingleton dateFormatterSingleton;

    @Autowired
    public ErrorLogService(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
        this.dateFormatterSingleton = DateFormatterSingleton.getInstance();
    }

    public List<ErrorLog> GetErrors() {
        return errorLogRepository.findAll();
    }

    public List<ErrorLog> GetBackendErrorsByInterval(String initialDate, String finalDate) {
        if (initialDate == null || finalDate == null
            || initialDate.isBlank() || finalDate.isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'GetBackendErrorsByInterval' - Both start and end dates must be supplied");
        }

        LocalDateTime startDate, endDate;
        try {
            startDate = LocalDateTime.parse(initialDate, dateFormatterSingleton.value);
            endDate = LocalDateTime.parse(finalDate, dateFormatterSingleton.value);
        }
        catch(Exception ex) {
            throw new Exceptions.BadRequestException("Error at 'GetBackendErrorsByInterval' - Both start and end dates must be a valid date form", ex);
        }

        return errorLogRepository.findByOriginAndInterval(ErrorLog.SERVER, startDate, endDate);
    }

    public List<ErrorLog> GetFrontendErrorsByInterval(String initialDate, String finalDate) {
        if (initialDate == null || finalDate == null
                || initialDate.isBlank() || finalDate.isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'GetFrontendErrorsByInterval' - Both start and end dates must be supplied");
        }

        LocalDateTime startDate, endDate;
        try {
            startDate = LocalDateTime.parse(initialDate, dateFormatterSingleton.value);
            endDate = LocalDateTime.parse(finalDate, dateFormatterSingleton.value);
        }
        catch(Exception ex) {
            throw new Exceptions.BadRequestException("Error at 'GetFrontendErrorsByInterval' - Both start and end dates must be a valid date form", ex);
        }

        return errorLogRepository.findByOriginAndInterval(ErrorLog.CLIENT, startDate, endDate);
    }

    public void SetBackendError(String description, String httpCode, Throwable innerException) {
        // Empty error message
        if (description.isBlank()) return;

        String innerExceptionMessage = innerException != null ? innerException.getMessage() : null;
        ErrorLog errorLog = new ErrorLog(description, ErrorLog.SERVER, httpCode, innerExceptionMessage);

        errorLogRepository.save(errorLog);
    }

    public Boolean SetFrontendError(ErrorLog errorLog) {
        try {
            // Empty error message
            if (errorLog.getDescription().isBlank()) return false;

            errorLog.setId(null);
            errorLog.setOrigin(ErrorLog.CLIENT);
            if (errorLog.getDate() == null) errorLog.setDate(LocalDateTime.now());

            errorLogRepository.save(errorLog);

            return true;
        } catch(Exception ex) {
            return false;
        }
    }
}

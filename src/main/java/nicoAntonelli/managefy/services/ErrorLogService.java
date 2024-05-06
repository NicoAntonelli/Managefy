package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.ErrorLog;
import nicoAntonelli.managefy.repositories.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ErrorLogService {
    private final ErrorLogRepository errorLogRepository;

    @Autowired
    public ErrorLogService(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    public List<ErrorLog> GetErrors() {
        return errorLogRepository.findAll();
    }

    public List<ErrorLog> GetBackendErrorsByInterval(Date initialDate, Date finalDate) {
        return errorLogRepository.findByOrigin(ErrorLog.SERVER, initialDate, finalDate);
    }

    public List<ErrorLog> GetFrontendErrorsByInterval(Date initialDate, Date finalDate) {
        return errorLogRepository.findByOrigin(ErrorLog.CLIENT, initialDate, finalDate);
    }

    public void SetBackendError(String description) {
        // Empty error message
        if (description.isBlank()) return;

        ErrorLog errorLog = new ErrorLog(description, ErrorLog.SERVER);
        errorLogRepository.save(errorLog);
    }

    public Boolean SetFrontendError(ErrorLog errorLog) {
        try {
            // Empty error message
            if (errorLog.getDescription().isBlank()) return false;

            errorLog.setId(null);
            errorLog.setOrigin(ErrorLog.CLIENT);
            if (errorLog.getDate() == null) errorLog.setDate(new Date());

            errorLogRepository.save(errorLog);

            return true;
        } catch(Exception ex) {
            return false;
        }
    }
}

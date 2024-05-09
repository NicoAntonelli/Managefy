package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.ErrorLog;
import nicoAntonelli.managefy.utils.Result;
import nicoAntonelli.managefy.services.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/errorLogs")
public class ErrorLogController {
    private final ErrorLogService errorLogService;

    @Autowired
    public ErrorLogController(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public Result<List<ErrorLog>> GetErrorLogs() {
        try {
            List<ErrorLog> errors = errorLogService.GetErrors();
            return new Result<>(errors);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @GetMapping(path = "front")
    public Result<List<ErrorLog>> GetFrontendErrors(@RequestParam String from,
                                                    @RequestParam String to) {
        try {
            List<ErrorLog> errors = errorLogService.GetFrontendErrorsByInterval(from, to);
            return new Result<>(errors);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @GetMapping(path = "back")
    public Result<List<ErrorLog>> GetBackendErrorsByInterval(@RequestParam String from,
                                                             @RequestParam String to) {
        try {
            List<ErrorLog> errors = errorLogService.GetBackendErrorsByInterval(from, to);
            return new Result<>(errors);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @PostMapping
    public Result<Boolean> SetFrontendError(@RequestBody ErrorLog errorLog) {
        try {
            Boolean operationResult = errorLogService.SetFrontendError(errorLog);
            return new Result<>(operationResult);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }
}

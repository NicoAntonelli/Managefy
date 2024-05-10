package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.ErrorLog;
import nicoAntonelli.managefy.services.AuthService;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/errorLogs")
public class ErrorLogController {
    private final ErrorLogService errorLogService;
    private final AuthService authService; // Dependency

    @Autowired
    public ErrorLogController(ErrorLogService errorLogService,
                              AuthService authService) {
        this.errorLogService = errorLogService;
        this.authService = authService;
    }

    @GetMapping
    public Result<List<ErrorLog>> GetErrorLogs(@RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "GetErrorLogs");

            List<ErrorLog> errors = errorLogService.GetErrors();
            return new Result<>(errors);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @GetMapping(path = "front")
    public Result<List<ErrorLog>> GetFrontendErrors(@RequestParam String from,
                                                    @RequestParam String to,
                                                    @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "GetFrontendErrors");

            List<ErrorLog> errors = errorLogService.GetFrontendErrorsByInterval(from, to);
            return new Result<>(errors);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @GetMapping(path = "back")
    public Result<List<ErrorLog>> GetBackendErrorsByInterval(@RequestParam String from,
                                                             @RequestParam String to,
                                                             @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "GetBackendErrorsByInterval");

            List<ErrorLog> errors = errorLogService.GetBackendErrorsByInterval(from, to);
            return new Result<>(errors);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PostMapping
    public Result<Boolean> SetFrontendError(@RequestBody ErrorLog errorLog,
                                            @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "SetFrontendError");

            Boolean operationResult = errorLogService.SetFrontendError(errorLog);
            return new Result<>(operationResult);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }
}

package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.ErrorLog;
import nicoAntonelli.managefy.services.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "api/errorLogs")
public class ErrorLogController {
    private final ErrorLogService errorLogService;

    @Autowired
    public ErrorLogController(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public List<ErrorLog> GetErrorLogs() {
        return errorLogService.GetErrors();
    }

    @GetMapping(path = "front")
    public List<ErrorLog> GetFrontendErrors(@RequestParam Date from,
                                            @RequestParam Date to) {
        return errorLogService.GetFrontendErrorsByInterval(from, to);
    }

    @GetMapping(path = "back")
    public List<ErrorLog> GetBackendErrorsByInterval(@RequestParam Date from,
                                                     @RequestParam Date to) {
        return errorLogService.GetBackendErrorsByInterval(from, to);
    }

    @PostMapping
    public Boolean SetFrontendError(@RequestBody ErrorLog errorLog) {
        return errorLogService.SetFrontendError(errorLog);
    }
}

package nicoAntonelli.managefy.api;

import jakarta.servlet.http.HttpServletRequest;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller used to handle non-existent URIs cases only (404)
@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api")
public class PathController {
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public PathController(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    @GetMapping(path = "**")
    public Result<Object> NotFound(HttpServletRequest req) {
        String url = req.getRequestURL().toString();
        Exceptions.NotFoundException ex = new Exceptions.NotFoundException("Route not found: " + url);

        errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
        return new Result<>(null, 404, ex.getMessage());
    }
}

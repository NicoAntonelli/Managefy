package nicoAntonelli.managefy.api;

import jakarta.servlet.http.HttpServletRequest;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Controller used to handle non-existent URIs cases (404)
@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api")
public class PathController {
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public PathController(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    @RequestMapping(path = {"", "/"})
    public Result<Object> API() {
        return new Result<>("Welcome to Managefy API!");
    }

    @RequestMapping(path = "**",
                    method = {RequestMethod.GET, RequestMethod.POST,
                              RequestMethod.PUT, RequestMethod.DELETE,
                              RequestMethod.HEAD, RequestMethod.OPTIONS,
                              RequestMethod.PATCH, RequestMethod.TRACE})
    public Result<Object> NotFound(HttpServletRequest req) {
        String url = req.getRequestURL().toString();
        Exceptions.NotFoundException ex = new Exceptions.NotFoundException("Route not found: " + url);

        errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
        return new Result<>(null, 404, ex.getMessage());
    }
}

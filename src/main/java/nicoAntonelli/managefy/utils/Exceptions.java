package nicoAntonelli.managefy.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class Exceptions {
    @Getter @NoArgsConstructor
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static public final class BadRequestException extends RuntimeException {
        public String message;
        public final String status = "400 (Bad Request)";
        public Exception InnerException;

        public BadRequestException(String message) {
            this.message = message;
        }

        public BadRequestException(String message, Exception ex) {
            this.message = message;
            this.InnerException = ex;
        }
    }

    @Getter @NoArgsConstructor
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static public final class UnauthorizedException extends RuntimeException {
        public String message;
        public final String status = "401 (Unauthorized)";
        public Exception InnerException;

        public UnauthorizedException(String message) {
            this.message = message;
        }

        public UnauthorizedException(String message, Exception ex) {
            this.message = message;
            this.InnerException = ex;
        }
    }

    @Getter @NoArgsConstructor
    @ResponseStatus(HttpStatus.NOT_FOUND)
    static public final class NotFoundException extends RuntimeException {
        public String message;
        public final String status = "404 (Not Found)";
        public Exception InnerException;

        public NotFoundException(String message) {
            this.message = message;
        }

        public NotFoundException(String message, Exception ex) {
            this.message = message;
            this.InnerException = ex;
        }
    }

    @Getter @NoArgsConstructor
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    static public final class InternalServerErrorException extends RuntimeException {
        public String message;
        public final String status = "500 (Internal Server Error)";
        public Exception InnerException;

        public InternalServerErrorException(String message) {
            this.message = message;
        }

        public InternalServerErrorException(String message, Exception ex) {
            this.message = message;
            this.InnerException = ex;
        }
    }
}

package nicoAntonelli.managefy.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class Exceptions {
    @Getter @NoArgsConstructor
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static public final class BadRequestException extends RuntimeException {
        static public final String status = "400 (Bad Request)";
        public String message;
        public Exception innerException;

        public BadRequestException(String message) {
            this.message = message;
        }

        public BadRequestException(String message, Exception ex) {
            this.message = message;
            this.innerException = ex;
        }

        public String getStatus() {
            return status;
        }
    }

    @Getter @NoArgsConstructor
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static public final class UnauthorizedException extends RuntimeException {
        static public final String status = "401 (Unauthorized)";
        public String message;
        public Exception innerException;

        public UnauthorizedException(String message) {
            this.message = message;
        }

        public UnauthorizedException(String message, Exception ex) {
            this.message = message;
            this.innerException = ex;
        }

        public String getStatus() {
            return status;
        }
    }

    @Getter @NoArgsConstructor
    @ResponseStatus(HttpStatus.NOT_FOUND)
    static public final class NotFoundException extends RuntimeException {
        static public final String status = "404 (Not Found)";
        public String message;
        public Exception innerException;

        public NotFoundException(String message) {
            this.message = message;
        }

        public NotFoundException(String message, Exception ex) {
            this.message = message;
            this.innerException = ex;
        }

        public String getStatus() {
            return status;
        }
    }

    @Getter @NoArgsConstructor
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    static public final class InternalServerErrorException extends RuntimeException {
        static public final String status = "500 (Internal Server Error)";
        public String message;
        public Exception innerException;

        public InternalServerErrorException(String message) {
            this.message = message;
        }

        public InternalServerErrorException(String message, Exception ex) {
            this.message = message;
            this.innerException = ex;
        }

        public String getStatus() {
            return status;
        }
    }
}

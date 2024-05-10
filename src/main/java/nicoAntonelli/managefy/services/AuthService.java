package nicoAntonelli.managefy.services;

import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.JWTHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    // Fails with null
    public User validateToken(String token) {
        if (token == null) return null;

        // Remove bearer
        String filter = "Bearer ";
        if (token.contains(filter)) {
            token = token.substring(token.indexOf(filter) + filter.length());
        }

        if (!JWTHelper.validateToken(token)) return null;

        String userStringify = JWTHelper.getSubjectFromToken(token);
        return User.UserFromJWT(userStringify);
    }

    // Fails with exceptions
    public User validateTokenFromHeaders(HttpHeaders headers, String methodName) {
        if (headers == null) {
            throw new Exceptions.UnauthorizedException(methodName + " - Invalid access token");
        }

        String token = headers.getFirst("Authorization");
        User user = validateToken(token);
        if (user == null) {
            throw new Exceptions.UnauthorizedException(methodName + " - Invalid access token");
        }

        return user;
    }
}

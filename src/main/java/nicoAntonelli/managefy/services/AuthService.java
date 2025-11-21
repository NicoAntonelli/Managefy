package nicoAntonelli.managefy.services;

import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.JWTHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

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

        // Remove authorization
        filter = "Authorization=";
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

        // Check if the token it's contained directly in headers
        String token = headers.getFirst("Authorization");

        // Check if the token it's inside an http-only cookie
        if (token == null) token = headers.getFirst("Cookie");

        User user = validateToken(token);
        if (user == null) {
            throw new Exceptions.UnauthorizedException(methodName + " - Invalid access token");
        }

        // User needs to have mail verification except for a few methods
        List<String> whiteList = List.of("GenerateUserValidation", "ValidateUser", "DeleteUser");
        if (!user.getValidated() && !whiteList.contains(methodName)) {
            throw new Exceptions.UnauthorizedException(methodName + " - The user is not correctly validated yet!");
        }

        return user;
    }
}

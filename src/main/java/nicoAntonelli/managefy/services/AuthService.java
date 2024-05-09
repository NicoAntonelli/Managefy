package nicoAntonelli.managefy.services;

import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.utils.JWTHelper;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public User validateToken(String token) {
        if (token == null) return null;
        if (!JWTHelper.validateToken(token)) return null;

        String userStringify = JWTHelper.getSubjectFromToken(token);
        return User.UserFromJWT(userStringify);
    }
}

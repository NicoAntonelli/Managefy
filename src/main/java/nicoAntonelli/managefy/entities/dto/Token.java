package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Token {
    private String accessToken;
    private String type;
    private Integer expirationInSeconds;
    private Long userID;

    public Token(String AccessToken, Long userID) {
        this.accessToken = AccessToken;
        this.type = "Bearer";
        this.expirationInSeconds = 86400;
        this.userID = userID;
    }
}

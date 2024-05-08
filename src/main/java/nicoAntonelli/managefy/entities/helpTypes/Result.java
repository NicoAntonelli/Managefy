package nicoAntonelli.managefy.entities.helpTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Result<T> {
    private Boolean ok;
    private Integer statusCode;
    private String status;
    private T body;

    public Result(T body, Integer statusCode) {
        this.ok = statusCode == 200;
        this.statusCode = statusCode;
        this.status = setStatusByCode(statusCode);
        this.body = body;
    }

    public Result(T body) {
        this.ok = true;
        this.statusCode = 200;
        this.status = "200 (OK)";
        this.body = body;
    }

    private static String setStatusByCode(Integer status) {
        return switch (status) {
            case 200 -> "200 (OK)";
            case 400 -> "400 (Bad Request)";
            case 401 -> "401 (Unauthorized)";
            case 404 -> "404 (Not Found)";
            case 500 -> "500 (Internal Server Error)";
            default -> status.toString();
        };
    }
}

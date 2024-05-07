package nicoAntonelli.managefy.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class SaleLineKey {
    private Long sale;
    private Integer position;
}

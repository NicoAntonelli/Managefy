package nicoAntonelli.managefy.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data @NoArgsConstructor @AllArgsConstructor
public class SaleLineKey implements Serializable {
    private Long saleID;
    private Integer position;
}

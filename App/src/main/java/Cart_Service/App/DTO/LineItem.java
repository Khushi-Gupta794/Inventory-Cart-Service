package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItem {
    private String id;          // this is your lineItemId
    private String productId;
    private int quantity;
    private TotalPrice totalPrice;
}

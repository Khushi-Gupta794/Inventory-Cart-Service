package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartResponse {
    private String type;
    private String id;
    private int version;          //version updates after every CT call
    private String cartState;
    private String taxMode;
    private String customerId;
    private List<LineItem> lineItems;   // typed now — need lineItemId from here
    private TotalPrice totalPrice;
    private String shippingMode;
    private String inventoryMode;
    private String origin;
}

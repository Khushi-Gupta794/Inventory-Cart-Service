package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {
    private String type;
    private String id;
    private int version;
    private String orderState;      // "Open"
    private String taxMode;
    private String origin;
    private String shippingMode;

    private TotalPrice totalPrice;
    private TaxedPrice taxedPrice;
    private TaxedPrice taxedShippingPrice;

    private ShippingInfo shippingInfo;
    private ShippingAddress shippingAddress;

    private List<LineItem> lineItems;

    private CartReference cart;
}

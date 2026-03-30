package Cart_Service.App.DTO;

import lombok.Data;

@Data
public class ShippingMethodReference {
    private String id;
    private String typeId = "shipping-method";  // always this value
}
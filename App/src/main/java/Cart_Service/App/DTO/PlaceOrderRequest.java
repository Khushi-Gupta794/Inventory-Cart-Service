package Cart_Service.App.DTO;

import lombok.Data;

@Data
public class PlaceOrderRequest {
    private String cartId;//still sned cartId from Postman
    private int version;
}
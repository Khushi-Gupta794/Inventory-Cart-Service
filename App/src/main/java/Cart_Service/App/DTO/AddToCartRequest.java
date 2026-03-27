package Cart_Service.App.DTO;

import lombok.Data;

@Data
public class AddToCartRequest {
    private String cartId;
    private String productId;
    private int quantity;
}

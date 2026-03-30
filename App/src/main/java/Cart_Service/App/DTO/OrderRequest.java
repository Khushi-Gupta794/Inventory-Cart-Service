package Cart_Service.App.DTO;

import lombok.Data;

@Data
public class OrderRequest {
//private String cartId;
private CartReference cart;

    private int version;

}

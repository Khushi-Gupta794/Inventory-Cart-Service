package Cart_Service.App.DTO;

import lombok.Data;

import java.util.List;

@Data
public class CartUpdateRequest {
    private int version;
    private List<CartAction> actions;
}

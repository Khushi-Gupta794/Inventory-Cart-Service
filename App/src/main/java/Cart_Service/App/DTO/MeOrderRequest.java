package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeOrderRequest {
    private String id;       // cartId directly
    private int version;
}

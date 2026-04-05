package Cart_Service.App.DTO;

import com.commercetools.api.models.common.Address;
import lombok.Data;

@Data
public class ShippingRequestNewDTO {
    private String cartId;
    private long version;
    private String shippingMethodId;
    private Address address;
}

package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShippingInfo {
    private String shippingMethodName;
    private TotalPrice price;
    private String shippingMethodState;
    private TaxedPrice taxedPrice;
}

package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartAction {
    private String action;  // "addLineItem" or "setLineItemTaxAmount"

    // For addLineItem
    private String productId;
    private Integer variantId;
    private Integer quantity;

    // For setLineItemTaxAmount
    private String lineItemId;
    private ExternalTaxAmount externalTaxAmount;

    //update changes added for the shipping part as now all will have the same url
    // setShippingAddress field
    private ShippingAddress address;

    // setShippingMethod field
    private ShippingMethodReference shippingMethod;

    // setCartTotalTax field
    private Money externalTotalGross;
}

package Cart_Service.App.DTO;

import lombok.Data;

@Data
public class ShippingRequest {
    private String cartId;
    private int version;

    // Address fields
    private String firstName;
    private String lastName;
    private String streetNumber;
    private String additionalStreetInfo;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Shipping method
    private String shippingMethodId;

    // Tax amounts
    private long shippingTaxCentAmount;    // 658
    //private long cartTotalCentAmount;  keeping now automatically calculation no hardcoding
}

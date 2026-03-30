package Cart_Service.App.DTO;

import lombok.Data;

@Data
public class ShippingAddress {
    private String key;
    private String firstName;
    private String lastName;
    private String streetNumber;
    private String additionalStreetInfo;
    private String postalCode;
    private String city;
    private String state;
    private String country;
}

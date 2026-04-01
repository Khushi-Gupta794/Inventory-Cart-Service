package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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

package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerResponse {

    private String id;
    private int version;
    private Instant versionModifiedAt;
    private int lastMessageSequenceNumber;
    private Instant createdAt;
    private Instant lastModifiedAt;

    private ClientInfoDTO lastModifiedBy;
    private ClientInfoDTO createdBy;

    private String email;
    private String firstName;
    private String lastName;
    private String password;

    private List<Object> addresses;           // Can be typed if you have address structure
    private List<String> shippingAddressIds;
    private List<String> billingAddressIds;

    private boolean isEmailVerified;

    private List<Object> customerGroupAssignments;
    private List<Object> stores;

    private String authenticationMode;
}
package Cart_Service.App.DTO;

import lombok.Data;

@Data
public class ClientInfoDTO {
    private String clientId;
    private boolean isPlatformClient;
    private String anonymousId;
}

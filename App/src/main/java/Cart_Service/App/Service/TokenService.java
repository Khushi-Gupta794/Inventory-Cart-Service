package Cart_Service.App.Service;

import Cart_Service.App.DTO.LoginResponse;
import Cart_Service.App.FeignClient.CTAuthClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class TokenService {

    @Value("${ct.client.id}")
    private String clientId;

    @Value("${ct.client.secret}")
    private String clientSecret;

    @Value("${ct.project.key}")
    private String projectKey;

    private final CTAuthClient ctAuthClient;

    public TokenService(CTAuthClient ctAuthClient) {
        this.ctAuthClient = ctAuthClient;
    }

    public String getAccessToken() {
        String credentials = clientId + ":" + clientSecret;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
       //reverse communication from client to service
        LoginResponse response = ctAuthClient.login(
                basicAuth,
                "client_credentials",
                "manage_project:" + projectKey
        );

        return response.getAccess_token();
    }
}

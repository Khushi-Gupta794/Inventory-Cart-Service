package Cart_Service.App.FeignClient;

import Cart_Service.App.DTO.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name =  "ct-auth", url = "https://auth.us-east-2.aws.commercetools.com")
public interface CTAuthClient {
    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  //  LoginResponse login(@RequestBody LoginRequest request); //as login takes param not the JSON body
    LoginResponse  login(@RequestHeader("Authorization") String basicAuth,  // "Basic base64(clientId:secret)"
                          @RequestParam("grant_type") String grantType,
                          @RequestParam("scope") String scope
    );
}

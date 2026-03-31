package Cart_Service.App.FeignClient;

import Cart_Service.App.DTO.LoginResponse;
import Cart_Service.App.DTO.MeCustomerTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name =  "ct-auth", url = "https://auth.us-east-2.aws.commercetools.com")
public interface CTAuthClient {

    //for the admin part
    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  //  LoginResponse login(@RequestBody LoginRequest request); //as login takes param not the JSON body
    LoginResponse  login(@RequestHeader("Authorization") String basicAuth,  // "Basic base64(clientId:secret)"
                          @RequestParam("grant_type") String grantType,
                          @RequestParam("scope") String scope
    );


    //for the individual customer part--me part
    @PostMapping(value = "/oauth/ecom-app-123456/customers/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    MeCustomerTokenResponse customerLogin(@RequestHeader("Authorization") String basicAuth,
                                          @RequestParam("grant_type") String grantType,
                                          @RequestParam("username") String email,
                                          @RequestParam("password") String password,
                                          @RequestParam("scope") String scope
    );
}

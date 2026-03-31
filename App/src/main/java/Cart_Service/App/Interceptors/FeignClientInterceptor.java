package Cart_Service.App.Interceptors;

import Cart_Service.App.Service.TokenHolder;
import Cart_Service.App.Service.TokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;



public class FeignClientInterceptor implements RequestInterceptor {
    private TokenService tokenService;

    public FeignClientInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void apply(RequestTemplate template) {
        // Skip auth endpoint itself to avoid circular call
       // if (template.url().contains("/oauth/token")) return;

 //imp-----in both the cases for admin and customer no storage is done
        //to skip if customer already exist
        //if (template.headers().containsKey("Authorization")) return;
//        if(TokenHolder.hasToken()){
//            template.header("Authorization", "Bearer " + TokenHolder.getToken());
//                    return;
//        }
       //for customer part as it gets the token pass in every header manually
       // if (template.headers().containsKey("Authorization")) return;


        //for admin part, call fresh token on every request
        String token = tokenService.getAccessToken();
        template.header("Authorization", "Bearer " + token);
    }
}

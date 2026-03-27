package Cart_Service.App.Interceptors;

import Cart_Service.App.Service.TokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    private TokenService tokenService;

    public FeignClientInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void apply(RequestTemplate template) {
        // Skip auth endpoint itself to avoid circular call
        if (template.url().contains("/oauth/token")) return;

        String token = tokenService.getAccessToken();
        template.header("Authorization", "Bearer " + token);
    }
}

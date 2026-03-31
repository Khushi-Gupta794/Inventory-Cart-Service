package Cart_Service.App.Interceptors;

import Cart_Service.App.Service.TokenService;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class AdminFeignConfig {
    @Bean
    public RequestInterceptor adminTokenInterceptor(TokenService tokenService) {
        return new FeignClientInterceptor(tokenService);
    }
}

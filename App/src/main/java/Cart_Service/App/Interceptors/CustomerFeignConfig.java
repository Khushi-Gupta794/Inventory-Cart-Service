package Cart_Service.App.Interceptors;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;

public class CustomerFeignConfig {
    @Bean
    public RequestInterceptor customerTokenInterceptor() {
        return new CustomerInterceptor();
    }
}

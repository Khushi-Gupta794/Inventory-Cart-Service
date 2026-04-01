package Cart_Service.App.Interceptors;
import Cart_Service.App.Service.TokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class CustomerInterceptor implements RequestInterceptor {

  //  private final TokenService tokenService;
  //  private final CustomerCredentialsContext customerCredentialsContext;
  //private final HttpServletRequest httpServletRequest;


    @Override
    public void apply(RequestTemplate template) {
        //calls my endpoint login get token pass to every me endpoint
        // Forward the Authorization header from the incoming HTTP request
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                template.header("Authorization", authHeader);
            }
        }
    }

}
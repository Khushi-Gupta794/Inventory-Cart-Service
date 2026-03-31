package Cart_Service.App.Interceptors;
import Cart_Service.App.Service.TokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


public class CustomerInterceptor implements RequestInterceptor {

  //  private final TokenService tokenService;
  //  private final CustomerCredentialsContext customerCredentialsContext;
  private final HttpServletRequest httpServletRequest;

    public CustomerInterceptor(
                               HttpServletRequest  httpServletRequest ) {
      //  this.tokenService = tokenService;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public void apply(RequestTemplate template) {
        //calls my endpoint login get token pass to every me endpoint
        // Forward the Authorization header from the incoming HTTP request
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            template.header("Authorization", authHeader);
        } else {
            throw new IllegalStateException("No Authorization token found in request");
        }
    }

}
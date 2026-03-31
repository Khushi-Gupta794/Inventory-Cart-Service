package Cart_Service.App.Service;


import org.springframework.stereotype.Component;

@Component
public class TokenHolder {

    // stores token per thread — automatically cleared after request
    private static final ThreadLocal<String> customerToken = new ThreadLocal<>();

    public static void setToken(String token) {
        customerToken.set(token);
    }

    public static String getToken() {
        return customerToken.get();
    }

    public static void clear() {
        customerToken.remove();  // important — prevent memory leak
    }

    public static boolean hasToken() {
        return customerToken.get() != null;
    }
}

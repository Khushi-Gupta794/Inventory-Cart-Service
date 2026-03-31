package Cart_Service.App.MyController;

import Cart_Service.App.DTO.MeCustomerLoginRequest;
import Cart_Service.App.Service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody MeCustomerLoginRequest request) {
    String token = tokenService.getCustomerToken(request.getEmail(),
                request.getPassword());
    Map<String, String> response = new HashMap<>();
        response.put("access_token", token);
        response.put("token_type", "Bearer");
        response.put("message", "Login successful");

        return ResponseEntity.ok(response);
    }
}
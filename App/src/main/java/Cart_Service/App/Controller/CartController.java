package Cart_Service.App.Controller;

import Cart_Service.App.DTO.AddtoCartRequest;
import Cart_Service.App.DTO.CartResponse;
import Cart_Service.App.Service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddtoCartRequest request) {

        String result = service.addToCart(request);

        return ResponseEntity.ok(
                new CartResponse("Item added successfully", result)
        );
    }
}

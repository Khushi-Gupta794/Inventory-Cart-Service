package Cart_Service.App.MyController;

import Cart_Service.App.DTO.CartResponse;
import Cart_Service.App.DTO.ShippingRequest;
import Cart_Service.App.Service.CartServiceNew;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShippingController {
    private final CartServiceNew cartServiceNew;

    public ShippingController(CartServiceNew cartServiceNew){
        this.cartServiceNew= cartServiceNew;
    }
    @PostMapping("/shipping")
    public ResponseEntity<CartResponse> addShipping(@RequestBody ShippingRequest request) {
        CartResponse response = cartServiceNew.addShipping(request);
        return ResponseEntity.ok(response);
    }
}

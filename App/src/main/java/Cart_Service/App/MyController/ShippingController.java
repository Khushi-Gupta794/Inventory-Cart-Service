package Cart_Service.App.MyController;

import Cart_Service.App.DTO.CartResponse;
import Cart_Service.App.DTO.ShippingRequest;
import Cart_Service.App.DTO.ShippingRequestNewDTO;
import Cart_Service.App.SDK.CTSDKService;
import Cart_Service.App.Service.CartServiceNew;
import com.commercetools.api.models.cart.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShippingController {
    private final CartServiceNew cartServiceNew;
    private final CTSDKService ctsdkService;

    public ShippingController(CartServiceNew cartServiceNew, CTSDKService ctsdkService){
        this.cartServiceNew= cartServiceNew;
        this.ctsdkService = ctsdkService;
    }
//    @PostMapping("/shipping")
//    public ResponseEntity<CartResponse> addShipping(@RequestBody ShippingRequest request) {
//        CartResponse response = cartServiceNew.addShipping(request);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/shipping")
    public ResponseEntity<Cart> addShipping(@RequestBody ShippingRequestNewDTO request) {
        Cart response = ctsdkService.addShipping(request.getCartId(), request.getVersion(), request.getShippingMethodId(),request.getAddress());
        return ResponseEntity.ok(response);
    }
}

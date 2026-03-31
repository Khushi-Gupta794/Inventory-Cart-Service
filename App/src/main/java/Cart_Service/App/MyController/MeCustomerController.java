package Cart_Service.App.MyController;

import Cart_Service.App.DTO.*;
import Cart_Service.App.FeignClient.MeCustomerClient;
import Cart_Service.App.Service.MeCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class MeCustomerController {
    private final MeCartService meCartService;
    private final MeCustomerClient meCustomerClient;

    public MeCustomerController(MeCartService meCartService,MeCustomerClient meCustomerClient){
        this.meCartService = meCartService;
        this.meCustomerClient = meCustomerClient;
    }

    @PostMapping("/create")
    public ResponseEntity<CartResponse> createCart(@RequestBody CartRequest cartRequest){
        return meCustomerClient.createMyCart(cartRequest);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(meCartService.addToMyCart(
                request.getCartId(),
                request.getProductId(),
                request.getQuantity()
        ));
    }

    @PostMapping("/cart/shipping")
    public ResponseEntity<CartResponse> addShipping(@RequestBody ShippingRequest request) {
        return ResponseEntity.ok(meCartService.addMyShipping(request));
    }

    @PostMapping("/orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        return ResponseEntity.ok(meCartService.placeMyOrder(
                request.getCartId(),
                request.getVersion()
        ));
    }


}

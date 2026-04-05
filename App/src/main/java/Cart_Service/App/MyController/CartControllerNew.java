package Cart_Service.App.MyController;

import Cart_Service.App.DTO.AddToCartRequest;
import Cart_Service.App.DTO.CartPagedResponse;
import Cart_Service.App.DTO.CartRequest;
import Cart_Service.App.DTO.CartResponse;
import Cart_Service.App.FeignClient.CTCartClient;
import Cart_Service.App.SDK.CTSDKService;
import Cart_Service.App.Service.CartServiceNew;
import com.commercetools.api.models.cart.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartControllerNew {
    private CartServiceNew cartServiceNew;
    private CTCartClient ctCartClient;

    private final CTSDKService ctsdkService;

    public CartControllerNew(CartServiceNew cartServiceNew, CTCartClient ctCartClient, CTSDKService ctsdkService){
        this.cartServiceNew= cartServiceNew;
        this.ctCartClient = ctCartClient;
        this.ctsdkService= ctsdkService;
    }

    @PostMapping("/create")
    public ResponseEntity<CartResponse> createCart(@RequestBody CartRequest cartRequest){
        return ctCartClient.createCart(cartRequest);
    }

    @GetMapping("/getAll")
    public ResponseEntity<CartPagedResponse> getAllCarts(){
        return ctCartClient.getAllCarts();
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCartById(@PathVariable String cartId){
        return ctCartClient.getCart(cartId);
    }

//    @PostMapping("/add")
//    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request) {
//        CartResponse response = cartServiceNew.addToCart(
//                request.getCartId(),
//                request.getProductId(),
//                request.getQuantity()
//        );
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody AddToCartRequest addToCartRequest){
        Cart response=  ctsdkService.addToCart(addToCartRequest.getCartId(), addToCartRequest.getProductId(), addToCartRequest.getQuantity());
        return ResponseEntity.ok(response);
    }




}

package Cart_Service.App.FeignClient;

import Cart_Service.App.DTO.CartPagedResponse;
import Cart_Service.App.DTO.CartRequest;
import Cart_Service.App.DTO.CartResponse;
import Cart_Service.App.DTO.CartUpdateRequest;
import Cart_Service.App.Interceptors.AdminFeignConfig;
import jakarta.persistence.PostRemove;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ct-carts", url = "https://api.us-east-2.aws.commercetools.com",  configuration = AdminFeignConfig.class )
public interface CTCartClient {

    @PostMapping("/ecom-app-123456/carts")
    ResponseEntity<CartResponse> createCart(@RequestBody CartRequest cartRequest);

//    @PostMapping("/ecom-app-123456/carts/{cartId}")
//    ResponseEntity<CartResponse> addToCart(@RequestHeader("Authorization") String token, @RequestBody CartRequest cartRequest, @PathVariable String cartId);

    //version + action body
    @PostMapping("/ecom-app-123456/carts/{cartId}")
    ResponseEntity<CartResponse> updateCart(
            @PathVariable String cartId,
            @RequestBody CartUpdateRequest updateRequest  // different DTO than CartRequest
    );

    @GetMapping("/ecom-app-123456/carts")
    ResponseEntity<CartPagedResponse> getAllCarts();

    @GetMapping("/ecom-app-123456/carts/{cartId}")
    ResponseEntity<CartResponse> getCart(@PathVariable String cartId);

    @GetMapping("/ecom-app-123456/carts/customer-id={customerId}")
    ResponseEntity<CartResponse> getCartByCustomerId(@PathVariable String customerId);


}

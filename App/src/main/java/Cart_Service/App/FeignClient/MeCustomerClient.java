package Cart_Service.App.FeignClient;

import Cart_Service.App.DTO.*;
import Cart_Service.App.Interceptors.CustomerFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ct-me-carts", url = "https://api.us-east-2.aws.commercetools.com", configuration = CustomerFeignConfig.class)
public interface MeCustomerClient {
    @PostMapping("/ecom-app-123456/me/carts")
    ResponseEntity<CartResponse> createMyCart(
           // @RequestHeader("Authorization") String token,
            @RequestBody CartRequest cartRequest);

    @GetMapping("/ecom-app-123456/me/active-cart")
    ResponseEntity<CartResponse> getMyActiveCart(
           // @RequestHeader("Authorization") String token
    );


    @GetMapping("/ecom-app-123456/me/carts/{cartId}")
    ResponseEntity<CartResponse> getCart(
         //   @RequestHeader("Authorization") String token,
            @PathVariable String cartId);

    @PostMapping("/ecom-app-123456/me/carts/{cartId}")
    ResponseEntity<CartResponse> updateMyCart(
         //   @RequestHeader("Authorization") String token,
            @PathVariable String cartId,
            @RequestBody CartUpdateRequest updateRequest
    );

    @PostMapping("/ecom-app-123456/me/orders")
    ResponseEntity<OrderResponse> placeMyOrder(
         //   @RequestHeader("Authorization") String token,
            @RequestBody MeOrderRequest meOrderRequest);
}

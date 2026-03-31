package Cart_Service.App.FeignClient;

import Cart_Service.App.DTO.OrderPagedResponse;
import Cart_Service.App.DTO.OrderRequest;
import Cart_Service.App.DTO.OrderResponse;
import Cart_Service.App.Interceptors.AdminFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ct-orders", url = "https://api.us-east-2.aws.commercetools.com",  configuration = AdminFeignConfig.class )
public interface CTOrderClient {

    @PostMapping("/ecom-app-123456/orders")
    ResponseEntity<OrderResponse> createOrder( @RequestBody OrderRequest orderRequest);

    @GetMapping("/ecom-app-123456/orders/{orderId}")
    ResponseEntity<OrderResponse> gerOrderById(@PathVariable String orderId);


    @GetMapping("/ecom-app-123456/orders")
    ResponseEntity<OrderPagedResponse> getAllOrders();

}

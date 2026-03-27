//package Cart_Service.App.FeignClient;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//@FeignClient(name = "ct-orders", url = "https://api.us-east-2.aws.commercetools.com")
//public interface CTOrderClient {
//
//    @PostMapping("/ecom-app-123456/orders")
//    ResponseEntity<OrderResponse> createOrder(@RequestHeader("Authorization") String token, @RequestBody OrderRequest orderRequest);
//}

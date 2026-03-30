package Cart_Service.App.MyController;

import Cart_Service.App.DTO.OrderRequest;
import Cart_Service.App.DTO.OrderResponse;
import Cart_Service.App.DTO.PlaceOrderRequest;
import Cart_Service.App.FeignClient.CTOrderClient;
import Cart_Service.App.Service.OrderService;
import com.fasterxml.jackson.databind.util.ClassUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    private final CTOrderClient ctOrderClient;

    public OrderController(OrderService orderService,CTOrderClient ctOrderClient) {
        this.orderService = orderService;
        this.ctOrderClient=ctOrderClient;
    }

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderService.placeOrder(
                request.getCartId(),
                request.getVersion()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        return ctOrderClient.gerOrderById(orderId);
    }
}

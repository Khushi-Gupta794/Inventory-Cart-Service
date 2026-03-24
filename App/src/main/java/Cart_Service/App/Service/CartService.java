package Cart_Service.App.Service;

import Cart_Service.App.DTO.AddtoCartRequest;
import Cart_Service.App.DTO.InventoryCheckRequest;
import Cart_Service.App.DTO.InventoryResponse;
import Cart_Service.App.FeignClient.InventoryClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final InventoryClient inventoryClient;
    private final CTService ctService;

    public CartService(InventoryClient inventoryClient,
                       CTService ctService) {
        this.inventoryClient = inventoryClient;
        this.ctService = ctService;
    }

    @CircuitBreaker(name="App2", fallbackMethod = "fallbackInventory")
    public String addToCart(AddtoCartRequest addtoCartRequest){
        InventoryCheckRequest invReq = new InventoryCheckRequest();
        invReq.setProductId(addtoCartRequest.getProductId());
        invReq.setQuantity(addtoCartRequest.getQuantity());

        InventoryResponse response = inventoryClient.checkStock(invReq);

        // 2. Validate stock
        if (!response.isAvailable()) {
            throw new RuntimeException("Product out of stock");
        }

        // 3. Call CommerceTools
        return ctService.createCartAndAddItem(
              //  addtoCartRequest.getCartId(),
//              addtoCartRequest.getVersion(),
                addtoCartRequest.getProductId(),
                addtoCartRequest.getQuantity()
        );
    }

    public String fallbackInventory(AddtoCartRequest addtoCartRequest, Exception e){
    System.out.println("Fallback triggered due to: " + e.getMessage());
    return "inventory service is down";
//        System.out.println("message: "get);
      //  throw new RuntimeException("Inventory service down. Try later.");
    }
}

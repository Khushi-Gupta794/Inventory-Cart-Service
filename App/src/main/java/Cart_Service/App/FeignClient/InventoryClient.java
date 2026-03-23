package Cart_Service.App.FeignClient;

import Cart_Service.App.DTO.InventoryCheckRequest;
import Cart_Service.App.DTO.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "App2", url = "http://localhost:8081")
public interface InventoryClient {

//    @PostMapping("/api/inventory")
//    ResponseEntity saveInventory(@RequestBody InventoryRequest inventoryRequest);

    @PostMapping("api/inventory/check")
    InventoryResponse checkStock(InventoryCheckRequest request);
}

package Inventory_Service.App2.Controller;

import Inventory_Service.App2.DTO.InventoryRequest;
import Inventory_Service.App2.DTO.InventoryResponse;
import Inventory_Service.App2.Entity.InventoryEntity;
import Inventory_Service.App2.Repo.InventoryRepo;
import Inventory_Service.App2.Service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service){
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<String> saveInventory(@RequestBody InventoryRequest inventoryRequest){
        service.create(inventoryRequest);
        return ResponseEntity.ok("Stocks added");
    }

    @PostMapping("/reduce")
    public ResponseEntity<String> reduce(@RequestBody InventoryRequest inventoryRequest){
        service.reduceStock(inventoryRequest.getProductId(), inventoryRequest.getQuantity());
        return ResponseEntity.ok("Stock reduced");
    }

    @PostMapping("/check")
    public ResponseEntity<InventoryResponse> checkStock(@RequestBody InventoryRequest request){
        InventoryResponse response = service.checkStock(request);
        return ResponseEntity.ok(response);
    }



}

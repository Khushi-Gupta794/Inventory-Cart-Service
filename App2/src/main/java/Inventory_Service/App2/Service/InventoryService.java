package Inventory_Service.App2.Service;

import Inventory_Service.App2.DTO.InventoryRequest;
import Inventory_Service.App2.DTO.InventoryResponse;
import Inventory_Service.App2.Entity.InventoryEntity;
import Inventory_Service.App2.Repo.InventoryRepo;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final InventoryRepo repo;

    public InventoryService(InventoryRepo repo){
        this.repo = repo;
    }

   public void create(InventoryRequest inventoryRequest){
        InventoryEntity inventory= new InventoryEntity(
       inventoryRequest.getProductId(),
               inventoryRequest.getQuantity()
//                inventory.setProductId(inventoryRequest.getProductId()),
//                inventory.setQuantity(inventoryRequest.getQuantity())
        );
        repo.save(inventory);
   }

    public InventoryResponse checkStock(InventoryRequest request) {
    InventoryEntity inventory = repo.findById(request.getProductId()).orElse(null);

        if (inventory == null) {
            return new InventoryResponse(
                    request.getProductId(),
                    false,
                    0
            );
        }

        boolean available = inventory.getQuantity() >= request.getQuantity();

        return new InventoryResponse(
                request.getProductId(),
                available,
                inventory.getQuantity()
        );
    }

    //for reduce stock part
    public void reduceStock(String productId, int quantity) {
        InventoryEntity inventory = repo
                .findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        repo.save(inventory);
    }

}

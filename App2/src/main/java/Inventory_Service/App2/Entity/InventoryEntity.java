package Inventory_Service.App2.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InventoryEntity {

    @Id
   private String productId;
    private int quantity;

    public InventoryEntity(String productId, int quantity) {
    }
}

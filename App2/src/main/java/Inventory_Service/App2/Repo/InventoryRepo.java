package Inventory_Service.App2.Repo;

import Inventory_Service.App2.Entity.InventoryEntity;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepo extends JpaRepository<InventoryEntity, String> {
}

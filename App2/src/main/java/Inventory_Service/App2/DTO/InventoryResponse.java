package Inventory_Service.App2.DTO;

public class InventoryResponse {
    private String productId;
    private boolean available;
    private int availableQuantity;

    public InventoryResponse() {}

    public InventoryResponse(String productId, boolean available, int availableQuantity) {
        this.productId = productId;
        this.available = available;
        this.availableQuantity = availableQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}

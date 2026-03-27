package Cart_Service.App.Service;

import Cart_Service.App.DTO.*;
import Cart_Service.App.FeignClient.CTCartClient;
import Cart_Service.App.FeignClient.InventoryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceNew {
    private InventoryClient inventoryClient;
    private CTCartClient ctCartClient;

    public CartServiceNew(InventoryClient inventoryClient,CTCartClient ctCartClient){
        this.inventoryClient=inventoryClient;
        this.ctCartClient=ctCartClient;
    }

    public CartResponse addToCart(String cartId, String productId, int quantity) {

        // Inventory Check
        InventoryCheckRequest inventoryRequest = new InventoryCheckRequest();
        inventoryRequest.setProductId(productId);
        //inventoryRequest.setProductId(sku);  use this if it is custom id, not like ct uuid
        inventoryRequest.setQuantity(quantity);

        InventoryResponse inventoryResponse = inventoryClient.checkStock(inventoryRequest);

        if (!inventoryResponse.isAvailable()) {
            throw new RuntimeException("Product " + productId + " is out of stock");
        }
        if (inventoryResponse.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Only " + inventoryResponse.getAvailableQuantity() + " items available");
        }

        // Get current cart (need version)
        CartResponse currentCart = ctCartClient.getCart(cartId).getBody();

        // addLineItem
        CartAction addAction = new CartAction();
        addAction.setAction("addLineItem");
        addAction.setProductId(productId);
        addAction.setVariantId(1);
        addAction.setQuantity(quantity);

        CartUpdateRequest addRequest = new CartUpdateRequest();
        addRequest.setVersion(currentCart.getVersion());
        addRequest.setActions(List.of(addAction));

        CartResponse cartAfterAdd = ctCartClient.updateCart(cartId, addRequest).getBody();

        // Extract lineItemId from response
        String lineItemId = cartAfterAdd.getLineItems()
                .stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Line item not found after adding"))
                .getId();

        // setLineItemTaxAmount
        Money totalGross = new Money();
        totalGross.setCurrencyCode("USD");
        totalGross.setCentAmount(2622);   // you can make this dynamic later

        TaxRate taxRate = new TaxRate();
        taxRate.setName("myTaxRate");
        taxRate.setAmount(0.14);
        taxRate.setIncludedInPrice(false);
        taxRate.setCountry("US");

        ExternalTaxAmount externalTaxAmount = new ExternalTaxAmount();
        externalTaxAmount.setTotalGross(totalGross);
        externalTaxAmount.setTaxRate(taxRate);

        CartAction taxAction = new CartAction();
        taxAction.setAction("setLineItemTaxAmount");
        taxAction.setLineItemId(lineItemId);
        taxAction.setExternalTaxAmount(externalTaxAmount);

        CartUpdateRequest taxRequest = new CartUpdateRequest();
        taxRequest.setVersion(cartAfterAdd.getVersion());  //use updated version
        taxRequest.setActions(List.of(taxAction));

        CartResponse finalCart = ctCartClient.updateCart(cartId, taxRequest).getBody();

        //after adding item the quantiy should be reduced
        InventoryCheckRequest reduceRequest = new InventoryCheckRequest();
        reduceRequest.setProductId(productId);
        reduceRequest.setQuantity(quantity);

        inventoryClient.reduce(reduceRequest);

       return finalCart;
    }
}

package Cart_Service.App.Service;

import Cart_Service.App.DTO.*;
import Cart_Service.App.FeignClient.CTCartClient;
import Cart_Service.App.FeignClient.CTOrderClient;
import Cart_Service.App.FeignClient.InventoryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final CTOrderClient ctOrderClient;
    private final CTCartClient ctCartClient;
    private final InventoryClient inventoryClient;

    public OrderService(CTOrderClient ctOrderClient,CTCartClient ctCartClient, InventoryClient inventoryClient){
        this.ctCartClient=ctCartClient;
        this.ctOrderClient=ctOrderClient;
        this.inventoryClient = inventoryClient;
    }
    public OrderResponse placeOrder(String cartId, int version){
//        CartResponse cart = ctCartClient.getCart(cartId).getBody();  //first check line item
//
//        //place request
////        OrderRequest orderRequest = new OrderRequest();
////        orderRequest.setCartId(cartId);
////        orderRequest.setVersion(version);
//
//        CartReference cartRef = new CartReference();
//        cartRef.setId(cartId);
//        cartRef.setTypeId("cart");
//
//        OrderRequest orderRequest = new OrderRequest();
//        orderRequest.setCart(cartRef);
//        orderRequest.setVersion(version);
//
//        OrderResponse orderResponse = ctOrderClient.createOrder(orderRequest).getBody();
//
//        return orderResponse;
//

        //  Get cart
        CartResponse cart = ctCartClient.getCart(cartId).getBody();

        // ── Step 2: Inventory check ────────────────
        for (LineItem item : cart.getLineItems()) {
            InventoryCheckRequest inventoryRequest = new InventoryCheckRequest();
            inventoryRequest.setProductId(item.getProductId());
            inventoryRequest.setQuantity(item.getQuantity());

            InventoryResponse inventoryResponse =
                    inventoryClient.checkStock(inventoryRequest);

            if (!inventoryResponse.isAvailable()) {
                throw new RuntimeException(
                        "Product " + item.getProductId() + " is out of stock"
                );
            }
            if (inventoryResponse.getAvailableQuantity() < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product " + item.getProductId()
                );
            }
        }

        // ── Step 3: Fix tax on any line item missing it ──
        CartResponse currentCart = cart;

        for (LineItem item : currentCart.getLineItems()) {
            if (item.getTaxedPrice() == null) {

                System.out.println("Tax missing for lineItem: " + item.getId() + "fixing...");

                // Calculate gross dynamically
                long itemTotal = item.getTotalPrice().getCentAmount();
                double taxRate = 0.14;
                long grossAmount = Math.round(itemTotal * (1 + taxRate));

                Money totalGross = new Money();
                totalGross.setCurrencyCode("USD");
                totalGross.setCentAmount(grossAmount);

                TaxRate taxRateObj = new TaxRate();
                taxRateObj.setName("myTaxRate");
                taxRateObj.setAmount(taxRate);
                taxRateObj.setIncludedInPrice(false);
                taxRateObj.setCountry("US");

                ExternalTaxAmount externalTaxAmount = new ExternalTaxAmount();
                externalTaxAmount.setTotalGross(totalGross);
                externalTaxAmount.setTaxRate(taxRateObj);

                CartAction taxAction = new CartAction();
                taxAction.setAction("setLineItemTaxAmount");
                taxAction.setLineItemId(item.getId());
                taxAction.setExternalTaxAmount(externalTaxAmount);

                CartUpdateRequest taxRequest = new CartUpdateRequest();
                taxRequest.setVersion(currentCart.getVersion());
                taxRequest.setActions(List.of(taxAction));

                // Update cart and keep latest version
                currentCart = ctCartClient.updateCart(cartId, taxRequest).getBody();

                System.out.println("Tax fixed for lineItem: " + item.getId());
            }
        }

        // ── Step 4: Place order ────────────────────
        CartReference cartRef = new CartReference();
    cartRef.setId(cartId);
        cartRef.setTypeId("cart");

//        OrderRequest orderRequest = new OrderRequest();
//        orderRequest.setCart(cartRef);
//        orderRequest.setVersion(version);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCart(cartRef);
        orderRequest.setVersion(currentCart.getVersion());  //use latest version

        OrderResponse order = ctOrderClient.createOrder(orderRequest).getBody();

        // ── Step 5: Reduce stock ───────────────────
        for (LineItem item : cart.getLineItems()) {
            InventoryCheckRequest reduceRequest = new InventoryCheckRequest();
            reduceRequest.setProductId(item.getProductId());
            reduceRequest.setQuantity(item.getQuantity());
            inventoryClient.reduce(reduceRequest);
        }

        return order;
    }
}

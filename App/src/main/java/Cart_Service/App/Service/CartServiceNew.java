package Cart_Service.App.Service;

import Cart_Service.App.DTO.*;
import Cart_Service.App.FeignClient.CTCartClient;
import Cart_Service.App.FeignClient.InventoryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceNew {
    private final InventoryClient inventoryClient;
    private final CTCartClient ctCartClient;

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

        // ── Debug ──────────────────────────────────────
        System.out.println("LineItems count: " + cartAfterAdd.getLineItems().size());
        cartAfterAdd.getLineItems().forEach(item -> {
            System.out.println("Item id: " + item.getId());
            System.out.println("Item productId: " + item.getProductId());
        });

        // Extract lineItemId from response
        LineItem addedItem = cartAfterAdd.getLineItems()
                .stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Line item not found after adding"));

        String lineItemId = addedItem.getId();
        long unitCentAmount = addedItem.getTotalPrice().getCentAmount();

        System.out.println("lineItemId: " + lineItemId);
        System.out.println("unitCentAmount: " + unitCentAmount);

 // calculations dynamically
        double taxRate_amount = 0.14;
        long totalGrossCentAmount = Math.round(unitCentAmount * (1 + taxRate_amount));

        System.out.println("totalGrossCentAmount: " + totalGrossCentAmount);
        // setLineItemTaxAmount
        Money totalGross = new Money();
        totalGross.setCurrencyCode("USD");
//totalGross.setCentAmount(2622); hardcoded which is wrong, gross should be higher than net
        totalGross.setCentAmount(totalGrossCentAmount);

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

        //after adding item the quantity should be reduced
        InventoryCheckRequest reduceRequest = new InventoryCheckRequest();
        reduceRequest.setProductId(productId);
        reduceRequest.setQuantity(quantity);

        inventoryClient.reduce(reduceRequest);

       return finalCart;
    }


    //for shipping part
    public CartResponse addShipping(ShippingRequest request) {

        // setShippingAddress ─────────────────
        ShippingAddress address = new ShippingAddress();
        address.setKey("DTC");
        address.setFirstName(request.getFirstName());
        address.setLastName(request.getLastName());
        address.setStreetNumber(request.getStreetNumber());
        address.setAdditionalStreetInfo(request.getAdditionalStreetInfo());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());

        CartAction addressAction = new CartAction();
        addressAction.setAction("setShippingAddress");
        addressAction.setAddress(address);

        CartUpdateRequest addressRequest = new CartUpdateRequest();
        addressRequest.setVersion(request.getVersion());
        addressRequest.setActions(List.of(addressAction));

        CartResponse afterAddress = ctCartClient
                .updateCart(request.getCartId(), addressRequest).getBody();

        // ─ setShippingMethod ──────────────────
        ShippingMethodReference methodRef = new ShippingMethodReference();
        methodRef.setId(request.getShippingMethodId());
        methodRef.setTypeId("shipping-method");

        CartAction methodAction = new CartAction();
        methodAction.setAction("setShippingMethod");
        methodAction.setShippingMethod(methodRef);

        CartUpdateRequest methodRequest = new CartUpdateRequest();
        methodRequest.setVersion(afterAddress.getVersion());   //updated version
        methodRequest.setActions(List.of(methodAction));

        CartResponse afterMethod = ctCartClient
                .updateCart(request.getCartId(), methodRequest).getBody();

        //  setShippingMethodTaxAmount ─────────
        Money shippingGross = new Money();
        shippingGross.setCurrencyCode("USD");
        shippingGross.setCentAmount(request.getShippingTaxCentAmount());  // 658

        TaxRate shippingTaxRate = new TaxRate();
        shippingTaxRate.setName("myTaxRate");
        shippingTaxRate.setAmount(0.10);
        shippingTaxRate.setCountry("US");

        ExternalTaxAmount shippingTax = new ExternalTaxAmount();
        shippingTax.setTotalGross(shippingGross);
        shippingTax.setTaxRate(shippingTaxRate);

        CartAction shippingTaxAction = new CartAction();
        shippingTaxAction.setAction("setShippingMethodTaxAmount");
        shippingTaxAction.setExternalTaxAmount(shippingTax);

        CartUpdateRequest shippingTaxRequest = new CartUpdateRequest();
        shippingTaxRequest.setVersion(afterMethod.getVersion());
        shippingTaxRequest.setActions(List.of(shippingTaxAction));

        CartResponse afterShippingTax = ctCartClient
                .updateCart(request.getCartId(), shippingTaxRequest).getBody();

        //  setCartTotalTax ────────────────────
//        Money totalGross = new Money();
//        totalGross.setCurrencyCode("USD");
//        totalGross.setCentAmount(request.getCartTotalCentAmount());  // 4930
//
//        CartAction totalTaxAction = new CartAction();
//        totalTaxAction.setAction("setCartTotalTax");
//        totalTaxAction.setExternalTotalGross(totalGross);
//
//        CartUpdateRequest totalTaxRequest = new CartUpdateRequest();
//        totalTaxRequest.setVersion(afterShippingTax.getVersion());
//        totalTaxRequest.setActions(List.of(totalTaxAction));


        long lineItemsTotal = afterShippingTax.getLineItems()
                .stream()
                .mapToLong(item -> item.getTotalPrice().getCentAmount())
                .sum();


//Calculate line item gross with tax
        double lineItemTaxRate = 0.14;
        long lineItemGross = Math.round(lineItemsTotal * (1 + lineItemTaxRate));
// 4000 * 1.14 = 4560

//Get shipping gross from request (658 — already taxed value)
        long shippingGrossTotal = request.getShippingTaxCentAmount();
// 658

// Final cart total = lineItem gross + shipping gross
        long cartTotalGross = lineItemGross + shippingGrossTotal;
// 4560 + 658 = 5218

        Money totalGross = new Money();
        totalGross.setCurrencyCode("USD");
        totalGross.setCentAmount(cartTotalGross);

        CartAction totalTaxAction = new CartAction();
        totalTaxAction.setAction("setCartTotalTax");
        totalTaxAction.setExternalTotalGross(totalGross);

        CartUpdateRequest totalTaxRequest = new CartUpdateRequest();
        totalTaxRequest.setVersion(afterShippingTax.getVersion());
        totalTaxRequest.setActions(List.of(totalTaxAction));

        return ctCartClient
                .updateCart(request.getCartId(), totalTaxRequest).getBody();
    }
}

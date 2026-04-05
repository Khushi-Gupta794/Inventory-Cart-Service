package Cart_Service.App.Service;

import Cart_Service.App.DTO.*;
import Cart_Service.App.FeignClient.CTCartClient;
import Cart_Service.App.FeignClient.InventoryClient;
import Cart_Service.App.FeignClient.MeCustomerClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeCartService {
    private final MeCustomerClient meCustomerClient;    // /me/carts endpoints
    private final InventoryClient inventoryClient;
    private final CTCartClient ctCartClient;

    public MeCartService(MeCustomerClient meCustomerClient, InventoryClient inventoryClient, CTCartClient ctCartClient) {
        this.inventoryClient = inventoryClient;
        this.meCustomerClient = meCustomerClient;
        this.ctCartClient = ctCartClient;
    }
  //fixes done as me will not set the tax, called the admin apis
    public CartResponse addToMyCart(String productId, int quantity) {

        // Inventory Check
        InventoryCheckRequest inventoryRequest = new InventoryCheckRequest();
        inventoryRequest.setProductId(productId); //for product must be available in ct also
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
        //   CartResponse currentCart = meCustomerClient.getCart(cartId).getBody();

        CartResponse currentCart;
        try {
            currentCart = meCustomerClient.getMyActiveCart().getBody();
        } catch (Exception e) {
            // No active cart — create one
            CartRequest cartRequest = new CartRequest();
            cartRequest.setCurrency("USD");
            cartRequest.setTaxMode("ExternalAmount");
            currentCart = meCustomerClient.createMyCart(cartRequest).getBody();
        }

        String cartId = currentCart.getId();

        // addLineItem
        CartAction addAction = new CartAction();
        addAction.setAction("addLineItem");
        addAction.setProductId(productId);
        addAction.setVariantId(1);
        addAction.setQuantity(quantity);

        CartUpdateRequest addRequest = new CartUpdateRequest();
        addRequest.setVersion(currentCart.getVersion());
        addRequest.setActions(List.of(addAction));

        //here iam calling my controller once, but in ct two api calls as per the actions because one
        //return lineitemid other uses that to add tax, not possible in one ct call especially for external amount
        try {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("addRequest JSON ");
            System.out.println(mapper.writeValueAsString(addRequest));
        } catch (Exception e) {
            System.out.println("JSON error: " + e.getMessage());
        }

        CartResponse cartAfterAdd = meCustomerClient.updateMyCart(cartId, addRequest).getBody();

        System.out.println("original cartId: " + cartId);
        System.out.println("cartAfterAdd.getId(): " + cartAfterAdd.getId());
        System.out.println("cartAfterAdd.getVersion(): " + cartAfterAdd.getVersion());

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

        try {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("=== taxRequest JSON ===");
            System.out.println(mapper.writeValueAsString(taxRequest));
        } catch (Exception e) {
            System.out.println("JSON error: " + e.getMessage());
        }

        CartResponse finalCart = ctCartClient.updateCart(cartId, taxRequest).getBody();

        //after adding item the quantity should be reduced
        InventoryCheckRequest reduceRequest = new InventoryCheckRequest();
        reduceRequest.setProductId(productId);
        reduceRequest.setQuantity(quantity);

        inventoryClient.reduce(reduceRequest);

        return finalCart;
    }


    //for shipping part
    public CartResponse addMyShipping(ShippingRequest request) {

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

        CartResponse afterAddress = meCustomerClient
                .updateMyCart(request.getCartId(), addressRequest).getBody();

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

        CartResponse afterMethod = meCustomerClient
                .updateMyCart(request.getCartId(), methodRequest).getBody();

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


    public OrderResponse placeMyOrder(String cartId, int version) {
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
       // CartResponse cart = meCustomerClient.getCart(cartId).getBody();
        CartResponse cart = meCustomerClient.getMyActiveCart().getBody();

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
                /// currentCart = meCustomerClient.updateMyCart(cartId, taxRequest).getBody();
                currentCart = ctCartClient.updateCart(cartId, taxRequest).getBody();

                System.out.println("Tax fixed for lineItem: " + item.getId());
            }
        }

        // ── Step 4: Place order ────────────────────
//        CartReference cartRef = new CartReference();
//        cartRef.setId(cartId);
//        cartRef.setTypeId("cart");

//        OrderRequest orderRequest = new OrderRequest();
//        orderRequest.setCart(cartRef);
//        orderRequest.setVersion(version);
//        OrderRequest orderRequest = new OrderRequest();
//        orderRequest.setCart(cartRef);
//        orderRequest.setVersion(currentCart.getVersion());  //use latest version

        MeOrderRequest orderRequest = new MeOrderRequest();
        orderRequest.setId(cartId);                        // direct cartId
        orderRequest.setVersion(currentCart.getVersion());

        OrderResponse order = meCustomerClient.placeMyOrder(orderRequest).getBody();

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

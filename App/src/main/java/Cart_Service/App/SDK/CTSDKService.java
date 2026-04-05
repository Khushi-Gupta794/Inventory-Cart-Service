package Cart_Service.App.SDK;

import Cart_Service.App.DTO.InventoryCheckRequest;
import Cart_Service.App.DTO.InventoryResponse;
import Cart_Service.App.FeignClient.InventoryClient;
import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.shipping_method.ShippingMethodResourceIdentifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CTSDKService {
    private final ProjectApiRoot projectApiRoot;
    private final InventoryClient inventoryClient;

    public CTSDKService(ProjectApiRoot projectApiRoot,InventoryClient inventoryClient){
        this.projectApiRoot = projectApiRoot;
        this.inventoryClient = inventoryClient;
    }

    public Cart addToCart(String cartId, String productId, int quantity){
        // ── Inventory check — still uses Feign ────
        InventoryCheckRequest inventoryRequest = new InventoryCheckRequest();
        inventoryRequest.setProductId(productId);
        inventoryRequest.setQuantity(quantity);
        InventoryResponse inv = inventoryClient.checkStock(inventoryRequest);

        if (!inv.isAvailable()) {
            throw new RuntimeException("Product " + productId + " is out of stock");
        }
        if (inv.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Only " + inv.getAvailableQuantity() + " available");
        }
    //to get first cart
        Cart cart =projectApiRoot.carts().withId(cartId).get().executeBlocking().getBody();

        //interface already created to add line item and add external tax
        CartAddLineItemAction addLineItemAction= CartAddLineItemAction.builder().productId(productId)
                .variantId(1L).quantity((long)quantity).externalTaxRate( ExternalTaxRateDraft.builder().name("myTaxRate").amount(0.14).country("US")
                        .includedInPrice(false)
                        .build()
                )
                .build();
     //calling line item and tAX IN CALL
     return projectApiRoot.carts().withId(cartId).post(CartUpdate.builder().version(cart.getVersion()).actions(List.of(addLineItemAction))
                        .build()
        ).executeBlocking().getBody();
    }

    public Cart addShipping(String cartId, long version, String shippingMethodId, Address address){
        return projectApiRoot.carts().withId(cartId).post(CartUpdate.builder().version(version)
                .actions(List.of(
                        //set address
                        CartSetShippingAddressAction.builder().address(address).build(),
                     //set shipping method
                     CartSetShippingMethodAction.builder().shippingMethod(ShippingMethodResourceIdentifier.builder().id(shippingMethodId).build()).build()
                ))
                .build()).executeBlocking().getBody();
    }


}

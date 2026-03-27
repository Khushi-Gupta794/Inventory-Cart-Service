package Cart_Service.App.DTO;

import lombok.Data;

import java.util.List;

@Data
public class CartRequest {
    private String currency;        // "USD"
    private String customerId;
    private String taxMode;         // "External" for external tax
  //  private String country;         // "US"
  //  private List<LineItemDraft> lineItems;
}

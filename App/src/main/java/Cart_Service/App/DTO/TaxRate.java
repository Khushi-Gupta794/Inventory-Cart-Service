package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaxRate {
    private String name;
    private double amount;
    private boolean includedInPrice;
    private String country;
}


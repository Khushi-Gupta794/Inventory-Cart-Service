package Cart_Service.App.DTO;

import lombok.Data;

@Data
public class TaxRate {
    private String name;
    private double amount;
    private boolean includedInPrice;
    private String country;
}


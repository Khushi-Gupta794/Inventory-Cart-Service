package Cart_Service.App.DTO;

import lombok.Data;

@Data
public class ExternalTaxAmount {
    private Money totalGross;
    private TaxRate taxRate;
}

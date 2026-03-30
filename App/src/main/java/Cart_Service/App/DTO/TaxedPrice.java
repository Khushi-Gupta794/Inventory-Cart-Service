package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaxedPrice {
    private TotalPrice totalNet;
    private TotalPrice totalGross;
    private TotalPrice totalTax;
}
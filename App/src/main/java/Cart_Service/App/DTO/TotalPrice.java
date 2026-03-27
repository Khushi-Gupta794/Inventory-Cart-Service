package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TotalPrice {
    private String type;
    private String currencyCode;
    private long centAmount;       // CT uses cents — 1000 = $10.00
    private int fractionDigits;
}

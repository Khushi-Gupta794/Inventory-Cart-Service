package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerPagedResponseDTO {
    private int limit;
    private int offset;
    private int count;
    private int total;
    private List<CustomerResponse> results;  // not here customerapiresponse because for create it was in wrapper of customer not in get
}

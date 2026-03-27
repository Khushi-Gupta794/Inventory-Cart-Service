package Cart_Service.App.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartPagedResponse {
    private int limit;
    private int offset;
    private int count;
    private int total;
    private List<CartResponse> results;
}

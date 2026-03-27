package Cart_Service.App.MyController;

import Cart_Service.App.DTO.CustomerApiResponseDTO;
import Cart_Service.App.DTO.CustomerPagedResponseDTO;
import Cart_Service.App.DTO.CustomerRequest;
import Cart_Service.App.FeignClient.CTCustomerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private CTCustomerClient ctCustomerClient;

    public CustomerController(CTCustomerClient ctCustomerClient) {
        this.ctCustomerClient = ctCustomerClient;
    }
   //post /api/customers/register
    @PostMapping("/register")
    public ResponseEntity<CustomerApiResponseDTO> createCustomer(@RequestBody CustomerRequest customerRequest){
        return ctCustomerClient.createCustomer(customerRequest);
    }

    // GET /api/customers
    @GetMapping
    public ResponseEntity<CustomerPagedResponseDTO> getAllCustomers() {
        return ctCustomerClient.getAllCustomers();
    }


}

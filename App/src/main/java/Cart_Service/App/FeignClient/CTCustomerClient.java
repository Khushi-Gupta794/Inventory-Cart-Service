package Cart_Service.App.FeignClient;

import Cart_Service.App.DTO.CustomerApiResponseDTO;
import Cart_Service.App.DTO.CustomerPagedResponseDTO;
import Cart_Service.App.DTO.CustomerRequest;
import Cart_Service.App.Interceptors.AdminFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name =  "ct-customer", url = "https://api.us-east-2.aws.commercetools.com",  configuration = AdminFeignConfig.class )
public interface CTCustomerClient {
    @PostMapping("/ecom-app-123456/customers")
    ResponseEntity<CustomerApiResponseDTO> createCustomer( @RequestBody CustomerRequest customerRequest);
   //@RequestHeader("Authorization") String token --now interceptor will manage auto apply my apply method , no manually writing now
    @GetMapping("/ecom-app-123456/customers")
    ResponseEntity<CustomerPagedResponseDTO> getAllCustomers();


}

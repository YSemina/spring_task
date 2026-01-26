package y.semina.service;

import y.semina.dto.CustomerResponseDto;
import y.semina.model.Customer;

public interface CustomerService {

    CustomerResponseDto findCustomerById (Long id);

    Customer getCustomerById(Long id);

    Customer findCustomerByUsername(String username);

}

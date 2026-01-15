package y.semina.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import y.semina.dto.CustomerResponseDto;
import y.semina.exeption.CustomerNotFoundException;
import y.semina.model.Customer;
import y.semina.repository.CustomerRepository;
import y.semina.service.CustomerService;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerJsonConverter converterCustomer;

    @Override
    public CustomerResponseDto findCustomerById (Long id){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Покупателя с id " + id + " не существует."));
        return converterCustomer.entityToResponseDto(customer);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Покупателя с id " + id + " не существует."));
    }

}

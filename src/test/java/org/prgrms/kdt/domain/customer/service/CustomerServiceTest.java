package org.prgrms.kdt.domain.customer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prgrms.kdt.domain.customer.model.Customer;
import org.prgrms.kdt.domain.customer.repository.CustomerRepository;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerService customerService;

    @Test
    @DisplayName("블랙리스트 고객들을 조회할 수 있다.")
    public void findBlackList(){
        //given
        Customer customerPark = new Customer.Builder(UUID.randomUUID(), "a@naver.com").name("park").build();
        Customer customerKim = new Customer.Builder(UUID.randomUUID(), "b@gmail.com").name("kim").build();
        List<Customer> savedBlackCustomers = Arrays.asList(customerPark, customerKim);
        //when
        when(customerRepository.findAll()).thenReturn(savedBlackCustomers);
        List<Customer> blackListCustomers = customerService.findBlackList();
        //then
        assertThat(blackListCustomers).contains(customerKim, customerPark);
    }

    @Test
    public void saveCustomers() throws Exception {
        //given

        //when
        //then
    }

    @Test
    public void saveCustomer() throws Exception {
        //given

        //when

        //then
    }


}
package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findBySystemCustomerNo(String systemCustomerNo);
    Customer findBySessionNo(String sessionNo);
    Customer findTopByOrderByIdDesc();
    Customer findFirstBySystemCustomerNoNotNullOrderByIdDesc();
    List<Customer> findAllBySystemCustomerNoIsNull();
    List<Customer> findBySystemCustomerNoIsNull();
}

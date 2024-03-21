package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurrentPaymentRepository extends JpaRepository<RecurrentPayment, Long> {
    RecurrentPayment findBySystemCustomerNo(String systemCustomerNo);

    RecurrentPayment findFirstByOrderByIdDesc();

    RecurrentPayment findByJobNo(String jobNo);

}

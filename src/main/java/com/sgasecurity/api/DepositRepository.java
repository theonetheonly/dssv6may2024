package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    Deposit findBySystemCustomerNo(String systemCustomerNo);

    Deposit findFirstByOrderByIdDesc();
}

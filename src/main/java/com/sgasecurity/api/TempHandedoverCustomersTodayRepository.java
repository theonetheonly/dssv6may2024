package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TempHandedoverCustomersTodayRepository extends JpaRepository<TempHandedoverCustomersToday, Integer> {
    TempHandedoverCustomersToday findBySystemCustomerNo(String systemCustomerNo);
    List<TempHandedoverCustomersToday> findByHandoverDate(LocalDate handoverDate);
}

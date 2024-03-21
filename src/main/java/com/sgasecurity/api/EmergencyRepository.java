package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyRepository extends JpaRepository<Emergency, Long> {
    Emergency findByEventId(String eventId);
    Emergency findBySystemCustomerNo(String systemCustomerNo);
}

package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HandoverEventRepository extends JpaRepository<HandoverEvent, Long> {
    HandoverEvent findByEventId(String eventId);
    HandoverEvent findBySystemCustomerNo(String systemCustomerNo);
}

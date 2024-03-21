package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HandoverEventService {
    @Autowired
    HandoverEventRepository handoverEventRepository;
    public HandoverEvent getEventByEventId(String eventId) {
        return handoverEventRepository.findByEventId(eventId);
    }

    public HandoverEvent getEventBySystemCustomerNo(String systemCustomerNo) {
        return handoverEventRepository.findBySystemCustomerNo(systemCustomerNo);
    }

    public HandoverEvent saveHandoverEvent(HandoverEvent handoverEvent)
    {
        return handoverEventRepository.save(handoverEvent);
    }
}

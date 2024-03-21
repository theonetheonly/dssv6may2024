package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmergencyService {
    @Autowired
    EmergencyRepository emergencyRepository;
    public Emergency getEventByEventId(String eventId) {
        return emergencyRepository.findByEventId(eventId);
    }

    public Emergency getEmergencyBySystemCustomerNo(String systemCustomerNo) {
        return emergencyRepository.findBySystemCustomerNo(systemCustomerNo);
    }

    public Emergency getEmergencyByEventId(String eventId) {
        return emergencyRepository.findByEventId(eventId);
    }

    public Emergency saveEmergency(Emergency emergency)
    {
        return emergencyRepository.save(emergency);
    }

    public List<Emergency> getAllEmergencies()
    {
        return emergencyRepository.findAll();
    }
}

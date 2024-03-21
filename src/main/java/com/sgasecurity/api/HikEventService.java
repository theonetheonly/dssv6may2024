package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HikEventService {
    @Autowired
    HikEventRepository hikEventRepository;

    public HikEvent getHikEventBySystemCustomerNo(String systemCustomerNo) {
        return (HikEvent)this.hikEventRepository.findBySystemCustomerNo(systemCustomerNo);
    }

    public HikEvent getHikEventByHppSiteId(String hppSiteId) {
        return (HikEvent)this.hikEventRepository.findByHppSiteId(hppSiteId);
    }

    public HikEvent getHikEventByUniqueSiteId(String uniqueSiteId) {
        return (HikEvent)this.hikEventRepository.findByUniqueSiteId(uniqueSiteId);
    }

    public HikEvent saveHikEvent(HikEvent hikEvent)
    {
        return hikEventRepository.save(hikEvent);
    }
}

package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewMonthService {
    @Autowired
    NewMonthRepository newMonthRepository;
    public NewMonth getNewMonthByUniqueSiteId(String uniqueSiteId) {
        return (NewMonth)this.newMonthRepository.findByUniqueSiteId(uniqueSiteId);
    }
    public NewMonth saveNewMonth(NewMonth newMonth)
    {
        return newMonthRepository.save(newMonth);
    }
}

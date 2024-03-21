package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TempHandedoverCustomersTodayService {
    @Autowired
    TempHandedoverCustomersTodayRepository tempHandedoverCustomersTodayRepository;
    public List<TempHandedoverCustomersToday> getTempHandedoverCustomersTodayByHandoverDate(LocalDate handoverDate)
    {
        return tempHandedoverCustomersTodayRepository.findByHandoverDate(handoverDate);
    }

    public TempHandedoverCustomersToday saveTempHandedoverCustomersToday(TempHandedoverCustomersToday tempHandedoverCustomersToday)
    {
        return tempHandedoverCustomersTodayRepository.save(tempHandedoverCustomersToday);
    }

    public TempHandedoverCustomersToday getTempHandedOverCustomersTodayBySystemCustomerNo(String systemCustomerNo) {
        return this.tempHandedoverCustomersTodayRepository.findBySystemCustomerNo(systemCustomerNo);
    }
}

package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecurrentPaymentService {
    @Autowired
    RecurrentPaymentRepository recurrentPaymentRepository;
    public List<RecurrentPayment> getAllRecurrentPayments()
    {
        List<RecurrentPayment> recurrentPayments = (List<RecurrentPayment>) recurrentPaymentRepository.findAll();
        return recurrentPayments;
    }

    public RecurrentPayment getRecurrentPayment(String systemCustomerNo) {
        return (RecurrentPayment) this.recurrentPaymentRepository.findBySystemCustomerNo(systemCustomerNo);
    }

    public RecurrentPayment saveRecurrentPayment(RecurrentPayment recurrentPayment)
    {
        return recurrentPaymentRepository.save(recurrentPayment);
    }

    public RecurrentPayment getLastRecurrentPayment() {
        return recurrentPaymentRepository.findFirstByOrderByIdDesc();
    }

    public RecurrentPayment getRecurrentPaymentByJobNo(String jobNo) {
        return recurrentPaymentRepository.findByJobNo(jobNo);
    }
}

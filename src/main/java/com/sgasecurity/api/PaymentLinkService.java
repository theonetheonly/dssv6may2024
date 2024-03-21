package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentLinkService {
    @Autowired
    PaymentLinkRepository paymentLinkRepository;
    public PaymentLink getPaymentLinkBySystemCustomerNo(String systemCustomerNo) {
        return (PaymentLink)this.paymentLinkRepository.findBySystemCustomerNo(systemCustomerNo);
    }

    public PaymentLink getPaymentLinkByTokenId(String tokenId) {
        return (PaymentLink)this.paymentLinkRepository.findByTokenId(tokenId);
    }

    public PaymentLink savePaymentLink(PaymentLink paymentLink)
    {
        return paymentLinkRepository.save(paymentLink);
    }

    public PaymentLink getPaymentLink(String customerNo, String tokenId) {
        return (PaymentLink)this.paymentLinkRepository.findPaymentLink(customerNo, tokenId);
    }

    public PaymentLink getLatestPaymentLink(String customerNo) {
        return paymentLinkRepository.findLatestPaymentLink(customerNo);
    }

    public List<PaymentLink> getPaymentLinkByIsUsed(String isUsed) {
        return paymentLinkRepository.findByIsUsed(isUsed);
    }

    public List<PaymentLink> getPaymentLinksBySystemCustomerNoIsNo(String customerNo) {
        return paymentLinkRepository.findPaymentLinksBySystemCustomerNoIsNo(customerNo);
    }

    public List<PaymentLink> getPaymentLinksBySystemCustomerNoIsYes(String customerNo) {
        return paymentLinkRepository.findPaymentLinksBySystemCustomerNoIsYes(customerNo);
    }
}

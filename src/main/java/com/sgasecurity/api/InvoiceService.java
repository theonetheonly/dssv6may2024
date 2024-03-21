package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {
    @Autowired
    InvoiceRepository invoiceRepository;

    public Invoice getInvoiceById(long id) {
        return this.invoiceRepository.findById(id);
    }

    public Invoice getInvoiceByInvoiceRefNo(String invoiceRefNo) {
        return (Invoice) this.invoiceRepository.findByInvoiceRefNo(invoiceRefNo);
    }

    public Invoice getInvoiceByTokenId(String tokenId) {
        return this.invoiceRepository.findByTokenId(tokenId);
    }

    public Invoice getInvoiceBySystemCustomerNoAndTimestamp(String systemCustomerNo, LocalDate invoicingDate) {
        return (Invoice) this.invoiceRepository.findBySystemCustomerNoAndInvoicingDate(systemCustomerNo, invoicingDate);
    }

    public Invoice saveInvoice(Invoice invoice)
    {
        return invoiceRepository.save(invoice);
    }

    public Invoice getInvoiceBySystemCustomerNo(String systemCustomerNo) {
        return (Invoice) this.invoiceRepository.findBySystemCustomerNo(systemCustomerNo);
    }

    public List<Invoice> getAllInvoices() {
        return (List<Invoice>) this.invoiceRepository.findAll();
    }

    public Invoice findLatestInvoiceForCustomer(String customerNo) {
        return invoiceRepository.findTop1BySystemCustomerNoOrderByIdDesc(customerNo);
    }
}

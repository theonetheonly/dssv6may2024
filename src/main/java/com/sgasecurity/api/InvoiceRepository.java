package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Invoice findById(long id);
    Invoice findBySystemCustomerNoAndInvoicingDate(String systemCustomerNo, LocalDate invoicingDate);
    Invoice findBySystemCustomerNo(String systemCustomerNo);
    Invoice findByInvoiceRefNo(String invoiceRefNo);
    Invoice findByTokenId(String tokenId);

    Invoice findTop1BySystemCustomerNoOrderByIdDesc(String customerNo);
}

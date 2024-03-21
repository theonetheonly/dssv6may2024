package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "amount")
    private double amount;
    @Column(name = "system_customer_no")
    private String systemCustomerNo;
    @Column(name = "invoice_ref_no")
    private String invoiceRefNo;
    @Column(name = "installation_id")
    private long installationId;
    @Column(name = "package_type_id")
    private int packageTypeId;
    @Column(name = "invoicing_date")
    private LocalDate invoicingDate;
    @Column(name = "narrative")
    private String narrative;
    @Column(name = "is_paid_out")
    private int isPaidOut;
    @Column(name = "date_paid")
    private LocalDate datePaid;
    @Column(name = "token_id")
    private String tokenId;
    @Column(name = "is_printed")
    private String isPrinted;
    @Column(name = "invoice_response")
    private String invoiceResponse;
    @Column(name = "month_count")
    private int monthCount;

    @Column(name = "payment_link_id")
    private long paymentLinkId;

    @Column(name = "allocation_tranx_no")
    private String allocationTranxNo;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    public Invoice() {
    }

    public Invoice(long id, double amount, String systemCustomerNo, String invoiceRefNo, long installationId, int packageTypeId, LocalDate invoicingDate, String narrative, int isPaidOut, LocalDate datePaid, String tokenId, String isPrinted, int monthCount, String invoiceResponse, long paymentLinkId, String allocationTranxNo, Timestamp timestamp) {
        this.id = id;
        this.amount = amount;
        this.systemCustomerNo = systemCustomerNo;
        this.invoiceRefNo = invoiceRefNo;
        this.installationId = installationId;
        this.packageTypeId = packageTypeId;
        this.invoicingDate = invoicingDate;
        this.narrative = narrative;
        this.isPaidOut = isPaidOut;
        this.datePaid = datePaid;
        this.tokenId = tokenId;
        this.isPrinted = isPrinted;
        this.monthCount = monthCount;
        this.invoiceResponse = invoiceResponse;
        this.paymentLinkId = paymentLinkId;
        this.allocationTranxNo = allocationTranxNo;
        this.timestamp = timestamp;
    }

    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public Double getAmount()
    {
        return amount;
    }
    public void setAmount(Double amount)
    {
        this.amount = amount;
    }
    public String getSystemCustomerNo()
    {
        return systemCustomerNo;
    }
    public void setSystemCustomerNo(String systemCustomerNo)
    {
        this.systemCustomerNo = systemCustomerNo;
    }
    public String getInvoiceRefNo() {
        return invoiceRefNo;
    }
    public void setInvoiceRefNo(String invoiceRefNo) {
        this.invoiceRefNo = invoiceRefNo;
    }
    public long getInstallationId() {
        return installationId;
    }
    public void setInstallationId(long installationId) {
        this.installationId = installationId;
    }

    public int getPackageTypeId() {
        return packageTypeId;
    }

    public void setPackageTypeId(int packageTypeId) {
        this.packageTypeId = packageTypeId;
    }
    public LocalDate getInvoicingDate()
    {
        return invoicingDate;
    }
    public void setInvoicingDate(LocalDate invoicingDate)
    {
        this.invoicingDate = invoicingDate;
    }
    public String getNarrative()
    {
        return narrative;
    }
    public void setNarrative(String narrative)
    {
        this.narrative = narrative;
    }

    public int getIsPaidOut()
    {
        return isPaidOut;
    }
    public void setIsPaidOut(int isPaidOut)
    {
        this.isPaidOut = isPaidOut;
    }
    public LocalDate getDatePaid()
    {
        return datePaid;
    }
    public void setDatePaid(LocalDate datePaid)
    {
        this.datePaid = datePaid;
    }
    public String getTokenId()
    {
        return tokenId;
    }
    public void setTokenId(String tokenId)
    {
        this.tokenId = tokenId;
    }

    public String getIsPrinted()
    {
        return isPrinted;
    }
    public void setIsPrinted(String isPrinted)
    {
        this.isPrinted = isPrinted;
    }

    public int getMonthCount()
    {
        return monthCount;
    }
    public void setMonthCount(int monthCount)
    {
        this.monthCount = monthCount;
    }

    public long getPaymentLinkId()
    {
        return paymentLinkId;
    }
    public void setPaymentLinkId(long paymentLinkId)
    {
        this.paymentLinkId = paymentLinkId;
    }

    public String getInvoiceResponse()
    {
        return invoiceResponse;
    }
    public void setInvoiceResponse(String invoiceResponse)
    {
        this.invoiceResponse = invoiceResponse;
    }

    public String getAllocationTranxNo()
    {
        return allocationTranxNo;
    }
    public void setAllocationTranxNo(String allocationTranxNo)
    {
        this.allocationTranxNo = allocationTranxNo;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

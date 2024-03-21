package com.sgasecurity.api;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_links")
public class PaymentLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "system_customer_no")
    private String systemCustomerNo;
    @Column(name = "link")
    private String link;
    @Column(name = "token_id")
    private String tokenId;
    @Column(name = "amount")
    private double amount;
    @Column(name = "is_used")
    private String isUsed;
    @Column(name = "invoice_id")
    private long invoiceId;
    @Column(name = "month_count")
    private int monthCount;
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public PaymentLink() {
    }

    public PaymentLink(long id, String systemCustomerNo, String link, String tokenId, double amount, String isUsed, long invoiceId, int monthCount, Timestamp timestamp) {
        this.id = id;
        this.systemCustomerNo = systemCustomerNo;
        this.link = link;
        this.tokenId = tokenId;
        this.amount = amount;
        this.isUsed = isUsed;
        this.invoiceId = invoiceId;
        this.monthCount = monthCount;
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
    public String getSystemCustomerNo()
    {
        return systemCustomerNo;
    }
    public void setSystemCustomerNo(String systemCustomerNo)
    {
        this.systemCustomerNo = systemCustomerNo;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getTokenId()
    {
        return tokenId;
    }
    public void setTokenId(String tokenId)
    {
        this.tokenId = tokenId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getIsUsed() {
        return isUsed;
    }
    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }

    public long getInvoiceId() {
        return invoiceId;
    }
    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getMonthCount() {
        return monthCount;
    }
    public void setMonthCount(int monthCount) {
        this.monthCount = monthCount;
    }
    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

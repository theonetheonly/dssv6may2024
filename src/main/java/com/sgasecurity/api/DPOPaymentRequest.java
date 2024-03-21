package com.sgasecurity.api;

public class DPOPaymentRequest {
    private Double paymentAmount;
    private String paymentCurrency;
    private String companyRef;
    private String redirectURL;
    private String backURL;
    private String companyRefUnique;
    private int PTL;
    private String companyAccRef;
    private String PTLType;
    private String defaultPayment;
    private String allowRecurrent;
    private String customerFirstName;
    private String customerLastName;
    private String customerCity;
    private String customerCountry;
    private String cardHolderName;
    private String customerEmail;
    private String customerPhone;
    private String defaultPaymentCountry;
    private String packageName;
    private double vat;
    private double amountExclusive;
    private String invoicePeriod;
    private String DPOMarkup;
    private String tokenId;
    private String dpoTransId;
    private int customerId;
    private String dynamicCompanyRef;
    private String companyToken;
    private String serviceType;

    // Constructor, getters, and setters
    public DPOPaymentRequest() {
    }

    public DPOPaymentRequest(Double paymentAmount, String paymentCurrency, String companyRef, String redirectURL, String backURL, String companyRefUnique, int PTL, String companyAccRef, String PTLType, String defaultPayment, String allowRecurrent, String customerFirstName, String customerLastName, String customerCity, String customerCountry, String cardHolderName, String customerEmail, String customerPhone, String defaultPaymentCountry, String packageName, double vat, double amountExclusive, String invoicePeriod, String DPOMarkup, String tokenId, String dpoTransId, int customerId, String dynamicCompanyRef, String companyToken, String serviceType) {
        this.paymentAmount = paymentAmount;
        this.paymentCurrency = paymentCurrency;
        this.companyRef = companyRef;
        this.redirectURL = redirectURL;
        this.backURL = backURL;
        this.companyRefUnique = companyRefUnique;
        this.PTL = PTL;
        this.companyAccRef = companyAccRef;
        this.PTLType = PTLType;
        this.defaultPayment = defaultPayment;
        this.allowRecurrent = allowRecurrent;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerCity = customerCity;
        this.customerCountry = customerCountry;
        this.cardHolderName = cardHolderName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.defaultPaymentCountry = defaultPaymentCountry;
        this.packageName = packageName;
        this.vat = vat;
        this.amountExclusive = amountExclusive;
        this.invoicePeriod = invoicePeriod;
        this.DPOMarkup = DPOMarkup;
        this.tokenId = tokenId;
        this.dpoTransId = dpoTransId;
        this.customerId = customerId;
        this.dynamicCompanyRef = dynamicCompanyRef;
        this.companyToken = companyToken;
        this.serviceType = serviceType;
    }

    public Double getPaymentAmount()
    {
        return paymentAmount;
    }
    public void setPaymentAmount(Double paymentAmount)
    {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentCurrency()
    {
        return paymentCurrency;
    }
    public void setPaymentCurrency(String paymentCurrency)
    {
        this.paymentCurrency = paymentCurrency;
    }

    public String getCompanyRef()
    {
        return companyRef;
    }
    public void setCompanyRef(String companyRef)
    {
        this.companyRef = companyRef;
    }

    public String getRedirectURL()
    {
        return redirectURL;
    }
    public void setRedirectURL(String redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    public String getBackURL()
    {
        return backURL;
    }
    public void setBackURL(String backURL)
    {
        this.backURL = backURL;
    }

    public String getCompanyRefUnique()
    {
        return companyRefUnique;
    }
    public void setCompanyRefUnique(String companyRefUnique)
    {
        this.companyRefUnique = companyRefUnique;
    }

    public int getPTL()
    {
        return PTL;
    }
    public void setPTL(int PTL)
    {
        this.PTL = PTL;
    }

    public String getCompanyAccRef()
    {
        return companyAccRef;
    }
    public void setCompanyAccRef(String companyAccRef)
    {
        this.companyAccRef = companyAccRef;
    }

    public String getPTLType()
    {
        return PTLType;
    }
    public void setPTLType(String PTLType)
    {
        this.PTLType = PTLType;
    }

    public String getDefaultPayment()
    {
        return defaultPayment;
    }
    public void setDefaultPayment(String defaultPayment)
    {
        this.defaultPayment = defaultPayment;
    }

    public String getAllowRecurrent()
    {
        return allowRecurrent;
    }
    public void setAllowRecurrent(String allowRecurrent)
    {
        this.allowRecurrent = allowRecurrent;
    }

    public String getCustomerFirstName()
    {
        return customerFirstName;
    }
    public void setCustomerFirstName(String customerFirstName)
    {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName()
    {
        return customerLastName;
    }
    public void setCustomerLastName(String customerLastName)
    {
        this.customerLastName = customerLastName;
    }

    public String getCustomerCity()
    {
        return customerCity;
    }
    public void setCustomerCity(String customerCity)
    {
        this.customerCity = customerCity;
    }

    public String getCustomerCountry()
    {
        return customerCountry;
    }
    public void setCustomerCountry(String customerCountry)
    {
        this.customerCountry = customerCountry;
    }

    public String getCardHolderName()
    {
        return cardHolderName;
    }
    public void setCardHolderName(String cardHolderName)
    {
        this.cardHolderName = cardHolderName;
    }

    public String getCustomerEmail()
    {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail)
    {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone()
    {
        return customerPhone;
    }
    public void setCustomerPhone(String customerPhone)
    {
        this.customerPhone = customerPhone;
    }

    public String getDefaultPaymentCountry()
    {
        return defaultPaymentCountry;
    }
    public void setDefaultPaymentCountry(String defaultPaymentCountry)
    {
        this.defaultPaymentCountry = defaultPaymentCountry;
    }
    public String getPackageName()
    {
        return packageName;
    }
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public double getVat()
    {
        return vat;
    }
    public void setVat(double vat)
    {
        this.vat = vat;
    }

    public double getAmountExclusive()
    {
        return amountExclusive;
    }
    public void setAmountExclusive(double amountExclusive)
    {
        this.amountExclusive = amountExclusive;
    }

    public String getInvoicePeriod()
    {
        return invoicePeriod;
    }
    public void setInvoicePeriod(String invoicePeriod)
    {
        this.invoicePeriod = invoicePeriod;
    }

    public String getDPOMarkup()
    {
        return DPOMarkup;
    }
    public void setDPOMarkup(String DPOMarkup)
    {
        this.DPOMarkup = DPOMarkup;
    }

    public String getTokenId()
    {
        return tokenId;
    }
    public void setTokenId(String tokenId)
    {
        this.tokenId = tokenId;
    }

    public String getDpoTransId()
    {
        return dpoTransId;
    }
    public void setDpoTransId(String dpoTransId)
    {
        this.dpoTransId = dpoTransId;
    }

    public int getCustomerId()
    {
        return customerId;
    }
    public void setCustomerId(int customerId)
    {
        this.customerId = customerId;
    }

    public String getDynamicCompanyRefd()
    {
        return dynamicCompanyRef;
    }
    public void setDynamicCompanyRef(String dynamicCompanyRef)
    {
        this.dynamicCompanyRef = dynamicCompanyRef;
    }

    public String getCompanyToken()
    {
        return companyToken;
    }
    public void setCompanyToken(String companyToken)
    {
        this.companyToken = companyToken;
    }

    public String getServiceType()
    {
        return serviceType;
    }
    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }
}

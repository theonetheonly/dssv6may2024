package com.sgasecurity.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DPOPaymentResponse")
public class DPOPaymentResponse {
    private String result;
    private String resultExplanation;
    private String transToken;
    private String transRef;

    public String getResult() {
        return result;
    }

    @XmlElement(name = "Result")
    public void setResult(String result) {
        this.result = result;
    }

    public String getResultExplanation() {
        return resultExplanation;
    }

    @XmlElement(name = "ResultExplanation")
    public void setResultExplanation(String resultExplanation) {
        this.resultExplanation = resultExplanation;
    }

    public String getTransToken() {
        return transToken;
    }

    @XmlElement(name = "TransToken")
    public void setTransToken(String transToken) {
        this.transToken = transToken;
    }

    public String getTransRef() {
        return transRef;
    }

    @XmlElement(name = "TransRef")
    public void setTransRef(String transRef) {
        this.transRef = transRef;
    }
}

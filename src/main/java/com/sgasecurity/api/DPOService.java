package com.sgasecurity.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;

@CrossOrigin
@Service
public class DPOService {
    private static final Logger logger = LoggerFactory.getLogger(DPOService.class);
    private static final String DPO_PAYMENT_URL = "https://secure.3gdirectpay.com/API/v6/createToken";
    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    PackageTypeService packageTypeService;
    PackageType packageType = null;

    public DPOService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DPOPaymentResponse makeMonthlyPayment(DPOPaymentRequest paymentRequest) throws Exception {
        String xmlRequest = generateXmlRequest(paymentRequest);
        String xmlResponse = makeHttpRequest(xmlRequest, DPO_PAYMENT_URL);
        return parseXmlResponse(xmlResponse);
    }

    private String generateXmlRequest(DPOPaymentRequest paymentRequest) {
        double amountInclusive = paymentRequest.getPaymentAmount();

        double convenienceFee = amountInclusive * 0.03;
        BigDecimal convenienceFeeBigDecimal = new BigDecimal(convenienceFee);
        convenienceFee = convenienceFeeBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        String packageName = paymentRequest.getPackageName();

        String[] words = packageName.split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            result.append(capitalizedWord).append(" ");
        }

        String thePackageName = result.toString().trim();

        String serviceDescription = "-Package: " + thePackageName +
                "-Monthly Subscription Fee: KES " + amountInclusive +
                "-Convenience Fee: KES " + convenienceFee;

        serviceDescription = serviceDescription.replace("-", "*****");

        return "<?xml version='1.0' encoding='utf-8'?>"+
                "<API3G>"+
                "<CompanyToken>"+paymentRequest.getCompanyToken()+"</CompanyToken>"+
                "<Request>createToken</Request>"+
                "<Transaction>"+
                "<PaymentAmount>"+amountInclusive+"</PaymentAmount>"+
                "<PaymentCurrency>"+paymentRequest.getPaymentCurrency()+"</PaymentCurrency>"+
                "<CompanyRef>"+paymentRequest.getCompanyRef()+"</CompanyRef>"+
                "<RedirectURL>"+paymentRequest.getRedirectURL()+"</RedirectURL>"+
                "<BackURL>"+paymentRequest.getBackURL()+"</BackURL>"+
                "<CompanyRefUnique>"+paymentRequest.getCompanyRefUnique()+"</CompanyRefUnique>"+
                "<PTL>"+paymentRequest.getPTL()+"</PTL>"+
                "</Transaction>"+
                "<Services>"+
                "<Service>"+
                "<ServiceType>"+paymentRequest.getServiceType()+"</ServiceType>"+
                "<ServiceDescription>"+serviceDescription+"</ServiceDescription>"+
                "<ServiceDate>"+paymentRequest.getInvoicePeriod()+"</ServiceDate>"+
                "</Service>"+
                "</Services>"+
                "</API3G>";
    }

    private String makeHttpRequest(String xmlRequest, String endpointUrl) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xmlRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpointUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Failed to make HTTP request");
        }
        return response.getBody();
    }

    public DPOPaymentResponse parseXmlResponse(String xmlResponse) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlResponse));
        Document doc = builder.parse(is);

        // Retrieve the values from the XML response
        String resultCode = doc.getElementsByTagName("Result").item(0).getTextContent();
        String resultExplanation = doc.getElementsByTagName("ResultExplanation").item(0).getTextContent();
        String transactionRef = doc.getElementsByTagName("TransRef").item(0).getTextContent();
        String transactionToken = doc.getElementsByTagName("TransToken").item(0).getTextContent();

        // Create and return the DPOPaymentResponse object
        DPOPaymentResponse paymentResponse = new DPOPaymentResponse();
        paymentResponse.setResult(resultCode);
        paymentResponse.setResultExplanation(resultExplanation);
        paymentResponse.setTransRef(transactionRef);
        paymentResponse.setTransToken(transactionToken);
        return paymentResponse;
    }
}

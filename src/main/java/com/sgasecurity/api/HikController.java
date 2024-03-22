package com.sgasecurity.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.expression.ParseException;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin
@RestController
public class HikController {
    @Autowired
    InstallationSiteService installationSiteService;
    InstallationSite installationSite = null;
    @Autowired
    CustomerService customerService;
    Customer customer = null;
    Common common = null;
    @Autowired
    TokenService tokenService;
    @Autowired
    HandoverEventService handoverEventService;
    Token token = null;
    HandoverEvent handoverEvent = null;
    @Autowired
    EmergencyService emergencyService;
    Emergency emergency = null;
    @Autowired
    ConfigDataService configDataService;
    ConfigData configData = null;
    String emailAddress = null;
    String emailSubject = null;
    String emailHeader = null;
    String emailFooter = null;
    String emailBody = null;
    String fullEmail = null;
    String smsBody = null;
    String smsApiUrl = null;
    String emailApiUrl = null;
    String bitlyToken = null;
    String bitlyUrl = null;
    DPOPaymentRequest dpoPaymentRequest = null;
    @Autowired
    DPOService dpoService;
    @Autowired
    PackageTypeService packageTypeService = null;
    PackageType packageType = null;
    @Autowired
    HikEventService hikEventService;
    HikEvent hikEvent = null;
    @Autowired
    PaymentLinkService paymentLinkService;
    PaymentLink paymentLink = null;
    LocalDate nextPaymentDate = null;
    String today = null;
    LocalDate todayFormatYYYYMD = null;
    @Autowired
    NextDateService nextDateService;
    @Autowired
    InvoiceService invoiceService;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    AuditTrailService auditTrailService;
    String contextName = "";
    String contextDesc = "";
    String contextValueJsonString = "";
    @Autowired
    IPRPDataService iprpDataService;


    public void captureAuditTrail(String contextName, String contextDesc, String contextValue) {
        try {
            AuditTrail auditTrail = new AuditTrail();
            auditTrail.setContextName(contextName);
            auditTrail.setContextDesc(contextDesc);
            auditTrail.setContextValue(contextValue);
            auditTrailService.saveAuditTrail(auditTrail);
        } catch (Exception e) {
            common.logErrors("api", "InstallationReminderController", "captureAuditTrail", "Capture Audit Trail", e.toString());
        }
    }

    //  List sites to be displayed on the portal
    @CrossOrigin
    @GetMapping("/sites/list/beforehandover")
    public List<InstallationSite> listSitesBeforeHandover(){
        common = new Common();
        try {
            LocalDate today = LocalDate.now();
            List<InstallationSite> sites = installationSiteService.getAllInstallationsWithHandoverStatusNotDone(today);
            return sites;
        } catch (Exception e) {
            common.logErrors("api", "HikController", "listSitesBeforeHandover", "List Sites Before Handover", e.toString());
            return null;
        }
    }

    //  List sites after handover
    @CrossOrigin
    @GetMapping("/sites/list/afterhandover")
    public List<InstallationSite> listSitesAfterHandover(){
        common = new Common();
        try {
            LocalDate today = LocalDate.now();
            List<InstallationSite> sites = installationSiteService.getAllInstallationsWithHandoverStatusDone(today);
            return sites;
        } catch (Exception e) {
            common.logErrors("api", "HikController", "listSitesAfterHandover", "List Sites After Handover", e.toString());
            return null;
        }
    }

//    @CrossOrigin
//    @GetMapping("/process/omni/invoice")
//    public String processOmniInvoice(@RequestParam String customerNo, @RequestParam String tokenId){

    public String processOmniInvoice(String customerNo, String tokenId){
        common = new Common();
        try {
            ConfigData configDataApiAppUrl = configDataService.getConfigDataByConfigName("API_APP_URL");
            String apiAppUrl = configDataApiAppUrl.getConfigValue();
//
//           Push invoice to Omni
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String url = apiAppUrl + "/omni/invoice/push?customerNo=" + customerNo + "&tokenId=" + tokenId;
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getBody().contains("ERROR")) {
                contextName = "PUSH_OMNI_INVOICE_RESPONSE_AT_HANDOVER_FAIL";
                try {
                    contextValueJsonString = objectMapper.writeValueAsString(response);
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            } else {
                contextName = "PUSH_OMNI_INVOICE_RESPONSE_AT_HANDOVER_SUCCESS";
                try {
                    contextValueJsonString = objectMapper.writeValueAsString(response);
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }
            return response.getBody();
        } catch (Exception e){
            common.logErrors("api", "HikController", "sendEvent", "Omni Push Response", e.toString());
            return "ERROR";
        }
    }


//    @CrossOrigin
//    @PostMapping("/createcentraldbinvoicerecord")
//    public long createCentralDBInvoiceRecord(@RequestParam String invoiceRefNo, @RequestParam double amountInclusive, @RequestBody InstallationSite installationSite, @RequestParam String tokenId, @RequestParam LocalDate todayFormatYYYYMD, @RequestParam String narrative){

    public long createCentralDBInvoiceRecord(String invoiceRefNo, double amountInclusive, InstallationSite installationSite, String tokenId, LocalDate todayFormatYYYYMD, String narrative, String invoiceResponse){
        try{
            Invoice invoice = new Invoice();
//          Save invoice data into Invoice table
            invoice.setInvoiceRefNo(invoiceRefNo);
            invoice.setAmount(amountInclusive);
            invoice.setSystemCustomerNo(installationSite.getSystemCustomerNo());
            invoice.setInstallationId(installationSite.getId());
            invoice.setPackageTypeId(installationSite.getPackageTypeId());
            invoice.setInvoicingDate(todayFormatYYYYMD);
            invoice.setTokenId(tokenId);
            invoice.setNarrative(narrative);
            invoice.setIsPaidOut(0); // Not yet paid
            invoice.setIsPrinted("PENDING");
            invoice.setMonthCount(1); // First month
            invoice.setInvoiceResponse(invoiceResponse);
            invoiceService.saveInvoice(invoice);

            contextName = "SAVE_INVOICE_RECORD";
            objectMapper = new ObjectMapper();
            try {
                contextValueJsonString = objectMapper.writeValueAsString(invoice);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            return invoice.getId();
        } catch (Exception e){
            common.logErrors("api", "PaymentController", "omniInvoicePush", "Save Invoice Record For Recurrent Payment", e.toString());

            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
            String emailApiUrl = configDataEmail.getConfigValue();
            ConfigData configData = configDataService.getConfigDataByConfigName("FAILURE_EMAIL");
            String emailAddress = configData.getConfigValue();
            String emailSubject = "DSS - Invoice Record Saving Failure";
            String emailBody = "Sorry. Invoice record not saved. The invoice reference number obtained is "+invoiceRefNo+"\nSee the error:\n "+e.toString();
            sendFailureEmail(emailSubject, emailBody);

            contextName = "FAILED_TO_SAVE_INVOICE_RECORD";
            objectMapper = new ObjectMapper();
            try {
                contextValueJsonString = "Failed to save record for invoice associated with invoice ref no "+invoiceRefNo+"\n\n"+e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return -1;
        }
    }

    public String generateDPOPaymentLink(int monthNo, Customer customer, long newInvoiceId, String omniInvoiceNo, double paymentAmount, String tokenId) {
        DPOPaymentResponse dpoResponse = null;
        String payToken = null;
        try {
            configData = configDataService.getConfigDataByConfigName("DPO_EXPIRY_PERIOD");
            int expiryPeriod = Integer.parseInt(configData.getConfigValue());
            configData = configDataService.getConfigDataByConfigName("DPO_REDIRECT_URL");
            String dpoRedirectUrl = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("DPO_BACK_URL");
            String dpoBackUrl = configData.getConfigValue();


            String timestamp = common.getTheTimestamp();
            String companyRef = "CUSTOMER-" + customer.getSystemCustomerNo() + " MONTH-" + String.valueOf(monthNo) + " INVID-" + newInvoiceId + " OMNIINV" + omniInvoiceNo;
//            String dynamicCompanyRef = "FIRSTMONTH-"+ customerId +"-"+ timestamp;

            double vat = packageType.getVatRate();
            String email = customer.getEmail();
            String firstName = customer.getFirstName();
            String lastName = customer.getLastName();
            String townCity = customer.getTownCity();
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            String phone = customer.getPhone();
            int customerId = customer.getId();
            String customerNo = customer.getSystemCustomerNo();
            String packageTypeName = installationSite.getPackageTypeName();
            String dpoMarkup = "DSS Monthly Payment";

            configData = configDataService.getConfigDataByConfigName("DSS_COUNTRY");
            String dssCountry = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("DPO_CURRENCY");
            String dpoCurrency = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("DPO_COMPANY_TOKEN");
            String dpoCompanyToken = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("DPO_SERVICE_TYPE");
            String dpoServiceType = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("REDIRECT_URL_HANDOVER_AND_RECURRING");
            String pathUrl = configData.getConfigValue();
            pathUrl = pathUrl.replace("#customerno", customerNo);
            pathUrl = pathUrl.replace("#tokenid", tokenId);

            String dpoRedirectUrlFull = dpoRedirectUrl + pathUrl;
            dpoPaymentRequest = common.getDPORequest(firstName, lastName, townCity, fullName, email, phone, packageTypeName, paymentAmount, dpoRedirectUrlFull, dpoBackUrl, vat, dpoMarkup, expiryPeriod, tokenId, customerId, companyRef, dpoCompanyToken, dpoCurrency, dssCountry, dpoServiceType);

            dpoResponse = dpoService.makeMonthlyPayment(dpoPaymentRequest);
            payToken = dpoResponse.getTransToken();

            String paymentURL = "https://secure.3gdirectpay.com/payv3.php?ID=" + payToken;

            configData = configDataService.getConfigDataByConfigName("BITLY_AVAILABILITY");
            String bitlyAvailability = configData.getConfigValue();
            String shortenedPaymentUrl = null;
            if (bitlyAvailability.equals("AVAILABLE")) {
                shortenedPaymentUrl = common.shortenUrl(paymentURL, bitlyToken, bitlyUrl);
            } else {
                shortenedPaymentUrl = paymentURL;
            }
            contextName = "AT_HANDOVER_GENERATED_PAYMENT_LINK";
            try {
                contextValueJsonString = shortenedPaymentUrl;
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return shortenedPaymentUrl;

        } catch (Exception e){
            common.logErrors("api", "HikController", "sendEvent", "Get DPO Request", e.toString());

            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
            String emailApiUrl = configDataEmail.getConfigValue();
            ConfigData configData = configDataService.getConfigDataByConfigName("FAILURE_EMAIL");
            String emailAddress = configData.getConfigValue();
            String emailSubject = "DSS - Failed To Generate Payment Link";
            String emailBody = "Sorry. Payment link generation failed for customer "+customer.getFirstName() +" "+customer.getLastName()+". The invoice reference number obtained is "+omniInvoiceNo+"\nSee the error:\n "+e.toString();
            sendFailureEmail(emailSubject, emailBody);

            contextName = "AT_HANDOVER_GENERATED_PAYMENT_LINK_FAILED";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return "ERROR_NO_PAYMENT_LINK_GENERATED";
        }
    }

    public long savePaymentLink(String customerNo, String tokenId, long invoiceId, String shortenedPaymentUrl, double paymentAmount){
        try {
            PaymentLink paymentLink = new PaymentLink();
            paymentLink.setSystemCustomerNo(customerNo);
            paymentLink.setTokenId(tokenId);
            paymentLink.setLink(shortenedPaymentUrl);
            paymentLink.setIsUsed("NO");
            paymentLink.setAmount(paymentAmount);
            paymentLink.setInvoiceId(invoiceId);
            paymentLink.setMonthCount(1); // First month
            paymentLinkService.savePaymentLink(paymentLink);

            contextName = "SAVE_PAYMENT_LINK_DETAILS";
            try {
                contextValueJsonString = objectMapper.writeValueAsString(paymentLink);
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return paymentLink.getId();
        } catch (Exception e){
            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
            String emailApiUrl = configDataEmail.getConfigValue();
            ConfigData configData = configDataService.getConfigDataByConfigName("FAILURE_EMAIL");
            String emailAddress = configData.getConfigValue();
            String emailSubject = "DSS - Failed To Save Payment Link";
            String emailBody = "Sorry. Failed to save payment link for customer with customer number "+customerNo+". The invoice ID is "+invoiceId+"\nSee the error:\n "+e.toString();
            sendFailureEmail(emailSubject, emailBody);

            common.logErrors("api", "HikController", "sendEvent", "Save Payment Link Record", e.toString());
            return -1;
        }
    }

    //  Send handover eventsendevent
    @CrossOrigin
    @PostMapping("/v1/sendevent")
    public ResponseEntity<String> sendEvent(
            @RequestBody APIRequestData apiRequestData,
            @RequestHeader("Authorization") String bearerToken, @RequestHeader("Content-Type") String contentType) throws JsonProcessingException, ParseException, java.text.ParseException {

        common = new Common();

        String hppsiteid = apiRequestData.getHppsiteid();
        String application = apiRequestData.getApplication();
        Data data = apiRequestData.getData();

        String event = data.getEvent();
        String latitude = data.getLatitude();
        String longitude = data.getLongitude();
        String measuredAt = data.getMeasuredAt();

        LocalDate handoverDate = LocalDate.now();

//        String manuallySetHandoverDate = apiRequestData.getManuallySetHandoverDate();
//
//       if(manuallySetHandoverDate != null || manuallySetHandoverDate != ""){
//            handoverDate = LocalDate.parse(manuallySetHandoverDate);
//        }

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(data);

        HttpHeaders responseHeaders = new HttpHeaders();
//      responseHeaders.set("Content-Type", contentType);
        responseHeaders.set("Content-Type", "application/json");
        responseHeaders.set("Authorization", bearerToken);

        String tokenId = UUID.randomUUID().toString().substring(0, 16);

        ConfigData configDataApiAppUrl = configDataService.getConfigDataByConfigName("API_APP_URL");
        String apiAppUrl = configDataApiAppUrl.getConfigValue();

        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestStr = mapper.writeValueAsString(apiRequestData);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Bearer Token: " + bearerToken +"\n");
            stringBuilder.append("Content Type: " + contentType +"\n");

            JsonNode rootNode = mapper.readTree(requestStr);
            ((ObjectNode) rootNode).remove("accesscode");
            ((ObjectNode) rootNode).remove("customerno");
            requestStr = mapper.writeValueAsString(rootNode);

            stringBuilder.append("Data: " + requestStr +"\n");

            writeEmergencyEventsWithFileWriter(stringBuilder.toString());

        } catch (Exception e) {
            common.logErrors("api", "HikController", "sendEvent", "Send Event:- Object Mapper And Write Emergency Event Into File", e.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(responseHeaders)
                    .body("Cannot write the request data in file: " + e.toString());
        }

//        if(!isValidToken(bearerToken)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .headers(responseHeaders)
//                    .body("Invalid token");
//        }
        if(!isValidApplication(application)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .headers(responseHeaders)
                    .body("Invalid application");
        }
        if(!isValidJson(jsonData)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .headers(responseHeaders)
                    .body("Invalid json data");
        }
        if(!isValidHppSiteId(hppsiteid)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .headers(responseHeaders)
                    .body("Invalid HIK HPP site id");
        }

        try {
            installationSite = installationSiteService.getInstallationSiteByHppSiteId(hppsiteid);
            if(!Objects.isNull(installationSite)) {
                String customerNo = installationSite.getSystemCustomerNo();
                String generatedString = UUID.randomUUID().toString().substring(0, 32);
                long installationId = installationSite.getId();
                int packageTypeId = installationSite.getPackageTypeId();

                String hikFormattedCustomerSiteName = installationSite.getHikFormattedCustomerSiteName();
                String uniqueSiteId = installationSite.getUniqueSiteId();

                try {
                    customer = customerService.getCustomerBySystemCustomerNo(customerNo);
                } catch (Exception e){
                    common.logErrors("api", "HikController", "sendEvent", "Send Event", e.toString());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("No customer data found: " + e.toString());
                }

                packageType = packageTypeService.getPackageType(installationSite.getPackageTypeName());
                double paymentAmount = packageType.getMonthlyCostInclusive();

                try {
                    ConfigData configDataUseTempAmount = configDataService.getConfigDataByConfigName("USE_TEMP_AMOUNT");
                    String useTemAmount = configDataUseTempAmount.getConfigValue();

                    if(useTemAmount.equals("YES")){
                        ConfigData configDataTempAmountValue = configDataService.getConfigDataByConfigName("TEMP_AMOUNT_VALUE");
                        paymentAmount = Double.parseDouble(configDataTempAmountValue.getConfigValue());
                    } else {
                        paymentAmount = packageType.getMonthlyCostInclusive();
                    }
                } catch (Exception e){
                    common.logErrors("api", "HikController", "sendEvent", "Get Payment Amount", e.toString());
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                            .headers(responseHeaders)
//                            .body("ERROR: " + e.toString());
                }

                double vat = packageType.getVatRate();
                String email = customer.getEmail();
                String firstName = customer.getFirstName();
                String lastName = customer.getLastName();
                String townCity = customer.getTownCity();
                String fullName = customer.getFirstName() + " " + customer.getLastName();
                String phone = customer.getPhone();
                int customerId = customer.getId();
                String systemCustomerNo = customer.getSystemCustomerNo();
                String packageTypeName = installationSite.getPackageTypeName();
                double amountInclusive = packageType.getMonthlyCostInclusive();
                String dpoMarkup = "DSS Monthly Payment";

                if(event.equals("handover")) {
                    ResponseEntity<String> iprpResponseJson = null;
                    String deviceSerial = "";

                    String companyName = "";
                    String omniUsername = "";
                    String omniPassword = "";
                    String omniUrl = "";
                    String url = "";
                    String secretKey = "";
                    String appKey = "";
                    String hikToken = "";
                    String urlList = "";

                    contextName = "AT_HANDOVER_CREATE_GENERAL_EMPTY_STRINGS";
                    try {
                        contextValueJsonString = "Created general empty stringgs for handover";
                        System.out.println(contextValueJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    try {
                        ConfigData configData = configDataService.getConfigDataByConfigName("DEVICE_LIST_URL");
                        urlList = configData.getConfigValue();
                        configData = configDataService.getConfigDataByConfigName("HIK_TOKEN_URL");
                        url = configData.getConfigValue();
                        configData = configDataService.getConfigDataByConfigName("HIK_SECRET_KEY");
                        secretKey = configData.getConfigValue();
                        configData = configDataService.getConfigDataByConfigName("HIK_APP_KEY");
                        appKey = configData.getConfigValue();
                        hikToken = common.getHikToken(appKey, secretKey, url);
                    } catch (Exception e){
                        common.logErrors("api", "HikController", "sendEvent", "Fetch HIK Config Details", e.toString());
                        ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
                        String emailApiUrl = configDataEmail.getConfigValue();
                        ConfigData configData = configDataService.getConfigDataByConfigName("FAILURE_EMAIL");
                        String emailAddress = configData.getConfigValue();
                        String emailSubject = "DSS - HIK config details not found";
                        String emailBody = "Sorry. HIK config details are not found for site named "+installationSite.getHikFormattedCustomerSiteName();
                        sendFailureEmail(emailSubject, emailBody);

                        contextName = "AT_HANDOVER_HIK_CONFIG_DETAILS_ARE_MISSING";
                        try {
                            contextValueJsonString = "HIK config details are not found for site named "+installationSite.getHikFormattedCustomerSiteName();
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }

                    try {
                        deviceSerial = hppGetDetails(hppsiteid, urlList, hikToken);
                    } catch (Exception e){
                        common.logErrors("api", "HikController", "sendEvent", "Fetch HIK Config Details", e.toString());
                        ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
                        String emailApiUrl = configDataEmail.getConfigValue();
                        ConfigData configData = configDataService.getConfigDataByConfigName("FAILURE_EMAIL");
                        String emailAddress = configData.getConfigValue();
                        String emailSubject = "DSS - Device serial not found";
                        String emailBody = "Sorry. Device serial could not found for site named "+installationSite.getHikFormattedCustomerSiteName();
                        sendFailureEmail(emailSubject, emailBody);

                        contextName = "AT_HANDOVER_DEVICE_SERIAL_NOT_FOUND";
                        try {
                            contextValueJsonString = "Device serial not found for site named "+installationSite.getHikFormattedCustomerSiteName();
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }
//                    deviceSerial = "Q18224155"; This device serial is for testing IPRP


                    try {
//                      Get Omni credentials
                        configData = configDataService.getConfigDataByConfigName("OMNI_COMPANY_NAME");
                        companyName = configData.getConfigValue();

                        configData = configDataService.getConfigDataByConfigName("OMNI_U_NAME");
                        omniUsername = configData.getConfigValue();

                        configData = configDataService.getConfigDataByConfigName("OMNI_USER_PASSWORD");
                        omniPassword = configData.getConfigValue();

                        configData = configDataService.getConfigDataByConfigName("OMNI_URL");
                        omniUrl = configData.getConfigValue();
                    } catch (Exception e){
//                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                .headers(responseHeaders)
//                                .body("ERROR Omni: " + e.toString());
                    }

                    try{
//                      Create and save handover event
                        handoverEvent = new HandoverEvent();
                        handoverEvent.setEventId(generatedString);
                        handoverEvent.setEvent(event);
                        handoverEvent.setHppSiteId(hppsiteid);
                        handoverEvent.setMeasuredAt(measuredAt);
                        handoverEvent.setSystemCustomerNo(customerNo);
                        handoverEvent.setLatitude(latitude);
                        handoverEvent.setLongitude(longitude);
                        handoverEventService.saveHandoverEvent(handoverEvent);

                        contextName = "MARKED_AS_HANDED_OVER";
                        try {
                            contextValueJsonString = objectMapper.writeValueAsString(handoverEvent);
                            System.out.println(contextValueJsonString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    } catch (Exception e){
                        common.logErrors("api", "HikController", "sendEvent", "Save Handover Event", e.toString());
//                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                .headers(responseHeaders)
//                                .body("ERROR captureAuditTrail: " + e.toString());
                    }

                    //Omni process invoice
                    String omniInvoiceNo = processOmniInvoice(customerNo, tokenId);

                    contextName = "RETURNED_OMNI_INVOICE_NO_AT_HANDOVER";
                    try {
                        contextValueJsonString = "The processOmniInvoice Function Returned "+omniInvoiceNo;
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    String narrative = "Success. Amount and Invoice Ref No. generated from Omni side. (Recurrent Payment)";
                    LocalDate startingDate = LocalDate.now();
                    nextPaymentDate = installationSite.getNextPaymentDate();
                    today = nextDateService.startingDate();
                    todayFormatYYYYMD = nextDateService.startingDateYYYYMD();
                    String invoiceResponse = "{\"error\":\"No invoice response found\"}";
                    try {
                        invoiceResponse = getInvoiceResponse(omniUrl, omniUsername, omniPassword, companyName, omniInvoiceNo);
                    } catch (Exception e) {
                        invoiceResponse = "{\"error\":\"No invoice response found\"}";
                    }

                    long newInvoiceId = createCentralDBInvoiceRecord(omniInvoiceNo, paymentAmount, installationSite, tokenId, todayFormatYYYYMD, narrative, invoiceResponse);

                    contextName = "RETURNED INVOICE ID";
                    try {
                        contextValueJsonString = "The createCentralDBInvoiceRecord Function Returned "+newInvoiceId;
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    String dPOPaymentLink = generateDPOPaymentLink(1, customer,  newInvoiceId,  omniInvoiceNo,  paymentAmount, tokenId);

                    contextName = "RETURNED DPO PAYMENT LINK";
                    try {
                        contextValueJsonString = "The generateDPOPaymentLink Function Returned "+dPOPaymentLink;
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    long paymentLinkId = savePaymentLink(customerNo, tokenId, newInvoiceId, dPOPaymentLink, paymentAmount);

                    contextName = "RETURNED DPO PAYMENT LINK ID";
                    try {
                        contextValueJsonString = "The savePaymentLink Function Returned "+paymentLinkId;
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    try {
                        Invoice invoicePaymentLink = invoiceService.getInvoiceById(newInvoiceId);
                        invoicePaymentLink.setPaymentLinkId(paymentLinkId);
                        invoiceService.saveInvoice(invoicePaymentLink);

                        contextName = "SAVE PAYMENT LINK IN INVOICES TABLE";
                        try {
                            contextValueJsonString = objectMapper.writeValueAsString(invoicePaymentLink);
                            System.out.println(contextValueJsonString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    } catch (Exception e){
                        contextName = "FAILED TO SAVE PAYMENT LINK IN INVOICES TABLE";
                        try {
                            contextValueJsonString = e.toString();
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }

                    try{
///                     Update the installation and customer statuses
                        installationSite.setInstallationStatus("ACTIVE"); // Activate the installation by setting status to ACTIVE
                        installationSite.setHandoverStatus("DONE");
                        installationSite.setHandoverDate(handoverDate);
                        installationSiteService.saveInstallationSite(installationSite);
                        customer.setCustomerStatus("ACTIVE"); // Set customer status to ACTIVE after Handover
                        customerService.saveCustomer(customer);

                        contextName = "UPDATE_CUSTOMER_AND_INSTALLATION_STATUSES";
                        try {
                            contextValueJsonString = objectMapper.writeValueAsString(customer);
                            contextValueJsonString.concat(" --- ");
                            contextValueJsonString.concat(objectMapper.writeValueAsString(installationSite));
                            System.out.println(contextValueJsonString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    } catch (Exception e){
                        common.logErrors("api", "HikController", "sendEvent", " Update Installation And Customer Statuses", e.toString());
//                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                .headers(responseHeaders)
//                                .body("installationSite: " + e.toString());
                        contextName = "FAILED_TO_UPDATE_INSTALLATION_SITE_STATUS";
                        try {
                            contextValueJsonString = e.toString();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }

//                  IPRP
                    try {
                        ConfigData configDataARCList = configDataService.getConfigDataByConfigName("HIK_ARC_DEVICE_LIST");
                        String urlARCList = configDataARCList.getConfigValue();
                        ConfigData configDataHikTokenURL = configDataService.getConfigDataByConfigName("HIK_TOKEN_URL");
                        String hikTokenUrl = configDataHikTokenURL.getConfigValue();
                        ConfigData configDataHikSecretKey = configDataService.getConfigDataByConfigName("HIK_SECRET_KEY");
                        String hikSecretKey = configDataHikSecretKey.getConfigValue();
                        ConfigData configDataHikAppKey = configDataService.getConfigDataByConfigName("HIK_APP_KEY");
                        String hikAppKey = configDataHikAppKey.getConfigValue();
                        ConfigData configDataHikDeviceType = configDataService.getConfigDataByConfigName("HIK_DEVICE_TYPE");
                        String targetDeviceType = configDataHikDeviceType.getConfigValue();
                    } catch (Exception e){
                        common.logErrors("api", "HikController", "sendEvent", "Fetch IPRP Configs", e.toString());

                        contextName = "AT_HANDOVER_IPRP_CONFIG_VALUES_FETCH_FAILED";
                        try {
                            contextValueJsonString = e.toString();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }

                    try {
                        if(deviceSerial.isEmpty()){
                            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
                            String emailApiUrl = configDataEmail.getConfigValue();
                            ConfigData configData = configDataService.getConfigDataByConfigName("FAILURE_EMAIL");
                            String emailAddress = configData.getConfigValue();
                            String emailSubject = "DSS - Device Serial is missing";
                            String emailBody = "Sorry. Device Serial is missing for site named "+installationSite.getHikFormattedCustomerSiteName();
                            sendFailureEmail(emailSubject, emailBody);

//                            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                    .headers(responseHeaders)
//                                    .body("FAILED! It seems the Device Serial is missing. Please attach the device serial to site. Note that Handover process will not be completed without the IPRP operations being completed first.");
                        }

                        iprpResponseJson = iprpDeviceDetails(deviceSerial, systemCustomerNo);

                        contextName = "RETURN_IPRP_RESPONSE";
                        try {
                            contextValueJsonString = objectMapper.writeValueAsString(iprpResponseJson);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    } catch (Exception e){
//                      Send failure email
                        ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
                        String emailApiUrl = configDataEmail.getConfigValue();
                        ConfigData configData = configDataService.getConfigDataByConfigName("FAILURE_EMAIL");
                        String emailAddress = configData.getConfigValue();
                        String emailSubject = "DSS - Failed To Fetch IPRP Details: "+ customer.getSystemCustomerNo();
                        String emailBody = "Sorry. IPRP could not be fetched for " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName()+"\nSee the error:\n "+e.toString();
                        sendFailureEmail(emailSubject, emailBody);

                        contextName = "FAILED_TO_FETCH_IPRP_RESPONSE";
                        try {
                            contextValueJsonString = e.toString();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

//                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                .headers(responseHeaders)
//                                .body("FAILED! It seems the IPRP operations have failed. Please investigate this. Note that the Handover process will not be completed without the IPRP operations being completed first.\n\n"+e.toString());

                    }

//                  Enable devices
                    try {
                        configData = configDataService.getConfigDataByConfigName("DEVICE_ENABLE_AVAILABILITY");
                        String deviceEnableAvailability = configData.getConfigValue();

                        ConfigData configDataHikEnableUrl = configDataService.getConfigDataByConfigName("HIK_URL_ENABLE");
                        String hikDeviceEnableUrl = configDataHikEnableUrl.getConfigValue();

                        boolean hikResponse = false;
                        boolean hikResponseStatus = false;

                        if(deviceEnableAvailability.equals("YES")) {
                            hikResponse = common.device(hppsiteid, true, hikDeviceEnableUrl, hikToken);
                            if(hikResponse){
                                hikResponseStatus = true;
                            }
                        }

                        hikEvent = new HikEvent();
                        hikEvent.setSystemCustomerNo(customerNo);
                        hikEvent.setHppSiteId(hppsiteid);
                        hikEvent.setUniqueSiteId(uniqueSiteId);
                        hikEvent.setEventResponse(hikResponseStatus);
                        hikEvent.setEventType("ENABLE");
                        hikEvent.setHikFormattedCustomerSiteName(hikFormattedCustomerSiteName);

                        if (iprpResponseJson == null) {
                            hikEvent.setIprpResponse("Null Response");
                        } else {
                            hikEvent.setIprpResponse(iprpResponseJson.toString());
                        }

                        hikEvent.setEventResponse(hikResponse);
                        hikEvent.setEventType("ENABLE");
                        hikEventService.saveHikEvent(hikEvent);

                        contextName = "SAVE_HIK_EVENT_DETAILS_FOR_DEVICE_ENABLE";
                        try {
                            contextValueJsonString = objectMapper.writeValueAsString(hikEvent);
                            System.out.println(contextValueJsonString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    } catch (Exception e){
                        common.logErrors("api", "HikController", "sendEvent", "Enable Devices", e.toString());

                        contextName = "FAILED_TO_SAVE_HIK_EVENT_DETAILS_FOR_DEVICE_ENABLE";
                        try {
                            contextValueJsonString = e.toString();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

//                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                .headers(responseHeaders)
//                                .body("ERROR hikEvent: " + e.toString());

                    }

////                    Obtain ARC device serial - See top
//                    try {
//
//
//                        contextName = "AT_HANDOVER_SAVE_DEVICE_SERIAL";
//                        try {
//                            contextValueJsonString = deviceSerial;
//                            System.out.println(contextValueJsonString);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//                    } catch (Exception e){
//                        common.logErrors("api", "HikController", "sendEvent", "Obtain ARC Device Serial", e.toString());
//                        String emailSubject = "DSS - Failed To Obtain ARC Device Serial";
//                        String emailBody = "Failed to obtain ARC device serial:\nSee the error:\n" + e.toString();
//                        sendFailureEmail(emailSubject, emailBody);
//                    }

                    try {
                        if(!Objects.isNull(installationSite)){
                            installationSite.setDeviceSerial(deviceSerial);
                            installationSite.setIprpResponse(iprpResponseJson.toString());
                            installationSiteService.saveInstallationSite(installationSite);
                        } else {
                            InstallationSite installationSite1 = installationSiteService.getInstallationSiteByHppSiteId(hppsiteid);
                            installationSite1.setDeviceSerial(deviceSerial);
                            installationSite.setIprpResponse(iprpResponseJson.toString());
                            installationSiteService.saveInstallationSite(installationSite1);
                        }

                    } catch (Exception e){
                        common.logErrors("api", "HikController", "sendEvent", "Save Device Serial in InstallationSite", e.toString());

                        contextName = "FAILED_TO_SAVE_INSTALLATION_DEVICE_SERIAL";
                        try {
                            contextValueJsonString = e.toString();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                        String emailSubject = "DSS - Failed To Update Installation Record";
                        String emailBody = "Failed to update installation:\nSee the error:\n" + e.toString();
                        sendFailureEmail(emailSubject, emailBody);
                    }

                    try {
                        ConfigData configDataSms = configDataService.getConfigDataByConfigName("SMS_API_URL");
                        smsApiUrl = configDataSms.getConfigValue();

                        ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
                        emailApiUrl = configDataEmail.getConfigValue();

                        ConfigData configDataBitlyToken = configDataService.getConfigDataByConfigName("BITLY_TOKEN");
                        bitlyToken = configDataBitlyToken.getConfigValue();

                        ConfigData configDataBitlyUrl = configDataService.getConfigDataByConfigName("BITLY_URL");
                        bitlyUrl = configDataBitlyUrl.getConfigValue();
                    } catch (Exception e){
                        common.logErrors("api", "HikController", "sendEvent", "Get Email Configs", e.toString());
//                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                .headers(responseHeaders)
//                                .body("bitlyUrl: " + e.toString());
                    }

                    if(!dPOPaymentLink.contains("ERROR")){
                        try {
//                          1. Send registration email (Thank You) to customer for registering with SGA
                            emailAddress = customer.getEmail();
                            configData = configDataService.getConfigDataByConfigName("AFTER_HANDOVER_EMAIL_SUBJECT");
                            emailSubject = configData.getConfigValue();
                            configData = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
                            emailHeader = configData.getConfigValue();
                            configData = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
                            emailFooter = configData.getConfigValue();
                            configData = configDataService.getConfigDataByConfigName("AFTER_HANDOVER_EMAIL_BODY");
                            emailBody = configData.getConfigValue();
                            emailBody = emailBody.replace("#customername", customer.getFirstName()+" "+customer.getLastName());
                            emailBody = emailBody.replace("#here", dPOPaymentLink);
                            fullEmail = emailHeader + emailBody + emailFooter;
                        } catch (Exception e){
                            common.logErrors("api", "HikController", "sendEvent", "Get Customer Emails Configs", e.toString());

                            String emailSubject = "DSS - Failed To Send Registration Email To Customer";
                            String emailBody = "Failed to send registration email to customer:\nSee the error: " + e.toString();
                            sendFailureEmail(emailSubject, emailBody);

                            contextName = "FAILED_TO_SEND_REGISTRATION_EMAIL_TO_CUSTOMER";
                            try {
                                contextValueJsonString = e.toString();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

//                          return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                             .headers(responseHeaders)
//                             .body("Registration: " + e.toString());

                        }

                        try {
                            common.sendEmail(emailAddress, emailSubject, fullEmail, "1", "", emailApiUrl, "1");
                            contextName = "AFTER_HANDOVER_EMAIL";
                            try {
                                contextValueJsonString = fullEmail;
                                System.out.println(contextValueJsonString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                        } catch (Exception e){
                            common.logErrors("api", "HikController", "sendEvent", "Send Registration Email To Customer", e.toString());

                            String emailSubject = "DSS - Failed To Send After Handover Email";
                            String emailBody = "Failed to send after handover email:\nSee the error: " + e.toString();
                            sendFailureEmail(emailSubject, emailBody);

                            contextName = "FAILED_TO_SEND_AFTER_HANDOVER_EMAIL";
                            try {
                                contextValueJsonString = e.toString();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//                            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                    .headers(responseHeaders)
//                                    .body("Email: " + e.toString());
                        }

//                      Send SMS
                        configData = configDataService.getConfigDataByConfigName("AFTER_HANDOVER_SMS_BODY");
                        smsBody = configData.getConfigValue();
                        smsBody = smsBody.replace("#customername", customer.getFirstName()+" "+customer.getLastName());
                        smsBody = smsBody.replace("#here", dPOPaymentLink);

                        try {
                            common.sendSMS(customer.getPhone(), smsBody, smsApiUrl);
                            contextName = "AFTER_HANDOVER_SMS";
                            try {
                                contextValueJsonString = smsBody;
                                System.out.println(contextValueJsonString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                        } catch (Exception e){
                            common.logErrors("api", "HikController", "sendEvent", "Send Registration SMS To Customer", e.toString());

                            String emailSubject = "DSS - Failed To Send After Handover SMS";
                            String emailBody = "Failed to send after handover SMS:\nSee the error: " + e.toString();
                            sendFailureEmail(emailSubject, emailBody);

                            contextName = "FAILED_TO_SEND_AFTER_HANDOVER_SMS";
                            try {
                                contextValueJsonString = e.toString();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//                            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                    .headers(responseHeaders)
//                                    .body("SMS: " + e.toString());
                        }
                    } else {
                        common.logErrors("api", "HikController", "sendevent", "Send Payment Link Error - Email", "Payment Link Error");
//                      Send failure email
                        configData = configDataService.getConfigDataByConfigName("FAILED_EMAIL");
                        emailAddress = configData.getConfigValue();
                        String emailSubject = "DSS - Payment Link Error";
                        String emailBody = "Sorry. We were not able to send payment link via email for " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName();
                        sendFailureEmail(emailSubject, emailBody);
                    }

//                  Return handover response
                    EventResponse eventResponse = new EventResponse();
                    eventResponse.setEventid(generatedString);
                    eventResponse.setStatus("SUCCESS");
                    eventResponse.setError("0");

                    contextName = "HANDOVER_EVENT_RESPONSE";
                    try {
                        contextValueJsonString = objectMapper.writeValueAsString(eventResponse);
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    try {
                        ObjectMapper objectMapperEvent = new ObjectMapper();
                        String json = objectMapperEvent.writeValueAsString(eventResponse);
                        return ResponseEntity.ok()
                                .headers(responseHeaders)
                                .body(json);
                    } catch (Exception e) {
                        common.logErrors("api", "HikController", "sendEvent", "Convert Handover Response To JSON", e.toString());

                        String emailSubject = "DSS - Failed To Return Handover Event Response";
                        String emailBody = "Failed to return handover event response:\nSee the error:\n" + e.toString();
                        sendFailureEmail(emailSubject, emailBody);

                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .headers(responseHeaders)
                                .body("FAILED! Could not return handover event response. Please contact the Admin and check database entries");
                    }

//                  } else {
//                        common.logErrors("api", "HikController", "sendEvent", "Push Invoice To Omni", "Could not push invoice to omni (if else block)");
//                        return ResponseEntity.status(HttpStatus.CONFLICT)
//                                .headers(responseHeaders)
//                                .body("Error occurred. Some data not be saved");
//                  }
                } else if(event.equals("reportemergency")) {

//                  Create and save handover event
                    if(Objects.isNull(emergencyService.getEmergencyBySystemCustomerNo(customerNo))){
                        try {
                            emergency = new Emergency();
                            emergency.setEventId(generatedString);
                            emergency.setHppSiteId(hppsiteid);
                            emergency.setSystemCustomerNo(customerNo);
                            emergency.setLatitude(latitude);
                            emergency.setLongitude(longitude);
                            emergency.setMeasuredAt(measuredAt);
                         //   emergencyService.saveEmergency(emergency);


                            contextName = "CREATE_AND_SAVE_EMERGENCY_EVENT";
                            try {
                                contextValueJsonString = objectMapper.writeValueAsString(String.valueOf(emergency));
                                System.out.println(contextValueJsonString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                        } catch (Exception e){
                            common.logErrors("api", "HikController", "sendEvent", "Save New Emergency Record", e.toString());
                        }
                    }
                    else{

                        try {
                            emergency = emergencyService.getEmergencyBySystemCustomerNo(customerNo);
                            generatedString = emergency.getEventId();
                            contextName = "GET_EXISIING_EMERGENCY_OBJECT";
                            try {
                                contextValueJsonString = objectMapper.writeValueAsString(emergency);
                                System.out.println(contextValueJsonString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                        }
                        catch (Exception ex)
                        {
                            emergency = emergencyService.getEmergencyBySystemCustomerNo(customerNo);
                            contextName = "ERROR_GET_EXISIING_EMERGENCY_OBJECT";
                            try {
                                contextValueJsonString =ex.toString();
                                System.out.println(contextValueJsonString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                        }
                    }


//                  Get SOS request statuses
                    RestTemplate restTemplateStatus = new RestTemplate();
                    HttpHeaders headersStatus = new HttpHeaders();
                    headersStatus.setContentType(MediaType.APPLICATION_JSON);
                    String urlStatus = apiAppUrl+"/sos/process/request/status?eventid=" + generatedString;
                    HttpEntity<String> entityStatus = new HttpEntity<>(headersStatus);

                    ResponseEntity<Map<String, String>> responseStatuses = restTemplateStatus.exchange(
                            urlStatus,
                            HttpMethod.GET,
                            entityStatus,
                            new ParameterizedTypeReference<Map<String, String>>() {});

                    Map<String, String> mapStatuses = responseStatuses.getBody();

                    EventResponse eventResponse = new EventResponse();
                    eventResponse.setEventid(generatedString);

                    // Capture the emergency response map status
                    contextName = "EMERGENCY_RESPONSE_MAP";
                    try {
                        contextValueJsonString = objectMapper.writeValueAsString(String.valueOf(mapStatuses));
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    if(mapStatuses != null) {


                        if (mapStatuses.get("status").equals("NOT_ONGOING")) {

                            emergency.setRescueCallStatus("NOT_ONGOING");
                            emergencyService.saveEmergency(emergency);

                            contextName = "EMERGENCY_RESPONSE_MAP_STATUS";
                            try {
                                contextValueJsonString = objectMapper.writeValueAsString(String.valueOf(mapStatuses.get("status")));
                                System.out.println(contextValueJsonString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);


//                          Create SOS request
                            RestTemplate restTemplate = new RestTemplate();
                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            String url = apiAppUrl+"/sos/request/send?eventid=" + generatedString;
                            HttpEntity<String> entity = new HttpEntity<>(headers);
                            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

                            if ((response.getBody()).equals("SUCCESS")) {
//                              Return emergency response
                                eventResponse.setStatus("SUCCESS");
                                eventResponse.setError("0");
                                try {
                                    ObjectMapper objectMapperEvent = new ObjectMapper();
                                    String json = objectMapperEvent.writeValueAsString(eventResponse);
                                    contextName = "SUCCESS_CREATE_REPORT_EMERGENCY_SUCCESS_EVENT_RESPONSE";
                                    try {
                                        contextValueJsonString = objectMapper.writeValueAsString(eventResponse);
                                        System.out.println(contextValueJsonString);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                                    return ResponseEntity.ok()
                                            .headers(responseHeaders)
                                            .body(json);
                                } catch (Exception e) {
                                    common.logErrors("api", "HikController", "sendEvent", "Convert ReportEmergency Event Response To Json - For Success Status", e.toString());

                                    contextName = "ERROR_SUCCESS_CREATE_REPORT_EMERGENCY_SUCCESS_EVENT_RESPONSE";
                                    try {
                                        contextValueJsonString = e.toString();
                                        System.out.println(contextValueJsonString);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                            .headers(responseHeaders)
                                            .body("Error occurred. Failed to return report emergency event response. Please contact the Admin: \n\nError: " + e.toString());
                                }
                            } else {
//                              Return emergency response
                                eventResponse.setStatus("FAILED");
                                eventResponse.setError("1");
                                try {
                                    ObjectMapper objectMapperEvent = new ObjectMapper();
                                    String json = objectMapperEvent.writeValueAsString(eventResponse);

                                    contextName = "FAILED_CREATE_EMERGENCY_REQUEST";
                                    try {
                                        contextValueJsonString = objectMapper.writeValueAsString(eventResponse);
                                        System.out.println(contextValueJsonString);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                                    return ResponseEntity.ok()
                                            .headers(responseHeaders)
                                            .body(json);
                                } catch (Exception e) {
                                    common.logErrors("api", "HikController", "sendEvent", "Convert ReportEmergency Event Response To Json - Fail", e.toString());

                                    contextName = "ERROR_AT_FAILED_CREATE_EMERGENCY_REQUEST_FAILED_ONE";
                                    try {
                                        contextValueJsonString = e.toString();
                                        System.out.println(contextValueJsonString);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                            .headers(responseHeaders)
                                            .body("Error occurred. Failed to return report emergency event response - though the failed one. Please contact the Admin: \n\nError: " + e.toString());
                                }
                            }
                        } else if(mapStatuses.get("status").equals("ONGOING")){
                            try{
                                emergency = emergencyService.getEventByEventId(generatedString);
                                emergency.setRescueCallStatus("ONGOING");
                                emergencyService.saveEmergency(emergency);

                                contextName = "SAVED_ONGOING_REPORT_EMERGENCY_EVENT_RECORD";
                                try {
                                    contextValueJsonString = objectMapper.writeValueAsString(emergency);
                                    System.out.println(contextValueJsonString);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                            } catch (Exception e){
                                common.logErrors("api", "HikController", "sendEvent", "Update Emergency Call Status", e.toString());

                                contextName = "ERROR_AT_SAVED_ONGOING_REPORT_EMERGENCY_EVENT_RECORD";
                                try {
                                    contextValueJsonString = e.toString();
                                    System.out.println(contextValueJsonString);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                            }

//                          Return emergency response
                            eventResponse.setStatus("ONGOING");
                            eventResponse.setError("0");
                            try {
                                ObjectMapper objectMapperEvent = new ObjectMapper();
                                String json = objectMapperEvent.writeValueAsString(eventResponse);

                                contextName = "RETURNED_EMERGENCY_STATUS_WITH_ONGOING_STATUS_AS_FRESH_ONGOING";
                                try {
                                    contextValueJsonString = objectMapper.writeValueAsString(eventResponse);
                                    System.out.println(contextValueJsonString);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                                return ResponseEntity.ok()
                                        .headers(responseHeaders)
                                        .body(json);
                            } catch (Exception e) {
                                common.logErrors("api", "HikController", "sendEvent", "Convert ReportEmergency Event Response To Json - For Ongoing Status", e.toString());

                                contextName = "ERROR_AT_RETURNED_EMERGENCY_STATUS_WITH_ONGOING_STATUS_AS_FRESH_ONGOING";
                                try {
                                    contextValueJsonString = e.toString();
                                    System.out.println(contextValueJsonString);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .headers(responseHeaders)
                                        .body("Error occurred. Failed to return report emergency event response - the one with ongoing status - though the failed one. Please contact the Admin: \n\nError: " + e.toString());
                            }
                        } else {
//                          Return emergency response
                            eventResponse.setStatus("FAILED");
                            eventResponse.setError("1");
                            try {
                                ObjectMapper objectMapperEvent = new ObjectMapper();
                                String json = objectMapperEvent.writeValueAsString(eventResponse);

                                contextName = "FAIL_EMERGENCY_NOT_ONGOING";
                                try {
                                    contextValueJsonString = objectMapper.writeValueAsString(eventResponse);
                                    System.out.println(contextValueJsonString);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                                return ResponseEntity.ok()
                                        .headers(responseHeaders)
                                        .body(json);
                            } catch (Exception e) {
                                common.logErrors("api", "HikController", "sendEvent", "Convert ReportEmergency Event Response To Json - For Failed Status", e.toString());

                                contextName = "ERROR_AT_FAIL_EMERGENCY_NOT_ONGOING";
                                try {
                                    contextValueJsonString = e.toString();
                                    System.out.println(contextValueJsonString);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .headers(responseHeaders)
                                        .body("Error occurred. Failed to return report emergency event response - though the failed one. Please contact the Admin: \n\nError: " + e.toString());
                            }
                        }
                    } else {
//                      Return emergency response
                        eventResponse.setStatus("FAILED");
                        eventResponse.setError("1");
                        try {
                            ObjectMapper objectMapperEvent = new ObjectMapper();
                            String json = objectMapperEvent.writeValueAsString(eventResponse);

                            contextName = "FAILED_EMERGENCY_MAP_STATUS_NULL";
                            try {
                                contextValueJsonString = objectMapper.writeValueAsString(eventResponse);
                                System.out.println(contextValueJsonString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                            return ResponseEntity.ok()
                                    .headers(responseHeaders)
                                    .body(json);
                        } catch (Exception e) {
                            common.logErrors("api", "HikController", "sendEvent", "Convert ReportEmergency Event Response To Json - For Failed Status", e.toString());

                            contextName = "ERROR_AT_FAILED_EMERGENCY_MAP_STATUS_NULL";
                            try {
                                contextValueJsonString = e.toString();
                                System.out.println(contextValueJsonString);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .headers(responseHeaders)
                                    .body("Error occurred. Failed to return report emergency event response - though the failed one. Please contact the Admin: \n\nError: " + e.toString());
                        }
                    }
                } else {
                    common.logErrors("api", "HikController", "sendEvent", "Save Emergency Data", "Could not save Emergency data (if else block)");

                    contextName = "NO_EVENT_FOUND";
                    try {
                        contextValueJsonString = "No event found";
                        System.out.println(contextValueJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                            .headers(responseHeaders)
                            .body("No event found");
                }
            } else {
                common.logErrors("api", "HikController", "sendEvent", "Check Whether The InstallationSite Object Is Not Null", "The InstallationSite object is null (if else block)");

                contextName = "NULL_INSTALLATION_RECORD";
                try {
                    contextValueJsonString = "Null installation record";
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(responseHeaders)
                        .body("Error occurred. Installation site does not exist");
            }
        } catch (Exception e){
            common.logErrors("api", "HikController", "sendEvent", "Processes After InstallationSite Is Found Not Null", e.toString());

            contextName = "FAILED_EVENT_EXECUTION";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(responseHeaders)
                    .body("Failed. Error occurred in event execution: " + e.toString());
        }
    }

    public String getInvoiceResponse(String domain, String userName, String password, String companyName, String invoiceNumber) {
        try {

            String url = domain + "/Invoice/" + invoiceNumber + "?UserName=" + userName + "&Password=" + password + "&CompanyName=" + companyName;
            url = url.replace(" ", "%20");

            RestTemplate restTemplate = new RestTemplate();

            URI uri = URI.create(url);
            String protocol = uri.getScheme();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            String result = response.getBody();

            return result;

        } catch (Exception e) {
            common.logErrors("api", "HikController", "getInvoiceResponse", "Get Invoice Response", e.toString());
            return null;
        }
    }

    @CrossOrigin
    @GetMapping("/gethiktoken")
    public String getTheHikToken() {
        Common common1 = new Common();
        try {
            configData = configDataService.getConfigDataByConfigName("HIK_TOKEN_URL");
            String url = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("HIK_SECRET_KEY");
            String secretKey = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("HIK_APP_KEY");
            String appKey = configData.getConfigValue();

            String token = common1.getHikToken(appKey, secretKey, url);
            return token;

        } catch (Exception e) {
            common1.logErrors("api", "Common", "device", "Enable Or Disable The Site", e.toString());
            return e.toString();
        }
    }

    //  Generate token
    @CrossOrigin
    @RequestMapping("/v1/authorize")
    @ResponseBody
    public ResponseEntity<String> authorize(@RequestBody APIRequestData apiRequestData,
                                            @RequestHeader("Content-Type") String contentType) {

        String accesscode = apiRequestData.getAccesscode();
        String application = apiRequestData.getApplication();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        common = new Common();

        if(!isValidContentType(contentType)){
            common.logErrors("api", "HikController", "authorize", "Check Content Type Validity", "Invalid content type (if else block)");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .headers(responseHeaders)
                    .body("Invalid content type");
        }

        if(!isValidAccessCode(accesscode)){
            common.logErrors("api", "HikController", "authorize", "Check Access Code Validity", "Invalid access code (if else block)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .headers(responseHeaders)
                    .body("Invalid access code");
        }

        if(!isValidApplication(application)){
            common.logErrors("api", "HikController", "authorize", "Check Application Validity", "Invalid application (if else block)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .headers(responseHeaders)
                    .body("Invalid application");
        }

        try {

            TokenResponse tokenResponse = generateAndSaveToken();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(tokenResponse);
                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body(json);
            } catch (Exception e) {
                common.logErrors("api", "HikController", "authorize", "Convert TokenResponse To Json", e.toString());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(responseHeaders)
                        .body("Not found. Error occurred: " + e.toString());
            }

        } catch (Exception e){
            common.logErrors("api", "HikController", "authorize", "Token Processes", e.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(responseHeaders)
                    .body("Not found. Error occurred: " + e.toString());
        }
    }

    private static final int TOKEN_LENGTH = 64;

    public static String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        String theToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        return theToken;
    }

    private boolean isValidToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }
        String theToken = authorizationHeader.substring(7); // Extract token from header
        theToken = theToken.replace('[', ' ').replace(']', ' ');
        theToken = theToken.trim();

        List<Token> tokenList = tokenService.getAllTokens();

        if(tokenList.size() > 0) {
            token = tokenService.getByToken(theToken);
            if (!Objects.isNull(token)) {
                return true;
            } else {
                return false;
            }
        } else {
            generateAndSaveToken();
            return true;
        }
    }

    private boolean isValidContentType(String contentTypeHeader) {
        if (contentTypeHeader == null || !contentTypeHeader.equals("application/json")) {
            return false;
        }
        return true;
    }

    private boolean isValidAccessCode(String accessCode) {
        configData = configDataService.getConfigDataByConfigName("HIK_ACCESS_CODE");
        String theAccessCode = configData.getConfigValue();
        if (accessCode == null || !accessCode.equals(theAccessCode)) {
            return false;
        }
        return true;
    }

    private boolean isValidApplication(String application) {
        configData = configDataService.getConfigDataByConfigName("HIK_APPLICATION_NAME");
        String theApplication = configData.getConfigValue();
        if (application == null || !application.equals(theApplication)) {
            return false;
        }
        return true;
    }

    public boolean isValidHppSiteId(String hppsiteid) {
        if (StringUtils.isEmpty(hppsiteid)) {
            return false;
        }
        if(Objects.isNull(installationSiteService.getInstallationSiteByHppSiteId(hppsiteid))){
            return false;
        }
        return true;
    }

    public static boolean isValidJson(String jsonString) {
        if (StringUtils.isEmpty(jsonString)) {
            return false;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public TokenResponse generateAndSaveToken() {
        String theToken = generateToken();
        LocalDate currentDate = LocalDate.now();
        LocalDate localDate = currentDate.plusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String expires = localDate.format(formatter);
        token = new Token();
        token.setToken(theToken);
        token.setExpires(expires);
        tokenService.saveToken(token);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(theToken);
        tokenResponse.setExpires(expires);
        tokenResponse.setError("0");

        return tokenResponse;
    }

    public void writeEmergencyEventsWithFileWriter(String requestData) {
        String filePath = "/opt/dssfiles/emergencyrequestfromhik.txt";
        try {
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(requestData);
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            common.logErrors("api", "HikController", "writeEmergencyEventsWithFileWriter", "Write Emergency Event Into A File", e.toString());
            e.printStackTrace();
        }
    }

    @CrossOrigin
    @GetMapping(value = "/file/content", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getFileContent() throws IOException {
        String filePath = "/opt/dssfiles/emergencyrequestfromhik.txt";
        Path file = Paths.get(filePath);

        if (Files.exists(file)) {
            byte[] fileBytes = Files.readAllBytes(file);
            return new String(fileBytes, StandardCharsets.UTF_8);
        } else {
            common.logErrors("api", "HikController", "getFileContent", "Read Emergency Event From A File", "Could not find file (if else block)");
            throw new RuntimeException("File not found: " + filePath);
        }
    }

    // IPRP

    //    @CrossOrigin
//    @PostMapping("/devicemgmt/devicelist")
//    @ResponseBody
    public ResponseEntity<String> iprpDeviceDetails(String key, String customerNo) {

        Map<String, Object> map = new HashMap<>();

        HttpHeaders responseHeaders = new HttpHeaders();

        ObjectMapper objectMapperEvent = new ObjectMapper();

        String apiUrl = null;
        String hikIprpUsername = null;
        String hikIprpPawd = null;
        int iprpMaxResult = 2000;

        try {
            ConfigData configDataUrl = configDataService.getConfigDataByConfigName("HIK_IPRP_DEVICE_URL_PUBLIC");
            apiUrl = configDataUrl.getConfigValue();
            ConfigData configDataHikIprpUsername = configDataService.getConfigDataByConfigName("HIK_IPRP_USERNAME");
            hikIprpUsername = configDataHikIprpUsername.getConfigValue();
            ConfigData configDataHikIprpPwd = configDataService.getConfigDataByConfigName("HIK_IPRP_PASSWORD");
            hikIprpPawd = configDataHikIprpPwd.getConfigValue();

            ConfigData configDataIprpMaxResult = configDataService.getConfigDataByConfigName("IPRP_DEVICE_MAX_RESULT");
            iprpMaxResult = Integer.parseInt(configDataIprpMaxResult.getConfigValue());

            contextName = "FETCH_IPRP_CONFIGS";
            try {
                contextValueJsonString = apiUrl.concat(" -- "+iprpMaxResult);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            map.put("status", "FAIL");
            map.put("error", "1");
            map.put("errorMessage", e.toString());

            contextName = "ERROR_FETCHING_IPRP_CONFIG_DETAILS";
            try {
                contextValueJsonString = "Device Serial "+key+" -- Customer No. "+customerNo+" -- Error "+e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(responseHeaders)
                    .body(e.toString());
        }

        String requestBody = "{\n" +
                "  \"SearchDescription\": {\n" +
                "    \"position\": 0,\n" +
                "    \"maxResult\": "+iprpMaxResult+",\n" +
                "    \"Filter\": {\n" +
                "      \"key\": \""+key+"\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // Create a CredentialsProvider for Digest Authentication
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials =
                new UsernamePasswordCredentials(hikIprpUsername, hikIprpPawd);
        provider.setCredentials(AuthScope.ANY, credentials);

        // Create a custom HttpClient with Digest Authentication
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(provider)
                .build();

        // Create an HttpPost request
        HttpPost request = new HttpPost(apiUrl);

        // Set the request body (if any)
        if (requestBody != null && !requestBody.isEmpty()) {
            request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
        }

        try {
            // Execute the request and get the response
            CloseableHttpResponse response = httpClient.execute(request);

            // Read the response content
            String responseBody = EntityUtils.toString(response.getEntity());

            // Close the response
            response.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(responseBody);
//        JsonNode matchList = jsonResponse.path("SearchResult").path("MatchList").get(0);
//        String siteID = matchList.path("Device").path("siteInfo").path("siteID").asText();
//            map.put("status", "SUCCESS");
//            map.put("error", "0");
//            map.put("errorMessage", "");
//            map.put("testData", siteID);
//            return map;

            JsonNode matchList = jsonResponse.path("SearchResult").path("MatchList").get(0);
            String siteID = matchList.path("Device").path("siteInfo").path("siteID").asText();
            String siteName = matchList.path("Device").path("siteInfo").path("siteName").asText();
            String devName = matchList.path("Device").path("devName").asText();
            String accountID = matchList.path("Device").path("accountID").asText();
            String activeStatus = matchList.path("Device").path("activeStatus").asText();
            String devIndex = matchList.path("Device").path("devIndex").asText();
            String devSerial = matchList.path("Device").path("devSerial").asText();
            String deviceID = matchList.path("Device").path("ISUP").path("deviceID").asText();
            String deviceKey = matchList.path("Device").path("ISUP").path("deviceKey").asText();

//          Function to store details in the database table if they do not exist
            Map<String, String> mapResponse = saveIPRPDetails(siteID, siteName, devName, accountID, activeStatus, devIndex, devSerial, deviceID, deviceKey);

            if(mapResponse == null){
                map.put("status", "FAIL");
                map.put("error", "1");
                map.put("errorMessage", "Null object");

                contextName = "FAILED_TO_SAVE_IPRP_DETAILS_AND_NULL_RESPONSE_RETURNED";
                try {
                    contextValueJsonString = "Failed to save HIK details and returned null map response. Value: "+mapResponse;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(responseHeaders)
                        .body("Null object");
            }

            if(mapResponse.get("status").equals("SUCCESS")){
                map.put("status", "SUCCESS");
                map.put("data", jsonResponse);
                map.put("error", "0");
                map.put("errorMessage", "");

                contextName = "SAVED_IPRP_DETAILS_SUCCESSFULLY";
                try {
                    contextValueJsonString = objectMapper.writeValueAsString(mapResponse)+"\n"+objectMapper.writeValueAsString(jsonResponse);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                String json = objectMapperEvent.writeValueAsString(map);
                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body(json);

            } else {
                if(jsonResponse != null){
                    map.put("status", "FAIL");
                    map.put("error", "1");
                    map.put("errorMessage", mapResponse.get("errorMessage"));

                    contextName = "FAILED_TO_SAVE_IPRP_DETAILS";
                    try {
                        contextValueJsonString = mapResponse.get("errorMessage");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                } else {
                    map.put("status", "FAIL");
                    map.put("error", "1");
                    map.put("errorMessage", mapResponse.get("errorMessage"));

                    contextName = "FAILED_TO_SAVE_IPRP_DETAILS";
                    try {
                        contextValueJsonString = mapResponse.get("errorMessage");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(responseHeaders)
                        .body(mapResponse.get("errorMessage"));
            }
        } catch (Exception e) {
            map.put("status", "FAIL");
            map.put("error", "1");
            map.put("errorMessage", e.toString());

            contextName = "FAILED_TO_SAVE_IPRP_DETAILS_AND_CAUGHT_ERROR";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(responseHeaders)
                    .body(e.toString());
        }
    }

    @CrossOrigin
    @PutMapping("/devicemgmt/update/status")
    @ResponseBody
    public JsonNode updateIPRPStatus(@RequestBody IPRP iprp){

        String deviceIndex = iprp.getDeviceIndex();
        String deviceName = iprp.getDeviceName();
        String accountID = iprp.getAccountID();
        String status = iprp.getStatus();
        String username = iprp.getUsername();
        String pwd = iprp.getPwd();

        ConfigData configDataUrl = configDataService.getConfigDataByConfigName("HIK_IPRP_STATUS_UPDATE_URL_PUBLIC");
        String url = configDataUrl.getConfigValue();
        String apiUrl = url.replace("#deviceindex", deviceIndex);

        String requestBody = "{\n" +
                "  \"accountID\": \""+accountID+"\",\n" +
                "  \"deviceName\": \""+deviceName+"\",\n" +
                "  \"activeStatus\": \""+status+"\",\n" +
                "  \"streamKey\": \"\",\n" +
                "  \"remark\": \"\"\n" +
                "}";

        // Create a CredentialsProvider for Digest Authentication
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials =
                new UsernamePasswordCredentials(username, pwd);
        provider.setCredentials(AuthScope.ANY, credentials);


        // Create a custom HttpClient with Digest Authentication
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(provider)
                .build();

        // Create an HttpPost request
        HttpPut request = new HttpPut(apiUrl);

        // Set the request body (if any)
        if (requestBody != null && !requestBody.isEmpty()) {
            request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
        }

        try {
            // Execute the request and get the response
            CloseableHttpResponse response = httpClient.execute(request);

            // Read the response content
            String responseBody = EntityUtils.toString(response.getEntity());

            // Close the response
            response.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(responseBody);

            return jsonResponse;

        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, String> saveIPRPDetails(String siteId, String siteName, String devName, String accountId, String activeStatus, String devIndex, String devSerial, String deviceId, String deviceKey){
        Map<String, String> map = new HashMap<>();
        common = new Common();
        try {
            if(!Objects.isNull(installationSiteService.getInstallationSiteByHppSiteId(siteId))){
                InstallationSite installationSite1 = installationSiteService.getInstallationSiteByHppSiteId(siteId);
                int customerId = installationSite1.getCustomerId();
                String customerNo = installationSite1.getSystemCustomerNo();

                Customer customer1 = customerService.getCustomerBySystemCustomerNo(customerNo);
                String customerFirstName = customer1.getFirstName();
                String customerLastName = customer1.getLastName();

                if(Objects.isNull(iprpDataService.getIPRPDataBySiteId(siteId))){
                    try {
                        IPRPData iprpData = new IPRPData();
                        iprpData.setCustomerId(customerId);
                        iprpData.setSiteId(siteId);
                        iprpData.setSiteName(siteName);
                        iprpData.setDevName(devName);
                        iprpData.setAccountId(accountId);
                        iprpData.setActiveStatus(activeStatus);
                        iprpData.setDevIndex(devIndex);
                        iprpData.setDevSerial(devSerial);
                        iprpData.setDeviceId(deviceId);
                        iprpData.setDeviceKey(deviceKey);
                        iprpDataService.saveIPRPData(iprpData);

                        map.put("status", "SUCCESS");
                        map.put("error", "0");
                        map.put("errorMessage", "");

                        contextName = "SAVED_IPRP_DETAILS_SUCCESSFULLY";
                        try {
                            contextValueJsonString = objectMapper.writeValueAsString(iprpData);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    } catch (Exception e) {
                        map.put("status", "FAIL");
                        map.put("error", "1");
                        map.put("errorMessage", e.toString());

                        String emailSubject = "DSS - Failed To Save IPRP Record";
                        String emailBody = "Sorry. IPRP record could be saved for device " +devSerial+" for customer with details " + customerNo + " " + customerFirstName + " " + customerLastName;
                        sendFailureEmail(emailSubject, emailBody);
                        common.logErrors("api", "HikController", "saveHikDetails", "Save IPRP Record", e.toString());

                        contextName = "FAILED_TO_SAVE_IPRP_DETAILS";
                        try {
                            contextValueJsonString = e.toString();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }

                } else {
                    map.put("status", "FAIL");
                    map.put("error", "1");
                    map.put("errorMessage", "IPRP record with site ID "+siteId+" exists");

                    String emailSubject = "DSS - Cannot Save Duplicate IPRP Device Record";
                    String emailBody = "Sorry. IPRP record could be duplicated for device " +devSerial+" for customer with details " + customerNo + " " + customerFirstName + " " + customerLastName;
                    sendFailureEmail(emailSubject, emailBody);
                    common.logErrors("api", "HikController", "saveHikDetails", "Save IPRP Record", "Attempt to Save Duplicate IPRP Record");

                    contextName = "IPRP_RECORD_ALREADY_EXISTS";
                    try {
                        contextValueJsonString = "IPRP record with site ID "+siteId+" exists";
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                }
            } else {
                map.put("status", "FAIL");
                map.put("error", "1");
                map.put("errorMessage", "No installation record found "+siteId);

                String emailSubject = "DSS - No Installation Site Record Found";
                String emailBody = "Sorry. No installation record found for device with serial number " +devSerial;
                sendFailureEmail(emailSubject, emailBody);
                common.logErrors("api", "HikController", "saveHikDetails", "Save Installation Site Record", "No Installation Site Record Found");

                contextName = "NO_INSTALLATION_RECORD_FOUND";
                try {
                    contextValueJsonString = "No installation record found for site with site ID "+siteId;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }
        } catch (Exception e){
            map.put("status", "FAIL");
            map.put("error", "1");
            map.put("errorMessage", e.toString());
            String emailSubject = "DSS - Failure To Save IPRP Details";
            String emailBody = "Sorry. There was error in saving IPRP details for device with serial number " +devSerial;
            sendFailureEmail(emailSubject, emailBody);
            common.logErrors("api", "SOSController", "saveHikDetails", "Save HIK Details If Installation Site Record With Site ID "+siteId+" Exists", e.toString());

            contextName = "FAILED_TO_SAVE_IPRP_DETAILS";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
        return map;
    }

    @CrossOrigin
    @GetMapping("/thepaymentlink")
    public String generateDPOPaymentLinkManually(@RequestParam("monthNo") int monthNo, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName, @RequestParam("townCity") String townCity, @RequestParam("phone") String phone, @RequestParam("customerId") int customerId, @RequestParam("customerNo") String customerNo, @RequestParam("packageTypeName") String packageTypeName, @RequestParam("vat") double vat, @RequestParam("email") String email, @RequestParam("newInvoiceId") long newInvoiceId, @RequestParam("omniInvoiceNo") String omniInvoiceNo, @RequestParam("paymentAmount") double paymentAmount, @RequestParam("tokenId") String tokenId){
        DPOPaymentResponse dpoResponse = null;
        String payToken = null;
        common = new Common();
        try {
            configData = configDataService.getConfigDataByConfigName("DPO_EXPIRY_PERIOD");
            int expiryPeriod = Integer.parseInt(configData.getConfigValue());
            configData = configDataService.getConfigDataByConfigName("DPO_REDIRECT_URL");
            String dpoRedirectUrl = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("DPO_BACK_URL");
            String dpoBackUrl = configData.getConfigValue();

            String timestamp = common.getTheTimestamp();
            String companyRef = "CUSTOMER-" + customerNo + " MONTH-" + String.valueOf(monthNo) + " INVID-" + newInvoiceId + " OMNIINV" + omniInvoiceNo;

            String fullName = firstName + " " + lastName;
            String dpoMarkup = "DSS Monthly Payment";

            configData = configDataService.getConfigDataByConfigName("DSS_COUNTRY");
            String dssCountry = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("DPO_CURRENCY");
            String dpoCurrency = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("DPO_COMPANY_TOKEN");
            String dpoCompanyToken = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("DPO_SERVICE_TYPE");
            String dpoServiceType = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("REDIRECT_URL_HANDOVER_AND_RECURRING");
            String pathUrl = configData.getConfigValue();
            pathUrl = pathUrl.replace("#customerno", customerNo);
            pathUrl = pathUrl.replace("#tokenid", tokenId);

            String dpoRedirectUrlFull = dpoRedirectUrl + pathUrl;
            dpoPaymentRequest = common.getDPORequest(firstName, lastName, townCity, fullName, email, phone, packageTypeName, paymentAmount, dpoRedirectUrlFull, dpoBackUrl, vat, dpoMarkup, expiryPeriod, tokenId, customerId, companyRef, dpoCompanyToken, dpoCurrency, dssCountry, dpoServiceType);

            dpoResponse = dpoService.makeMonthlyPayment(dpoPaymentRequest);
            payToken = dpoResponse.getTransToken();

            String paymentURL = "https://secure.3gdirectpay.com/payv3.php?ID=" + payToken;

            configData = configDataService.getConfigDataByConfigName("BITLY_AVAILABILITY");
            String bitlyAvailability = configData.getConfigValue();
            String shortenedPaymentUrl = null;
            if (bitlyAvailability.equals("AVAILABLE")) {
                shortenedPaymentUrl = common.shortenUrl(paymentURL, bitlyToken, bitlyUrl);
            } else {
                shortenedPaymentUrl = paymentURL;
            }

            return shortenedPaymentUrl;

        } catch (Exception e){
            common.logErrors("api", "HikController", "sendEvent", "Get DPO Request", e.toString());
            return "ERROR NO PAYMENT LINK GENERATED: \n"+e.toString();
        }
    }


    public String convertMapToString(Map<String, Object> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            // Handle the exception accordingly (e.g., log it or throw a custom exception)
            return e.toString(); // Or throw an exception
        }
    }

    public void sendFailureEmail(String emailSubject, String emailBody){
        ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
        String emailApiUrl = configDataEmail.getConfigValue();
        ConfigData configData = configDataService.getConfigDataByConfigName("FAILURE_EMAIL");
        String emailAddress = configData.getConfigValue();
        emailBody.concat("Regards, SGA Security Team");
        common.sendEmail(emailAddress, emailSubject, emailBody, "0", "", emailApiUrl, "0");
    }

    public String hppGetDetails(String siteId, String url, String token) {
        try {
            contextName = "AT_HPP_GET_DETAILS_GET_VALUES";
            try {
                contextValueJsonString = "Site ID: "+siteId+" URL: "+url+" Token: "+token;
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            ConfigData configDataHPPDataPageSize = configDataService.getConfigDataByConfigName("HPP_DEVICE_PAGE_SIZE");
            String pageSize = configDataHPPDataPageSize.getConfigValue();

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer "+token);

            String requestBody = "{\n" +
                    " \"page\": 1,\n" +
                    " \"pageSize\": "+pageSize+"\n" +
                    "}";

            contextName = "AT_HPP_GET_DETAILS_REQUEST_BODY_SENT";
            try {
                contextValueJsonString = requestBody;
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

//            String requestBody = "{\n" +
//                    " \"siteId\":"+siteId+"\n" +
//                    "}";

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            String targetDeviceType = "DS-PWA64-L-WE";
            String deviceSerial = null;

            ObjectMapper objectMapper = new ObjectMapper();
            DeviceData deviceData = objectMapper.readValue(response.getBody(), DeviceData.class);

            // Access the mapped POJOs and use them as needed
            com.sgasecurity.api.device.Data data = deviceData.getData();
            List<DeviceRow> rows = data.getRows();

            contextName = "AT_HPP_GET_DETAILS_HPP_DATA_ROWS";
            try {
                contextValueJsonString = objectMapper.writeValueAsString(rows);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            for (DeviceRow row : rows) {
                if((row.getDeviceType()).equals(targetDeviceType) && (row.getSiteID()).equals(siteId)){
                    deviceSerial = row.getDeviceSerial();

                    contextName = "AT_HPP_GET_DETAILS_DEVICE_SERIAL_OBTAINED";
                    try {
                        contextValueJsonString = deviceSerial;
                        System.out.println(contextValueJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    break;
                }
            }

            return deviceSerial;

        } catch (Exception e) {
            common.logErrors("api", "HikController", "hppGetDetails", "Extract HPP Device Serial", e.toString());

            contextName = "AT_HPP_GET_DETAILS_CAPTURE_FAILURE";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return "ERROR " + e.toString();
        }
    }
}
package com.sgasecurity.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {
    @Autowired
    InstallationSiteService installationSiteService;
    @Autowired
    ConfigDataService configDataService;
    @Autowired
    NextDateService nextDateService;
    @Autowired
    CustomerService customerService;
    @Autowired
    InvoiceService invoiceService;
    Customer customer = null;
    InstallationSite installationSite = null;
    @Autowired
    PackageTypeService packageTypeService;
    PackageType packageType = null;
    ConfigData configData = null;
    Common common = null;
    String requestBodyJson = null;
    String companyName = null;
    String omniUsername = null;
    String omniPassword = null;
    String omniUrl = null;
    LocalDate nextPaymentDate = null;
    String today = null;
    LocalDate todayFormatYYYYMD = null;
    String onThisDay = null;
    LocalDate plus30Days = null;
    Invoice invoice = null;
    String pkgName = null;
    String smsBody = null;
    String smsApiUrl = null;
    String emailApiUrl = null;
    double paymentAmount = 0;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    AuditTrailService auditTrailService;
    String contextName = "";
    String contextDesc = "";
    String contextValueJsonString = "";

    public void captureAuditTrail(String contextName, String contextDesc, String contextValue) {
        try {
            AuditTrail auditTrail = new AuditTrail();
            auditTrail.setContextName(contextName);
            auditTrail.setContextDesc(contextDesc);
            auditTrail.setContextValue(contextValue);
            auditTrailService.saveAuditTrail(auditTrail);
        } catch (Exception e) {
            common.logErrors("api", "PaymentController", "captureAuditTrail", "Capture Audit Trail", e.toString());
        }
    }

    public String postingPaymentFromDPOToOmni(String domain, String userName, String password, String companyName, String postFields) {
        try {
            String url = domain + "/BankingTransaction?UserName="+userName+"&Password="+password+"&CompanyName="+companyName;
            url = url.replace(" ", "%20");

            RestTemplate restTemplate = new RestTemplate();

            URI uri = URI.create(url);
            String protocol = uri.getScheme();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(postFields, headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            String result = response.getBody();

            return result;

        } catch (Exception e) {
            common.logErrors("api", "PaymentController", "postingPaymentFromDPOToOmni", "Posting Payment From DPO To Omni", e.toString());
            return e.toString();
        }
    }


    public String postJob(String domain, String userName, String password, String companyName, String postFields, String jobNo) {
        try {

            String url = domain + "/Job/"+jobNo+"?UserName="+userName+"&Password="+password+"&CompanyName="+companyName;
            url = url.replace(" ", "%20");

            RestTemplate restTemplate = new RestTemplate();

            URI uri = URI.create(url);
            String protocol = uri.getScheme();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(postFields, headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            String result = response.getBody();

            return result;

        } catch (Exception e) {
            common.logErrors("api", "PaymentController", "postJob", "Post Job", e.toString());
            return e.toString();
        }
    }

    @CrossOrigin
    @GetMapping("/omni/invoice/push")
    @ResponseBody
    public String omniInvoicePush(@RequestParam("customerNo") String customerNo, @RequestParam("tokenId") String tokenId) throws JsonProcessingException {
        common = new Common();
        try {
            DateFunctions  dateFunctions= new DateFunctions();


            double amountInclusive = 0;
            int transactionNo = 0;
            String invoiceRefNo = "NOT_YET_ASSIGNED";
            String narrative = "NO_NARRATIVE_YET";

            ConfigData configDataVAT= configDataService.getConfigDataByConfigName("VAT");
            double centralDBVAT = Double.parseDouble(configDataVAT.getConfigValue());

            configData = new ConfigData();
            InstallationSite installationSite = installationSiteService.getInstallationSiteBySystemCustomerNo(customerNo);

            packageType = packageTypeService.getPackageType(installationSite.getPackageTypeName());

            invoice = new Invoice();

            ConfigData configDataSms = configDataService.getConfigDataByConfigName("SMS_API_URL");
            smsApiUrl = configDataSms.getConfigValue();

            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
            emailApiUrl = configDataEmail.getConfigValue();

            LocalDate startingDate = LocalDate.now();
            String finalStartingDateString =  dateFunctions.doFormatDate(3, startingDate);
            nextPaymentDate = dateFunctions.getNextAnniversayReturnDate(startingDate);
            String finalNextDateString= dateFunctions.doFormatDate(3, nextPaymentDate);

            /*
            nextPaymentDate = installationSite.getNextPaymentDate();
            today = nextDateService.startingDate();
            todayFormatYYYYMD = nextDateService.startingDateYYYYMD();
            onThisDay = nextDateService.getDayValue(startingDate);
            plus30Days = nextDateService.add30DaysYYYYMD();
            */

            ConfigData configDataSafeHomeServiceCode = configDataService.getConfigDataByConfigName("SAFE_HOME_SERVICE_CODE");
            String safeHomeServiceCode = configDataSafeHomeServiceCode.getConfigValue();
            ConfigData configDataSafeHomePlusServiceCode = configDataService.getConfigDataByConfigName("SAFE_HOME_PLUS_SERVICE_CODE");
            String safeHomePlusServiceCode = configDataSafeHomePlusServiceCode.getConfigValue();

            if(installationSite.getPackageTypeName().equals("SAFE_HOME")){
                pkgName = safeHomeServiceCode;
            } else {
                pkgName = safeHomePlusServiceCode;
            }

            paymentAmount = getMonthlyExclusive(installationSite.getPackageTypeName());

            try {
                ConfigData configDataUseTempAmount = configDataService.getConfigDataByConfigName("USE_TEMP_AMOUNT");
                String useTemAmount = configDataUseTempAmount.getConfigValue();

                if(useTemAmount.equals("YES")){
                    ConfigData configDataTempAmountValue = configDataService.getConfigDataByConfigName("TEMP_AMOUNT_VALUE");
                    paymentAmount = Double.parseDouble(configDataTempAmountValue.getConfigValue());
                } else {
                    paymentAmount = packageType.getMonthlyCostExclusive();
                }
            } catch (Exception e){
                common.logErrors("api", "HikController", "sendEvent", "Get Payment Amount", e.toString());
            }

            // 1. POST INVOICE TO OMNI
            // A. Prepare json request data
//            requestBodyJson = "{\"invoice\" : {\"customer_account_code\" : \"" + customerNo + "\", \"invoice_lines\": [{\"description\" : \"Posting invoice to Omni\"}, {\"stock_code\": \""+pkgName+"\",\"quantity\" : 1,\"selling_price\" : " + paymentAmount + "}, {\"description\" : \"Service as at " + today.toString() + " to " + nextPaymentDate.toString() + "\"}]}}";

            String firstDescription = "Monthly Subscription Service charge for ";

            String secondDescription = "for the period "+finalStartingDateString+ " to " + finalNextDateString;

            requestBodyJson = "{\"invoice\" : {\"customer_account_code\" : \"" + customerNo + "\", \"invoice_lines\": [{\"description\" : \""+firstDescription+"\"}, {\"stock_code\": \""+pkgName+"\",\"quantity\" : 1,\"selling_price\" : " + paymentAmount + "}, {\"description\" : \""+secondDescription+"\"}]}}";


            contextName = "PUSH_OMNI_INVOICE_DATA_AT_OMNI_API_DEPOSIT";
            try {
                contextValueJsonString = requestBodyJson;
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            ConfigData configDataOmniAvailability = configDataService.getConfigDataByConfigName("OMNI_AVAILABILITY");
            String omniAvailability = configDataOmniAvailability.getConfigValue();

            if(omniAvailability.equals("YES")){
                // B. Get Omni credentials
                configData = configDataService.getConfigDataByConfigName("OMNI_COMPANY_NAME");
                companyName = configData.getConfigValue();

                configData = configDataService.getConfigDataByConfigName("OMNI_U_NAME");
                omniUsername = configData.getConfigValue();

                configData = configDataService.getConfigDataByConfigName("OMNI_USER_PASSWORD");
                omniPassword = configData.getConfigValue();

                configData = configDataService.getConfigDataByConfigName("OMNI_URL");
                omniUrl = configData.getConfigValue();

//          C. Post Invoice to Omni
                String invoiceNumber = postingInvoiceToOmni(omniUrl, omniUsername, omniPassword, companyName, requestBodyJson);

                contextName = "POSTING_INVOICE_TO_OMNI_DURING_HANDOVER";
                try {
                    contextValueJsonString = invoiceNumber;
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                String invoiceResponse = getInvoiceResponse(omniUrl, omniUsername, omniPassword, companyName, invoiceNumber);

                contextName = "GET_INVOICE_RESPONSE_DURING_HANDOVER";
                try {
                    contextValueJsonString = invoiceResponse;
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                if(invoiceResponse != null){
//                    amountInclusive = JsonPath.read(invoiceResponse, "$.invoice.total_info.inclusive_value_after_discount");
////                  transactionNo = JsonPath.read(invoiceResponse, "$.invoice.transaction_no");
//                    invoiceRefNo = JsonPath.read(invoiceResponse, "$.invoice.invoice_lines[0].reference");

                    amountInclusive = packageType.getMonthlyCostInclusive();
                    invoiceRefNo = invoiceNumber;
                    narrative = "Success. Amount and Invoice Ref No. generated from Omni side. (Recurrent Payment)";

                    contextName = "OMNI_INVOICE_PUSH";
                    contextDesc = "Omni Invoice Push";
                    contextValueJsonString = "";
                    objectMapper = new ObjectMapper();
                    try {
                        contextValueJsonString = objectMapper.writeValueAsString(invoiceResponse);
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                } else {
                    amountInclusive = paymentAmount * centralDBVAT;

                    BigDecimal amountInclusiveBigDecimal = new BigDecimal(amountInclusive);
                    amountInclusive = amountInclusiveBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

//                  transactionNo = JsonPath.read(invoiceResponse, "$.invoice.transaction_no");
                    invoiceRefNo = "NONE";
                    narrative = "Take Note: Amount and Invoice Ref No. generated from SGA API end. Check the availability of Omni and confirm the accuracy of these values.";

                    contextName = "OMNI_INVOICE_PUSH_NULL";
                    contextDesc = "Omni Invoice Null";
                    contextValueJsonString = "";
                    objectMapper = new ObjectMapper();
                    try {
                        contextValueJsonString = objectMapper.writeValueAsString(invoiceResponse);
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                }

            } else {
                amountInclusive = paymentAmount * centralDBVAT;

                BigDecimal amountInclusiveBigDecimal = new BigDecimal(amountInclusive);
                amountInclusive = amountInclusiveBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

//              transactionNo = JsonPath.read(invoiceResponse, "$.invoice.transaction_no");
                invoiceRefNo = "NONE";
                narrative = "Take Note: Amount and Invoice Ref No. generated from SGA API end. Check the availability of Omni and confirm the accuracy of these values.";

                contextName = "OMNI_INVOICE_PUSH_DESELECTED";
                contextDesc = "OMNI Invoice Deselected";
                objectMapper = new ObjectMapper();
                try {
                    contextValueJsonString = amountInclusive + " -- " + invoiceRefNo + " -- " + narrative;
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }

//            try{
////              Save invoice data into Invoice table
//                invoice.setInvoiceRefNo(invoiceRefNo);
//                invoice.setAmount(amountInclusive);
//                invoice.setSystemCustomerNo(installationSite.getSystemCustomerNo());
//                invoice.setInstallationId(installationSite.getId());
//                invoice.setPackageTypeId(installationSite.getPackageTypeId());
//                invoice.setInvoicingDate(todayFormatYYYYMD);
//                invoice.setTokenId(tokenId);
//                invoice.setNarrative(narrative);
//                invoice.setIsPaidOut(0); // Not yet paid
//                invoice.setIsPrinted("PENDING");
//                invoiceService.saveInvoice(invoice);
//
//                contextName = "SAVE_INVOICE_RECORD";
//                contextDesc = "Save Invoice Record For Monthly Payment";
//                contextValueJsonString = "";
//                objectMapper = new ObjectMapper();
//                try {
//                    contextValueJsonString = objectMapper.writeValueAsString(invoice);
//                    System.out.println(contextValueJsonString);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//
//            } catch (Exception e){
//                common.logErrors("api", "PaymentController", "omniInvoicePush", "Save Invoice Record For Recurrent Payment", e.toString());
//            }

            contextName = "POST_INVOICE_TO_OMNI_SUCCESSFUL";
            try {
                contextValueJsonString = "Successful Post Of Invoice To Omni";
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return invoiceRefNo;

        } catch (Exception e){
            common.logErrors("api", "PaymentController", "omniInvoicePush", "Push Invoice To Omni", e.toString());

            contextName = "POST_INVOICE_TO_OMNI_FAILED_AND_ERROR_CAUGHT";
            try {
                contextValueJsonString = "Failed To Post Invoice To Omni:\n\nError - "+e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return "ERROR";
        }
    }

    public String postingInvoiceToOmni(String domain, String userName, String password, String companyName, String postFields) {
        try {

            String url = domain + "/Invoice/?UserName="+userName+"&Password="+password+"&CompanyName="+companyName;
            url = url.replace(" ", "%20");

            RestTemplate restTemplate = new RestTemplate();

            URI uri = URI.create(url);
            String protocol = uri.getScheme();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(postFields, headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            String result = response.getBody();

            return result;

        } catch (Exception e) {
            common.logErrors("api", "PaymentController", "postingInvoiceToOmni", "Post Invoice To Omni", e.toString());
            return e.toString();
        }
    }

    public String getInvoiceResponse(String domain, String userName, String password, String companyName, String invoiceNumber){
        try {

            String url = domain + "/Invoice/"+invoiceNumber+"?UserName="+userName+"&Password="+password+"&CompanyName="+companyName;
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
            common.logErrors("api", "PaymentController", "getInvoiceResponse", "Get Invoice Response", e.toString());
            return null;
        }
    }

    public String customerReceipt(String domain, String userName, String password, String companyName, String postFields) {
        try {

            String url = domain + "/BankingTransaction?UserName="+userName+"&Password="+password+"&CompanyName="+companyName;
            url = url.replace(" ", "%20");

            RestTemplate restTemplate = new RestTemplate();

            URI uri = URI.create(url);
            String protocol = uri.getScheme();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                common.logErrors("api", "PaymentController", "customerReceipt", "Customer Receipt", "Unsupported protocol " + protocol + "(if else block)");
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(postFields, headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            String result = response.getBody();

            return result;

        } catch (Exception e) {
            common.logErrors("api", "PaymentController", "customerReceipt", "Customer Receipt", e.toString());
            return e.toString();
        }
    }

    public Map<String, Double> stockCreate(@RequestParam("stockCode") String stockCode) {
        try {
            configData = configDataService.getConfigDataByConfigName("OMNI_COMPANY_NAME");
            companyName = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("OMNI_U_NAME");
            omniUsername = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("OMNI_USER_PASSWORD");
            omniPassword = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("OMNI_URL");
            omniUrl = configData.getConfigValue();

            String url = omniUrl + "/Stock%20Item/"+stockCode+"?UserName="+omniUsername+"&Password="+omniPassword+"&CompanyName="+companyName;
            url = url.replace(" ", "%20");

            requestBodyJson = "{\"stockitem\" :  { \n" +
                    "  \"stock_code\" : \""+stockCode+"\",\n" +
                    "  \"stock_description\" : \"Home Safe Plus Service for a month\"\n" +
                    "}\n" +
                    "}";

            RestTemplate restTemplate = new RestTemplate();

            URI uri = URI.create(url);
            String protocol = uri.getScheme();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                common.logErrors("api", "PaymentController", "stockCreate", "Create Stock", "Unsupported protocol: " + protocol + "(if else block)");
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            String result = response.getBody();

            JSONObject jsonObject = new JSONObject(result);
            double sellingPrice1 = jsonObject.getJSONObject("stockitem").getDouble("selling_price_1");

            Map<String, Double> mapResult = new HashMap<>();
            mapResult.put("monthlyPaymentExclusive", sellingPrice1);

            return mapResult;

        } catch (Exception e) {
            common.logErrors("api", "PaymentController", "stockCreate", "Create Stock", e.toString());
            return null;
        }
    }

    @CrossOrigin
    @GetMapping("/prices/update")
    @ResponseBody
    public String pricesUpdate() {

        ConfigData configDataSHServiceCode = configDataService.getConfigDataByConfigName("SAFE_HOME_SERVICE_CODE");
        String safeHomeServiceCode = configDataSHServiceCode.getConfigValue();

        ConfigData configDataSHPServiceCode = configDataService.getConfigDataByConfigName("SAFE_HOME_PLUS_SERVICE_CODE");
        String safeHomePlusServiceCode = configDataSHPServiceCode.getConfigValue();

        Map<String, Double> safeHome = stockCreate(safeHomeServiceCode);
        Map<String, Double> safeHomePlus = stockCreate(safeHomePlusServiceCode);

        double monthlyPaymentExclusiveSafeHome = safeHome.get("monthlyPaymentExclusive");

        double monthlyPaymentInclusiveSafeHome = monthlyPaymentExclusiveSafeHome + (monthlyPaymentExclusiveSafeHome * 0.16);
        BigDecimal monthlyPaymentInclusiveSafeHomeBigDecimal = new BigDecimal(monthlyPaymentInclusiveSafeHome);
        monthlyPaymentInclusiveSafeHome = monthlyPaymentInclusiveSafeHomeBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        double depositSafeHome = monthlyPaymentInclusiveSafeHome * 2;
        BigDecimal depositSafeHomeBigDecimal = new BigDecimal(depositSafeHome);
        depositSafeHome = depositSafeHomeBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        double monthlyPaymentExclusiveSafeHomePlus = safeHomePlus.get("monthlyPaymentExclusive");

        double monthlyPaymentInclusiveSafeHomePlus = monthlyPaymentExclusiveSafeHomePlus + (monthlyPaymentExclusiveSafeHomePlus * 0.16);
        BigDecimal monthlyPaymentInclusiveSafeHomePlusBigDecimal = new BigDecimal(monthlyPaymentInclusiveSafeHomePlus);
        monthlyPaymentInclusiveSafeHomePlus = monthlyPaymentInclusiveSafeHomePlusBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        double depositSafeHomePlus = monthlyPaymentInclusiveSafeHomePlus * 2;
        BigDecimal depositSafeHomePlusBigDecimal = new BigDecimal(depositSafeHomePlus);
        depositSafeHomePlus = depositSafeHomePlusBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        packageType = packageTypeService.getPackageType("SAFE_HOME");
        packageType.setMonthlyCostExclusive(monthlyPaymentExclusiveSafeHome);
        packageType.setMonthlyCostInclusive(monthlyPaymentInclusiveSafeHome);
        packageType.setDepositAmount(depositSafeHome);
        packageTypeService.savePackageType(packageType);

        packageType = packageTypeService.getPackageType("SAFE_HOME_PLUS");
        packageType.setMonthlyCostExclusive(monthlyPaymentExclusiveSafeHomePlus);
        packageType.setMonthlyCostInclusive(monthlyPaymentInclusiveSafeHomePlus);
        packageType.setDepositAmount(depositSafeHomePlus);
        packageTypeService.savePackageType(packageType);

        System.out.println("Running prices updater loop...");
        return "SUCCESS";
    }

    private double getMonthlyCostInclusive(String packageName) {
        packageType = packageTypeService.getPackageType(packageName);
        return packageType.getMonthlyCostInclusive();
    }

    private double getMonthlyExclusive(String packageName) {
        packageType = packageTypeService.getPackageType(packageName);
        return packageType.getMonthlyCostExclusive();
    }
}
package com.sgasecurity.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@Controller
public class RecurrentPaymentController {
    @Autowired
    InstallationSiteService installationSiteService;
    @Autowired
    ConfigDataService configDataService;
    ConfigData configData = null;
    @Autowired
    CustomerService customerService;
    @Autowired
    InvoiceService invoiceService;
    Invoice invoice = null;
    Customer customer = null;
    InstallationSite installationSite = null;
    String emailAddress = null;
    String emailSubject = null;
    String emailHeader = null;
    String emailFooter = null;
    String fullEmail = null;
    String emailBody = null;
    String smsBody = null;
    Common common = null;
    String requestBodyJson = null;
    String companyName = null;
    String omniUsername = null;
    String omniPassword = null;
    String omniUrl = null;
    LocalDate nextPaymentDate = null;
    LocalDate todayFormatYYYYMD = null;
    String todayFormatDMYYYY = null;
    RecurrentPayment recurrentPayment = null;
    @Autowired
    RecurrentPaymentService recurrentPaymentService;
    @Autowired
    PackageTypeService packageTypeService = null;
    PackageType packageType = null;
    String customerNo = null;
    @Autowired
    DPOService dpoService;
    String smsApiUrl = null;
    String emailApiUrl = null;
    String bitlyToken = null;
    String bitlyUrl = null;
    DPOPaymentRequest dpoPaymentRequest = null;
    String payToken = null;
    DPOPaymentResponse dpoResponse = null;
    double paymentAmount = 0;
    double paymentAmountInclusive = 0;
    double paymentAmountExclusive = 0;
    String phone = null;
    @Autowired
    HikEventService hikEventService;
    HikEvent hikEvent = null;
    String hppSiteId = null;
    String hikFormattedCustomerSiteName = null;
    String uniqueSiteId = null;
    String hikToken = "";
    String hikDeviceEnableUrl = "";
    @Autowired
    PaymentLinkService paymentLinkService;
    PaymentLink paymentLink = null;
    String shortenedPaymentUrl = null;
    @Autowired
    LastJobNoService lastJobNoService;
    @Autowired
    SiteTimedEventRepository siteTimedEventRepository;
    @Autowired
    SiteTimedEventService siteTimedEventService;
    @Autowired
    NewMonthService newMonthService;
    NewMonth newMonth = null;
    @Autowired
    NextDateService nextDateService;
    String onThisDay = null;
    LocalDate plus30Days = null;
    String pkgName = null;
    String payLink = "";
    String payLinkForBalances = "";
    String payLinkForBalancesForSMS = "";
    @Autowired
    IPRPDataService iprpDataService;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    AuditTrailService auditTrailService;
    String contextName = "";
    String contextDesc = "";
    String contextValueJsonString = "";

    public void captureAuditTrail(String contextName, String contextDesc, String contextValue) {
        common = new Common();
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

    @CrossOrigin
    @GetMapping("/send24hrcomms")
    @ResponseBody
    public String send24HrComms(@RequestParam String uniqueSiteId) throws JsonProcessingException {
        common = new Common();
        try {
            configData = new ConfigData();

            installationSite = installationSiteService.getInstallationSiteByUniqueSiteId(uniqueSiteId);
            customerNo = installationSite.getSystemCustomerNo();
            customer = customerService.getCustomerBySystemCustomerNo(customerNo);
//            invoice = new Invoice();

            try {
                ConfigData configDataHikEnableUrl = configDataService.getConfigDataByConfigName("HIK_URL_ENABLE");
                hikDeviceEnableUrl = configDataHikEnableUrl.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("HIK_TOKEN_URL");
                String url = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("HIK_SECRET_KEY");
                String secretKey = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("HIK_APP_KEY");
                String appKey = configData.getConfigValue();
                hikToken = common.getHikToken(appKey, secretKey, url);
            } catch (Exception e) {

            }


            ConfigData configDataSms = configDataService.getConfigDataByConfigName("SMS_API_URL");
            smsApiUrl = configDataSms.getConfigValue();

            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
            emailApiUrl = configDataEmail.getConfigValue();

            ConfigData configDataBitlyToken = configDataService.getConfigDataByConfigName("BITLY_TOKEN");
            bitlyToken = configDataBitlyToken.getConfigValue();

            ConfigData configDataBitlyUrl = configDataService.getConfigDataByConfigName("BITLY_URL");
            bitlyUrl = configDataBitlyUrl.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("DEVICE_ENABLE_AVAILABILITY");
            String deviceEnableAvailability = configData.getConfigValue();

//          B. Get Omni credentials
            configData = configDataService.getConfigDataByConfigName("OMNI_COMPANY_NAME");
            companyName = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("OMNI_U_NAME");
            omniUsername = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("OMNI_USER_PASSWORD");
            omniPassword = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("OMNI_URL");
            omniUrl = configData.getConfigValue();

            hppSiteId = installationSite.getHppSiteId();
            hikFormattedCustomerSiteName = installationSite.getHikFormattedCustomerSiteName();

            try {
                paymentLink = paymentLinkService.getLatestPaymentLink(customerNo);
                shortenedPaymentUrl = paymentLink.getLink();

                contextName = "AT_24_HOUR_INTERVAL_GENERATED_PAYMENT_LINK";
                try {
                    contextValueJsonString = "shortenedPaymentUrl "+shortenedPaymentUrl.concat(" -- customerNo "+customerNo);
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            } catch (Exception e) {
                common.logErrors("api", "RecurrentPaymentController", "send96hrcomms", "Get Shortened Payment Url", e.toString());

                contextName = "AT_24_HOUR_INTERVAL_GENERATE_PAYMENT_LINK_FAILED";
                try {
                    contextValueJsonString = e.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }

            String iprpResponse = null;

//          Disable IPRP
            try {
                ConfigData configDataUrl = configDataService.getConfigDataByConfigName("HIK_IPRP_STATUS_UPDATE_URL_PUBLIC");
//              ConfigData configDataUrl = configDataService.getConfigDataByConfigName("HIK_IPRP_STATUS_UPDATE_URL_LOCAL");
                String iprpUrl = configDataUrl.getConfigValue();
                ConfigData configDataHikIprpUsername = configDataService.getConfigDataByConfigName("HIK_IPRP_USERNAME");
                String hikIprpUsername = configDataHikIprpUsername.getConfigValue();
                ConfigData configDataHikIprpPwd = configDataService.getConfigDataByConfigName("HIK_IPRP_PASSWORD");
                String hikIprpPawd = configDataHikIprpPwd.getConfigValue();

                String accountID = null;
                String deviceName = null;
                String deviceIndex = null;
                String deviceSerial = null;
                String customerName = null;
                String status = null;

//              Check if IPRP details exists
                if(Objects.isNull(iprpDataService.getIPRPDataBySiteId(hppSiteId))){
                    try {
//                      iprpResponseJson = common.changeIPRPStatus(accountID, deviceName, deviceIndex, status, hikIprpUsername, hikIprpPawd, iprpUrl);
                        ResponseEntity<String> iprpResponseJsonRecreate = iprpDeviceDetails( installationSite.getDeviceSerial());
                        iprpResponse = iprpResponseJsonRecreate.toString();

                        contextName = "AT_24_HOUR_INTERVAL_IPRP_RESPONSE";
                        try {
                            contextValueJsonString = "iprpResponse "+iprpResponse +" -- customerNo "+customerNo;
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    } catch (Exception e){
                        contextName = "AT_24_HOUR_FAILED_TO_GET_IPRP_DETAILS";
                        try {
                            contextValueJsonString = "Could not fetch IPRP details for " + customerNo + " " + customer.getFirstName() + " " + customer.getLastName()+" at 24HRS disconnect";
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

//                      Send failure email
                        String emailSubject = "DSS - (24HRS Disconnect) - Failed to Get IPRP Details "+customer.getSystemCustomerNo();
                        String emailBody = "Sorry. Could not fetch IPRP details for " + customerNo + " " + customer.getFirstName() + " " + customer.getLastName()+" at 24HRS disconnect";
                        sendFailureEmail(emailSubject, emailBody, emailApiUrl);
                    }
                }
                else
                {
//                  IPRP record exists
                    IPRPData iprpData = iprpDataService.getIPRPDataBySiteId(hppSiteId);
                    accountID = iprpData.getAccountId();
                    deviceName = iprpData.getDevName();
                    deviceIndex = iprpData.getDevIndex();
                    deviceSerial = iprpData.getDevSerial();
                    customerName = customer.getFirstName() +" "+customer.getLastName();
                    status = "inactive";

                    contextName = "AT_24_HOUR_SAVING_IPRP_DETAILS";
                    try {
                        contextValueJsonString = "accountID " +accountID+" -- deviceName "+deviceName+" -- deviceIndex "+deviceIndex+" -- deviceSerial "+deviceSerial+" -- customerName "+customerName+" -- status "+status;
                        System.out.println(contextValueJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    try {
                        JsonNode iprpResponseJson = common.changeIPRPStatus(accountID, deviceName, deviceIndex, status, hikIprpUsername, hikIprpPawd, iprpUrl);
                        iprpResponse = iprpResponseJson.toString();
                        iprpData.setActiveStatus(status);
                        iprpDataService.saveIPRPData(iprpData);

                        contextName = "AT_24_HOUR_AFTER_EXECUTING_IPRP";
                        try {
                            contextValueJsonString = "iprpResponse -- "+iprpResponse+" Customer No. " +customer.getSystemCustomerNo()+" -- IPRP Response "+objectMapper.writeValueAsString(iprpData);
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    } catch (Exception e){
//                      Send failure email
                        ConfigData configDataEmailApiUrl = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
                        String emailApiUrl = configDataEmailApiUrl.getConfigValue();
                        ConfigData configData = configDataService.getConfigDataByConfigName("FAILURE_EMAIL");
                        String emailAddress = configData.getConfigValue();
                        String emailSubject = "DSS - Failed To Disable IPRP";
                        String emailBody = "Sorry. IPRP could not be disabled for " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName();
                        emailBody.concat("Regards, SGA Security Team");
                        common.sendEmail(emailAddress, emailSubject, emailBody, "0", "", emailApiUrl, "0");

                        contextName = "AT_24_HOUR_AFTER_EXECUTING_IPRP_FAILED";
                        try {
                            contextValueJsonString = e.toString();
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }
                }

                try {
                    hikEvent = new HikEvent();
                    hikEvent.setSystemCustomerNo(customerNo);
                    hikEvent.setHppSiteId(hppSiteId);
                    hikEvent.setUniqueSiteId(uniqueSiteId);
                    hikEvent.setHikFormattedCustomerSiteName(hikFormattedCustomerSiteName);
//                  hikEvent.setEventResponse(iprpResponse);
                    hikEvent.setIprpResponse(iprpResponse);
                    hikEvent.setEventType("DISABLE");
                    hikEventService.saveHikEvent(hikEvent);
                }  catch (Exception e) {
                    common.logErrors("api", "RecurrentPaymentController", "send24hrcomms", "Save HIK Event", e.toString());

                    contextName = "AT_24_HOUR_FAILED_TO_SAVE_HIK_EVENT";
                    try {
                        contextValueJsonString = e.toString();
                        System.out.println(contextValueJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                }

                try {
                    sendSiteDisableEmailToFinance(installationSite.getHikFormattedCustomerSiteName(), emailApiUrl);

                    contextName = "AT_24_HOUR_INTERVAL_SENT_SITE_DISABLE_EMAIL_TO_FINANCE";
                    try {
                        contextValueJsonString = "customerNo "+installationSite.getSystemCustomerNo().concat("-- Hik Formatted Customer Site Name "+installationSite.getHikFormattedCustomerSiteName());
                        System.out.println(contextValueJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                } catch (Exception e){
                    contextName = "AT_24_HOUR_INTERVAL_FAILED_TO_SEND_SITE_DISABLE_EMAIL_TO_FINANCE";
                    try {
                        contextValueJsonString = "customerNo "+installationSite.getSystemCustomerNo().concat("-- Hik Formatted Customer Site Name "+installationSite.getHikFormattedCustomerSiteName())+"\n\n"+e.toString();
                        System.out.println(contextValueJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                }

                try {
                    sendSiteDisableEmailToTechnicalLead(installationSite.getHikFormattedCustomerSiteName(), emailApiUrl);

                    contextName = "AT_24_HOUR_INTERVAL_SENT_SITE_DISABLE_EMAIL_TO_TECHNICAL_LEAD";
                    try {
                        contextValueJsonString = "customerNo "+installationSite.getSystemCustomerNo().concat("-- Hik Formatted Customer Site Name "+installationSite.getHikFormattedCustomerSiteName());
                        System.out.println(contextValueJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                } catch (Exception e){
                    contextName = "AT_24_HOUR_INTERVAL_FAILED_TO_SEND_SITE_DISABLE_EMAIL_TO_TECHNICAL_LEAD";
                    try {
                        contextValueJsonString = "customerNo "+installationSite.getSystemCustomerNo().concat("-- Hik Formatted Customer Site Name "+installationSite.getHikFormattedCustomerSiteName())+"\n\n"+e.toString();
                        System.out.println(contextValueJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                }


//                try {
////                  Send email to Lead Technician and Finance
//                    configData = configDataService.getConfigDataByConfigName("SITE_DISABLE_EMAIL_SUBJECT");
//                    emailSubject = configData.getConfigValue();
//                    configData = configDataService.getConfigDataByConfigName("SITE_DISABLE_EMAIL_BODY");
//                    emailBody = configData.getConfigValue();
//
//                    configData = configDataService.getConfigDataByConfigName("OMNI_FINANCE_EMAIL");
//                    String financeEmail = configData.getConfigValue();
//                    configData = configDataService.getConfigDataByConfigName("TECHNICAL_LEAD_EMAIL");
//                    String technicalLeadEmail = configData.getConfigValue();
//
//                    emailBody = emailBody.replace("#sitename", installationSite.getHikFormattedCustomerSiteName());
//                    emailBody = emailBody.replace("#deviceserial", deviceSerial);
//                    emailBody = emailBody.replace("#customerno", customerNo);
//                    emailBody = emailBody.replace("#customername", customerName);
//                    emailBody = emailBody.replace("#date", LocalDateTime.now().toString());
//                    emailBody = emailBody.replace("#typeofevent", "ENABLE");
//
//                    common.sendEmail(financeEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");
//                    common.sendEmail(technicalLeadEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");
//
//                } catch (Exception e) {
//                    common.logErrors("api", "RecurrentPaymentController", "send24hrcomms", "Send Email To Technician", e.toString());
//                }
            } catch (Exception e) {
                common.logErrors("api", "RecurrentPaymentController", "send24hrcomms", "Disable IPRP", e.toString());

                contextName = "AT_24_HOUR_INTERVAL_FAILED_TO_DISABLE_IPRP_AND_THREW ERROR";
                try {
                    contextValueJsonString = e.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }

            if(deviceEnableAvailability.equals("YES")){
//              Disable cloud devices
                disableDevices(hppSiteId, customerNo, uniqueSiteId, hikFormattedCustomerSiteName, emailApiUrl, hikDeviceEnableUrl, hikToken);
            }

            contextName = "AT_24_HOUR_DISABLE_DEVICES";
            try {
                if(deviceEnableAvailability.equals("YES")){
                    contextValueJsonString = "At 24 Hour Disable Devices For Customer With Customer Number "+customerNo + " With Site Name "+hikFormattedCustomerSiteName;
                } else {
                    contextValueJsonString = "{\n" +
                            "  \"status\": \"Device cloud enable API is not available\"\n" +
                            "}";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

////          Disable ARC device
//            try {
//                configData = configDataService.getConfigDataByConfigName("ARC_SERVICE_URL");
//                String url = configData.getConfigValue();
//
//                common.disableARC(installationSite.getDeviceSerial(), url, hikToken);
//                contextName = "AT_24_HOUR_INTERVAL_DISABLE_ARC";
//                try {
//                    contextValueJsonString = "Disable ARC (common.disableARC(installationSite.getDeviceSerial())) On Device Serial " + installationSite.getDeviceSerial();
//                    System.out.println(contextValueJsonString);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//            } catch (Exception e) {
//                common.logErrors("api", "RecurrentPaymentController", "send96hrcomms", "Disable ARC Device", e.toString());
//                contextName = "AT_24_HOUR_INTERVAL_DISABLE_ARC_FAILED";
//                try {
//                    contextValueJsonString = "Failed to Disable ARC (common.disableARC(installationSite.getDeviceSerial())) On Device Serial " + installationSite.getDeviceSerial();
//                    System.out.println(contextValueJsonString);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//            }

            installationSite.setInstallationStatus("DISABLED");
            installationSiteService.saveInstallationSite(installationSite);

            contextName = "AT_24_HOUR_INTERVAL_UPDATE_OF_INSTALLATION_SITE_RECORD_STATUS_TO_DISABLE";
            try {
                contextValueJsonString = objectMapper.writeValueAsString(installationSite);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            contextName = "PASSED_VALUES_TO_SEND_EMAIL_24HR_COMMS";
            try {
                contextValueJsonString = objectMapper.writeValueAsString(customer)+"shortenedPaymentUrl "+shortenedPaymentUrl+" emailApiUrl: "+emailApiUrl;
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            sendAccountSuspensionEmail(customer, shortenedPaymentUrl, emailApiUrl);

            contextName = "PASSED_VALUES_TO_SEND_SMS_24HR_COMMS";
            try {
                contextValueJsonString = objectMapper.writeValueAsString(customer)+"shortenedPaymentUrl "+shortenedPaymentUrl+" smsApiUrl: "+smsApiUrl;
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            sendAccountSuspensionSMS(customer, shortenedPaymentUrl, smsApiUrl);

            return "SUCCESS";

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "send96hrcomms", "Send Email To Notify Customer Of Suspension", e.toString());
            return "FAIL";
        }
    }

    @CrossOrigin
    @GetMapping("/send552hrcomms")
    @ResponseBody
    public String send552HrComms(@RequestParam String uniqueSiteId) throws JsonProcessingException {

        try {
            common = new Common();
            configData = new ConfigData();

            installationSite = installationSiteService.getInstallationSiteByUniqueSiteId(uniqueSiteId);
            customerNo = installationSite.getSystemCustomerNo();
            customer = customerService.getCustomerBySystemCustomerNo(customerNo);
            invoice = new Invoice();

            ConfigData configDataSms = configDataService.getConfigDataByConfigName("SMS_API_URL");
            smsApiUrl = configDataSms.getConfigValue();

            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
            emailApiUrl = configDataEmail.getConfigValue();

            ConfigData configDataBitlyToken = configDataService.getConfigDataByConfigName("BITLY_TOKEN");
            bitlyToken = configDataBitlyToken.getConfigValue();

            ConfigData configDataBitlyUrl = configDataService.getConfigDataByConfigName("BITLY_URL");
            bitlyUrl = configDataBitlyUrl.getConfigValue();

//          B. Get Omni credentials
            configData = configDataService.getConfigDataByConfigName("OMNI_COMPANY_NAME");
            companyName = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("OMNI_U_NAME");
            omniUsername = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("OMNI_USER_PASSWORD");
            omniPassword = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("OMNI_URL");
            omniUrl = configData.getConfigValue();

            hppSiteId = installationSite.getHppSiteId();
            hikFormattedCustomerSiteName = installationSite.getHikFormattedCustomerSiteName();

            packageType = packageTypeService.getPackageType(installationSite.getPackageTypeName());
            paymentAmountInclusive = packageType.getMonthlyCostInclusive();

            LocalDate anniversaryDate = installationSite.getHandoverDate().plusDays(30);
            String anniversaryDateStr = anniversaryDate.toString();

            try {
//              Send email asking customer to pay
                configData = configDataService.getConfigDataByConfigName("SEVEN_DAYS_TO_PAYMENT_DATE_EMAIL_SUBJECT");
                emailSubject = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
                emailHeader = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
                emailFooter = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("SEVEN_DAYS_TO_PAYMENT_DATE_EMAIL_BODY");
                emailBody = configData.getConfigValue();
                emailBody = emailBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
                emailBody = emailBody.replace("#amount", String.valueOf(paymentAmountInclusive));
                emailBody = emailBody.replace("#anniversarydate", anniversaryDateStr);

                emailAddress = customer.getEmail();
                fullEmail = emailHeader + emailBody + emailFooter;
                common.sendEmail(emailAddress, emailSubject, fullEmail, "1", "", emailApiUrl, "1");

                contextName = "AT_552_HOUR_SEND_EMAIL_ASKING_CUSTOMER_TO_PAY";
                try {
                    contextValueJsonString = emailAddress.concat(" --- ").concat(emailSubject).concat(" --- ").concat(fullEmail);
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            } catch (Exception e) {
                common.logErrors("api", "RecurrentPaymentController", "send552hrcomms", "Send Email To Notify Customer Of 7 Days To Due Date", e.toString());

                contextName = "AT_552_HOUR_SEND_EMAIL_ASKING_CUSTOMER_TO_PAY_FAILED";
                try {
                    contextValueJsonString = e.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            }

            try {
//              Send SMS
                configData = configDataService.getConfigDataByConfigName("SEVEN_DAYS_TO_PAYMENT_DATE_SMS_BODY");
                smsBody = configData.getConfigValue();
                smsBody = smsBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
                smsBody = smsBody.replace("#amount", String.valueOf(paymentAmountInclusive));
                smsBody = smsBody.replace("#anniversarydate", anniversaryDateStr);
                common.sendSMS(customer.getPhone(), smsBody, smsApiUrl);

                contextName = "AT_552_HOUR_SEND_SMS_ASKING_CUSTOMER_TO_PAY";
                try {
                    contextValueJsonString = customer.getPhone().concat(" --- ").concat(smsBody);
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            } catch (Exception e) {
                common.logErrors("api", "RecurrentPaymentController", "send552hrcomms", "Send SMS To Notify Customer Of 7 Days To Due Date", e.toString());

                contextName = "AT_552_HOUR_SEND_SMS_ASKING_CUSTOMER_TO_PAY_FAILED";
                try {
                    contextValueJsonString = e.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }

            contextName = "AT_552_HOUR_EXECUTE_SUCCESSFUL";
            try {
                contextValueJsonString = "At 552 Hour Execution Successful And Returned SUCCESS";
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return "SUCCESS";

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "send552hrcomms", "Send SMS To Notify Customer Of 7 Days To Due Date", e.toString());

            contextName = "AT_552_HOUR_EXECUTE_FAILED_AND_ERROR_CAUGHT";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return "FAIL";
        }
    }


    @CrossOrigin
    @GetMapping("/sendduedatecomms")
    @ResponseBody
    public String sendDueDateComms(@RequestParam String uniqueSiteId) throws JsonProcessingException {
        common = new Common();
        try {
            configData = new ConfigData();
            installationSite = installationSiteService.getInstallationSiteByUniqueSiteId(uniqueSiteId);
            customerNo = installationSite.getSystemCustomerNo();
            customer = customerService.getCustomerBySystemCustomerNo(customerNo);
            int customerId = customer.getId();
            invoice = new Invoice();

            ConfigData configDataSms = configDataService.getConfigDataByConfigName("SMS_API_URL");
            smsApiUrl = configDataSms.getConfigValue();

            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
            emailApiUrl = configDataEmail.getConfigValue();

            ConfigData configDataBitlyToken = configDataService.getConfigDataByConfigName("BITLY_TOKEN");
            bitlyToken = configDataBitlyToken.getConfigValue();

            ConfigData configDataBitlyUrl = configDataService.getConfigDataByConfigName("BITLY_URL");
            bitlyUrl = configDataBitlyUrl.getConfigValue();

//          B. Get Omni credentials
            configData = configDataService.getConfigDataByConfigName("OMNI_COMPANY_NAME");
            companyName = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("OMNI_U_NAME");
            omniUsername = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("OMNI_USER_PASSWORD");
            omniPassword = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("OMNI_URL");
            omniUrl = configData.getConfigValue();

            hppSiteId = installationSite.getHppSiteId();
            hikFormattedCustomerSiteName = installationSite.getHikFormattedCustomerSiteName();

            packageType = packageTypeService.getPackageType(installationSite.getPackageTypeName());
            paymentAmountInclusive = packageType.getMonthlyCostInclusive();

            contextName = "AT_RECURRING_DUE_DATE_MONTHLY_COST_INCLUSIVE";
            try {
                contextValueJsonString = "Monthly cost inclusive for "+hppSiteId + " = "+String.valueOf(paymentAmount);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            double vat = packageType.getVatRate();
            String firstName = customer.getFirstName();
            String lastName = customer.getLastName();
            String townCity = customer.getTownCity();
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            String email = customer.getEmail();
            phone = customer.getPhone();
            String packageTypeName = installationSite.getPackageTypeName();
            String dpoMarkup = "DSS Monthly Payment";

            String tokenId = UUID.randomUUID().toString().substring(0, 16);

            SiteTimedEvent siteTimedEvent = siteTimedEventService.getSiteNamedEventByUniqueSiteId(uniqueSiteId);
            int monthCounter = siteTimedEvent.getMonthCounter();

            Map<String, String> postInvoiceResponse = postInvoiceToOmniAtDueDate(tokenId, installationSite.getNextPaymentDate());

            long newInvoiceId = saveInvoice(tokenId, paymentAmountInclusive, postInvoiceResponse.get("invoiceNumber"), customerNo, installationSite.getId(), packageType.getId(), postInvoiceResponse.get("invoiceResponse"), postInvoiceResponse.get("narrative"), monthCounter);

            contextName = "AT_DUE_DATE_RETURN_INVOICE_ID";
            try {
                contextValueJsonString = "At Due Date Return Invoice ID: "+newInvoiceId;
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            if (newInvoiceId == -1) {
                contextName = "AT_DUE_DATE_RETURNED_INVOICE_ID_NOT_FOUND";
                try {
                    contextValueJsonString = String.valueOf(newInvoiceId);
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            } else {
                contextName = "AT_DUE_DATE_INVOICE_ID_RETURNED";
                try {
                    contextValueJsonString = String.valueOf(newInvoiceId);
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }

            try {
                configData = configDataService.getConfigDataByConfigName("DPO_EXPIRY_PERIOD");
                int expiryPeriod = Integer.parseInt(configData.getConfigValue());
                configData = configDataService.getConfigDataByConfigName("DPO_REDIRECT_URL");
                String dpoRedirectUrl = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("DPO_BACK_URL");
                String dpoBackUrl = configData.getConfigValue();

                String timestamp = common.getTheTimestamp();
                String dynamicCompanyRef = "RECUR-" + String.valueOf(customerId) + "-" + timestamp;

                configData = configDataService.getConfigDataByConfigName("DSS_COUNTRY");
                String dssCountry = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("DPO_CURRENCY");
                String dpoCurrency = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("DPO_COMPANY_TOKEN");
                String dpoCompanyToken = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("DPO_SERVICE_TYPE");
                String dpoServiceType = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("REDIRECT_URL_HANDOVER_AND_RECURRING");
                String pathURL = configData.getConfigValue();
                pathURL = pathURL.replace("#customerno", customerNo);
                pathURL = pathURL.replace("#tokenid", tokenId);

                String dpoRedirectUrlFull = dpoRedirectUrl + pathURL;

                contextName = "AT_DUE_DATE_PAYMENT_AMOUNT_PASSED_TO_DPO";
                try {
                    contextValueJsonString = "Payment Amount "+String.valueOf(paymentAmountInclusive);
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                dpoPaymentRequest = common.getDPORequest(firstName, lastName, townCity, fullName, email, phone, packageTypeName, paymentAmountInclusive, dpoRedirectUrlFull, dpoBackUrl, vat, dpoMarkup, expiryPeriod, tokenId, customerId, dynamicCompanyRef, dpoCompanyToken, dpoCurrency, dssCountry, dpoServiceType);

                contextName = "AT_DUE_DATE_CREATE_DPO_PAYMENT_REQUEST_OBJECT";
                try {
                    contextValueJsonString = objectMapper.writeValueAsString(dpoPaymentRequest);
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            } catch (Exception e) {
                common.logErrors("api", "RecurrentPaymentController", "sendDueDateComms", "Get DPO Due Date, Redirect URL, And Back URL", e.toString());
                System.out.println("Cannot generate DPO request: " + e.toString());

                contextName = "AT_DUE_DATE_CREATE_DPO_PAYMENT_REQUEST_OBJECT_FAILED";
                try {
                    contextValueJsonString = e.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                return "Cannot generate DPO request: " + e.toString();
            }

            try {
                dpoResponse = dpoService.makeMonthlyPayment(dpoPaymentRequest);
                payToken = dpoResponse.getTransToken();

                contextName = "AT_DUE_DATE_GENERATE_DPO_TOKEN";
                try {
                    contextValueJsonString = payToken;
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            } catch (Exception e) {
                common.logErrors("api", "RecurrentPaymentController", "sendDueDateComms", "Generate Payment Token", e.toString());
                System.out.println("Cannot generate payment token: " + e.toString());
                return "Cannot generate payment token: " + e.toString();
            }

            String paymentURL = "https://secure.3gdirectpay.com/payv3.php?ID=" + payToken;
            configData = configDataService.getConfigDataByConfigName("BITLY_AVAILABILITY");
            String bitlyAvailability = configData.getConfigValue();

            if (bitlyAvailability.equals("AVAILABLE")) {
                shortenedPaymentUrl = common.shortenUrl(paymentURL, bitlyToken, bitlyUrl);
            } else {
                shortenedPaymentUrl = paymentURL;
            }

            PaymentLink paymentLinkResponseObj = savePaymentLinkRecord(customerNo, shortenedPaymentUrl, tokenId, paymentAmountInclusive, newInvoiceId, monthCounter);

            long paymentLinkId = -1;

            if (paymentLinkResponseObj == null) {
                contextName = "AT_DUE_DATE_NULL_PAYMENT_LINK_RECORD_RETURNED";
                paymentLinkId = -1;
                try {
                    contextValueJsonString = payToken;
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            } else {

                if(paymentLinkResponseObj.getId() > 0){
                    paymentLinkId = paymentLinkResponseObj.getId();
                } else {
                    paymentLinkId = -1;
                }
                contextName = "AT_DUE_DATE_PAYMENT_LINK_RECORD_RETURNED";
                try {
                    contextValueJsonString = objectMapper.writeValueAsString(paymentLinkResponseObj);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }

            try {
                Invoice invoice1 = invoiceService.getInvoiceById(newInvoiceId);
                invoice1.setPaymentLinkId(paymentLinkId);
                invoiceService.saveInvoice(invoice1);
            } catch (Exception e){
                common.logErrors("api", "RecurrentPaymentController", "Update Payment Link ID For Invoice Record With ID "+newInvoiceId, "Update Payment Link ID To Invoice Record", e.toString());
            }

            sendPaymentDueEmail(customer, shortenedPaymentUrl, emailApiUrl);

            contextName = "AT_DUE_DATE_SEND_EMAIL_WITH_PAYMENT_LINK";
            try {
                contextValueJsonString = "At Due Date Send Email With Payment Link: "+shortenedPaymentUrl;
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

//            try {
////              Send email asking customer to pay
//                configData = configDataService.getConfigDataByConfigName("PAYMENT_DUE_NOW_EMAIL_SUBJECT");
//                emailSubject = configData.getConfigValue();
//                configData = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
//                emailHeader = configData.getConfigValue();
//                configData = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
//                emailFooter = configData.getConfigValue();
//                configData = configDataService.getConfigDataByConfigName("PAYMENT_DUE_NOW_EMAIL_BODY");
//                emailBody = configData.getConfigValue();
//                emailBody = emailBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
//                emailBody = emailBody.replace("#link", shortenedPaymentUrl);
//                emailAddress = customer.getEmail();
//                fullEmail = emailHeader + emailBody + emailFooter;
//                common.sendEmail(emailAddress, emailSubject, fullEmail, "1", "", emailApiUrl, "1");
//
//                contextName = "AT_DUE_DATE_SEND_EMAIL_TO_CUSTOMER_TO_PAY";
//                try {
//                    contextValueJsonString = emailAddress.concat(" --- ").concat(emailSubject).concat(" --- ").concat(fullEmail);
//                    System.out.println(contextValueJsonString);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//
//            } catch (Exception e){
//                common.logErrors("api", "RecurrentPaymentController", "sendDueDateComms", "Send Due Date Payment Email", e.toString());
//
//                contextName = "AT_DUE_DATE_SEND_EMAIL_TO_CUSTOMER_TO_PAY_FAILED";
//                try {
//                    contextValueJsonString = e.toString();
//                    System.out.println(contextValueJsonString);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//
//                return "sendEmail "+ e.toString();
//            }

            sendPaymentDueSMS(customer, shortenedPaymentUrl, smsApiUrl);

            contextName = "AT_DUE_DATE_SEND_SMS_WITH_PAYMENT_LINK";
            try {
                contextValueJsonString = "At Due Date Send SMS With Payment Link: "+shortenedPaymentUrl;
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

//            try {
////              Send SMS
//                configData = configDataService.getConfigDataByConfigName("PAYMENT_DUE_NOW_SMS_BODY");
//                smsBody = configData.getConfigValue();
//                smsBody = smsBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
//                smsBody = smsBody.replace("#link", shortenedPaymentUrl);
//                common.sendSMS(customer.getPhone(), smsBody, smsApiUrl);
//
//                contextName = "AT_DUE_DATE_SEND_SMS_TO_CUSTOMER_TO_PAY";
//                try {
//                    contextValueJsonString = customer.getPhone().concat(" --- ").concat(smsBody);
//                    System.out.println(contextValueJsonString);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//
//            } catch (Exception e) {
//                common.logErrors("api", "RecurrentPaymentController", "sendDueDateComms", "Send Due Date Payment SMS", e.toString());
//
//                contextName = "AT_DUE_DATE_SEND_SMS_TO_CUSTOMER_TO_PAY_FAILED";
//                try {
//                    contextValueJsonString = e.toString();
//                    System.out.println(contextValueJsonString);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//
//                return "sendSMS " + e.toString();
//            }

            return "SUCCESS";

        } catch (Exception e) {

            contextName = "ERROR_AT_DUE_DATE_COMMS";
            try {
                contextValueJsonString = "At Due Date Return Invoice ID: "+uniqueSiteId;
                System.out.println(contextValueJsonString);
            } catch (Exception Xe) {
                Xe.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            common.logErrors("api", "RecurrentPaymentController", "sendDueDateComms", "Send Due Date Email And SMS", e.toString());
            return "FAIL2 - " + e.toString();
        }
    }

    @CrossOrigin
    @GetMapping("/pay")
    @ResponseBody
    public String pay(@RequestParam String customerNo, @RequestParam String tokenId) {
        common = new Common();
        String invoiceNumber = null;
        String pkgName = null;
        String invoiceRefNo = null;
        String theJobNo = null;
        String jobNo = null;
        String omniAvailability = null;
        int monthCounter = 0;
        String uniqueSiteId = null;
        String dpoToOmniTransactionalNo = null;
        long newInvoiceId = 0;

        try {
            customer = customerService.getCustomerBySystemCustomerNo(customerNo);
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Fetch Customer Object", e.toString());
        }

        // Get Omni credentials
        ConfigData configDataOmniCompanyName = configDataService.getConfigDataByConfigName("OMNI_COMPANY_NAME");
        companyName = configDataOmniCompanyName.getConfigValue();
        ConfigData configDataOmniUserName = configDataService.getConfigDataByConfigName("OMNI_U_NAME");
        omniUsername = configDataOmniUserName.getConfigValue();
        ConfigData configDataOmnuPwdName = configDataService.getConfigDataByConfigName("OMNI_USER_PASSWORD");
        omniPassword = configDataOmnuPwdName.getConfigValue();
        ConfigData configDataOmniUrl = configDataService.getConfigDataByConfigName("OMNI_URL");
        omniUrl = configDataOmniUrl.getConfigValue();

        ConfigData configDataSms = configDataService.getConfigDataByConfigName("SMS_API_URL");
        smsApiUrl = configDataSms.getConfigValue();

        ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
        emailApiUrl = configDataEmail.getConfigValue();

//        try {
//            ConfigData configDataSms = configDataService.getConfigDataByConfigName("SMS_API_URL");
//            smsApiUrl = configDataSms.getConfigValue();
//
//            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
//            emailApiUrl = configDataEmail.getConfigValue();
//        } catch (Exception e) {
//            contextName = "AT_PAY_GET_SMS_AND_EMAIL_API_FAILED";
//            try {
//                contextValueJsonString = e.toString();
//                System.out.println(contextValueJsonString);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//
//            common.logErrors("api", "RecurrentPaymentController", "pay", "Query Email and SMS URLs", e.toString());
//        }

        PaymentLink paymentDetailsFromToken = getPaymeDetailsFromPaymentLink(tokenId);

        paymentLink = paymentLinkService.getPaymentLink(customerNo, tokenId);

        String paymentLinkIdToOmni = String.valueOf(paymentLink.getId());

        if ((paymentLink.getIsUsed()).equals("NO")) {
            if (customer != null && paymentDetailsFromToken != null) {
                // Email sent to customer to notify payment has been received
                sendWeHaveReceivedYourPaymementEmail(emailApiUrl, customer, String.valueOf(paymentDetailsFromToken.getAmount()));
                sendWeHaveReceivedYourPaymementSMS(smsApiUrl, customer, String.valueOf(paymentDetailsFromToken.getAmount()));
                contextName = "AT_PAY_SEND_CUSTOMER_EMAIL_RECEIVED_FUNDS";
                try {
                    contextValueJsonString = "Email sent to " + customer.getFirstName() + " " + customer.getLastName() + " with details " + paymentDetailsFromToken.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                sendWeHaveReceivedYourPaymementEmailToFinanceAndTechLead(emailApiUrl, customer, String.valueOf(paymentDetailsFromToken.getAmount()));

                contextName = "AT_PAY_SEND_RECEIVED_FUNDS_TO_FINANCE_AND_TECHNICAL_LEAD";
                try {
                    contextValueJsonString = "Email sent to Finance and Technical Lead with details " + paymentDetailsFromToken.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }
        }

        ConfigData configDataDepositBankAcc = configDataService.getConfigDataByConfigName("OMNI_DEPOSIT_BANK_ACCOUNT");
        String omniDepositBankAccount = configDataDepositBankAcc.getConfigValue();

        try {
            ConfigData configDataHikEnableUrl = configDataService.getConfigDataByConfigName("HIK_URL_ENABLE");
            hikDeviceEnableUrl = configDataHikEnableUrl.getConfigValue();
        } catch (Exception e) {

        }

        try {
            if (!Objects.isNull(invoiceService.getInvoiceByTokenId(tokenId))) {
                invoice = invoiceService.getInvoiceByTokenId(tokenId);
                newInvoiceId = invoice.getId();
            } else {
                common.logErrors("api", "RecurrentPaymentController", "pay", "Query Invoice No.", "Could Not Get The Invoice Record - (if else block)");

                contextName = "AT_PAY_GET_INVOICE_ID_FAILED";
                try {
                    contextValueJsonString = "The Invoice ID Of The Invoice To Be Paid Against The Payment Link Could Not Be Fetched";
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Query Invoice No.", e.toString());
            contextName = "AT_PAY_GET_INVOICE_ID_FAILED_AND_ERROR_CAUGHT";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }

        try {
            installationSite = installationSiteService.getInstallationSiteBySystemCustomerNo(customerNo);
            uniqueSiteId = installationSite.getUniqueSiteId();
            SiteTimedEvent siteTimedEventMain = siteTimedEventRepository.getSiteTimedEventRecordbySiteID(uniqueSiteId);
            monthCounter = siteTimedEventMain.getMonthCounter();

            siteTimedEventMain.setIsPaid("PAID");
            siteTimedEventService.saveSiteNamedEvent(siteTimedEventMain);

            contextName = "AT_PAY_THE_SITE_TIMED_EVENTS_PAID_STATUS_UPDATED_TO_PAID";
            try {
                contextValueJsonString = objectMapper.writeValueAsString(siteTimedEventMain);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Save SiteNamedEvent Pay Status", e.toString());
        }

        try {
            ConfigData configDataOmniAvailability = configDataService.getConfigDataByConfigName("OMNI_AVAILABILITY");
            omniAvailability = configDataOmniAvailability.getConfigValue();

            todayFormatYYYYMD = LocalDate.now();
            todayFormatDMYYYY = nextDateService.todayDMYYYY();
            LocalDate today = LocalDate.now();

//          Compute next payment date
//            nextPaymentDate = computeNextPaymentDate(installationSite.getHandoverDate().toString());
            nextPaymentDate = installationSite.getHandoverDate().plusDays(30);

            jobNo = lastJobNoService.getLastJobNo();
            LastJobNo lastJobNo = new LastJobNo();
            lastJobNo.setJobNo(jobNo);
            lastJobNoService.saveJobNo(lastJobNo);

            String newTransactionNo = "NOT_YET_ASSIGNED";



            payLinkForBalances = getPayLinkForBalances(customerNo);
            payLinkForBalancesForSMS = getPayLinkForBalancesForSMS(customerNo);

            if ((paymentLink.getIsUsed()).equals("NO")) {
                paymentLink.setIsUsed("YES");
                paymentLinkService.savePaymentLink(paymentLink);


                ConfigData configDataSafeHomeServiceCode = configDataService.getConfigDataByConfigName("SAFE_HOME_SERVICE_CODE");
                String safeHomeServiceCode = configDataSafeHomeServiceCode.getConfigValue();
                ConfigData configDataSafeHomePlusServiceCode = configDataService.getConfigDataByConfigName("SAFE_HOME_PLUS_SERVICE_CODE");
                String safeHomePlusServiceCode = configDataSafeHomePlusServiceCode.getConfigValue();

//              Know the package you are dealing with
                if ((installationSite.getPackageTypeName()).equals("SAFE_HOME")) {
                    pkgName = safeHomeServiceCode;
                } else {
                    pkgName = safeHomePlusServiceCode;
                }

                packageType = packageTypeService.getPackageType(installationSite.getPackageTypeName());
                paymentAmount = packageType.getMonthlyCostInclusive();

                if (omniAvailability.equals("YES")) {
                    try {
                        if (!Objects.isNull(invoice)) {
                            invoiceNumber = invoice.getInvoiceRefNo();
                        } else {
                            try {
                                Invoice invoice1 = invoiceService.getInvoiceByTokenId(tokenId);
                                invoiceNumber = invoice1.getInvoiceRefNo();
                            } catch (Exception e) {
                                common.logErrors("api", "RecurrentPaymentController", "pay", "Fetch Invoice Reference Number", e.toString());
//                              Send failure email
                                String emailSubject = "DSS - Failed To Fetch Invoice Reference Number";
                                String emailBody = "Sorry. We were not able to fetch invoice reference number " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName() + "\n\nCaught Error: " + e.toString();
                                sendFailureEmail(emailSubject, emailBody, emailApiUrl);
                            }
                        }
                    } catch (Exception e) {
                        common.logErrors("api", "RecurrentPaymentController", "pay", "Post Invoice To Omni", e.toString());
//                   Send failure email
                        String emailSubject = "DSS - Failed To Post Invoice To Omni";
                        String emailBody = "Sorry. We were not able to post invoice to Omni " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName() + "\n\nCaught Error: " + e.toString();
                        sendFailureEmail(emailSubject, emailBody, emailApiUrl);
                    }

                    dpoToOmniTransactionalNo = postPaymentFromDPOToOmni(omniDepositBankAccount, todayFormatYYYYMD, customer, packageType, invoiceNumber, companyName, omniUsername, omniPassword, omniUrl, emailApiUrl, paymentLinkIdToOmni);

                    contextName = "AT_PAY_POST_PAYMENT_FROM_DPO_TO_OMNI";
                    try {
                        contextValueJsonString = dpoToOmniTransactionalNo;
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    try {
                        Thread.sleep(6000);
                    } catch (Exception e) {
                    }

//                    newTransactionNo = postSalesJournalEntry(invoiceNumber, customerNo, paymentAmount, todayFormatYYYYMD, companyName, omniUsername, omniPassword, omniUrl);

                    theJobNo = postJobTask(customer, jobNo, todayFormatYYYYMD, companyName, omniUsername, omniPassword, omniUrl, emailApiUrl);

                    contextName = "AT_PAY_POST_JOB_TASK";
                    try {
                        contextValueJsonString = theJobNo;
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                } else {
                    invoiceRefNo = "-1";
                    theJobNo = jobNo;
                    dpoToOmniTransactionalNo = "-1";
                }

                RecurrentPayment recurrentPaymentResponseObj = createRecurrentPaymentRecord(paymentAmount, installationSite, customerNo, dpoToOmniTransactionalNo, theJobNo, today, newTransactionNo);

                if (recurrentPaymentResponseObj == null) {
                    contextName = "AT_PAY_SAVED_AND_RETURNED_RECURRENT_PAYMENT_RECORD_FAILED";
                    try {
                        contextValueJsonString = "Recurrent payment record NOT saved and null value returned";
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                } else {
                    contextName = "AT_PAY_SAVED_AND_RETURNED_RECURRENT_PAYMENT_RECORD";
                    try {
                        contextValueJsonString = objectMapper.writeValueAsString(recurrentPaymentResponseObj);
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                }

                if (newInvoiceId > 0) {

                    invoice.setDatePaid(todayFormatYYYYMD);
                    invoice.setIsPaidOut(1); // Paid
                    invoice.setAllocationTranxNo(newTransactionNo);

                    try {
                        invoiceService.saveInvoice(invoice);
                        contextName = "AT_PAY_UPDATE_INVOICE_TO_PAID";
                        try {
                            contextValueJsonString = objectMapper.writeValueAsString(invoice);
                            System.out.println(contextValueJsonString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    } catch (Exception e) {
                        common.logErrors("api", "RecurrentPaymentController", "pay", "Save Invoice Record", e.toString());
                    }
                }

                hppSiteId = installationSite.getHppSiteId();
                hikFormattedCustomerSiteName = installationSite.getHikFormattedCustomerSiteName();
                uniqueSiteId = installationSite.getUniqueSiteId();

            // Check for outstanding payments
            boolean allPaymentStatus = checkCustomerAllPaymentsDone(customerNo);

            // If no arrears
            if (allPaymentStatus == true){
                // Enable cloud devices at all cost
                // This should be independent of IPRP
                try{
                    ConfigData configDataHikTokenUrl = configDataService.getConfigDataByConfigName("HIK_TOKEN_URL");
                    String url = configDataHikTokenUrl.getConfigValue();
                    ConfigData configDataHikSecretKey = configDataService.getConfigDataByConfigName("HIK_SECRET_KEY");
                    String secretKey = configDataHikSecretKey.getConfigValue();
                    ConfigData configDataHikAppKey = configDataService.getConfigDataByConfigName("HIK_APP_KEY");
                    String appKey = configDataHikAppKey.getConfigValue();
                    ConfigData configDataDeviceEnable = configDataService.getConfigDataByConfigName("DEVICE_ENABLE_AVAILABILITY");
                    String deviceEnableAvailability = configDataDeviceEnable.getConfigValue();

                    String token = common.getHikToken(appKey, secretKey, url);
                    HikEvent enableDeviceResponse = null;

                    if(deviceEnableAvailability.equals("YES")){
//                  Enable devices
                        enableDeviceResponse = enableDevice(hppSiteId, uniqueSiteId, customer.getSystemCustomerNo(), hikFormattedCustomerSiteName, hikDeviceEnableUrl, emailApiUrl, token);
                    }

                    contextName = "AT_PAY_ENABLE_DEVICE";
                    try {
                        if(deviceEnableAvailability.equals("YES")){
                            contextValueJsonString = objectMapper.writeValueAsString(enableDeviceResponse);
                        } else {
                            contextValueJsonString = "{\n" +
                                    "  \"status\": \"Device cloud enable API is not available\"\n" +
                                    "}";
                        }
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                } catch (Exception e){
                    common.logErrors("api", "RecurrentPaymentController", "pay", "Enable Cloud Devices", e.toString());

                    contextName = "AT_PAY_FAILED_TO_ENABLE_CLOUD_DEVICES";
                    try {
                        contextValueJsonString = e.toString();
                        System.out.println(contextValueJsonString);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                }

                // Check connection
                boolean iprpConnStatus = checkIPRPConnectionStatus(hppSiteId);

                // If IPRP connection status is NOT active / NOT connected
                if(iprpConnStatus == false){
                    // Enable/Connect IPRP
                    String enableResponse = enableIPRP(customer, installationSite);

                    contextName = "AT_PAY_IPRP_CONNECTED";
                    try {
                        contextValueJsonString = "enableResponse " +enableResponse+" -- Customer No. -- " +customer.getSystemCustomerNo()+" -- Site Name "+installationSite.getHikFormattedCustomerSiteName();
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    if(enableResponse.equals("SUCCESS")) {
                        // Check if payment is against handover (first month or other months)
                        // If payment is first month do not send reconnection email to customer and SMS to customer

                        String customerName = customer.getFirstName()+" "+customer.getLastName();

//                        if ((installationSite.getInstallationStatus()).equals("DISABLED")) {
////                  Send Account Restored Email to Customer
//                            sendAccountRestoredCommsToCustomer(customer, emailApiUrl, smsApiUrl, nextPaymentDate.toString());
//                        }

                        if(monthCounter != 1){
                            // Send reconnection email to customer
                            sendSiteReconnectionEmailToCustomer(customer.getEmail(), customerName, emailApiUrl);
                            sendSiteReconnectionSMSToCustomer(customer.getPhone(), customerName, smsApiUrl);
                        }

                        // Send reconnection email
                        sendSiteEnableEmailToFinance(installationSite.getHikFormattedCustomerSiteName(), emailApiUrl);
                        sendSiteEnableEmailToTechnicalLead(installationSite.getHikFormattedCustomerSiteName(), emailApiUrl);

                        contextName = "AT_PAY_RECONNECTION_SEND_EMAILS";
                        try {
                            contextValueJsonString = "Reconnection emails sent with relation to "+customer.getSystemCustomerNo();
                            System.out.println(contextValueJsonString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    } else {
//                      Send failure email
                        String emailSubject = "DSS - At Pay Failed To Enable IPRP";
                        String emailBody = "Sorry. Failed to enable IPRP after payment for customer "+customer.getFirstName()+" "+customer.getLastName()+" with customer no. "+customer.getSystemCustomerNo();
                        sendFailureEmail(emailSubject, emailBody, emailApiUrl);
                    }
                } else{
                    // No balance and NOT disconnected so nothing needs to happen
                    contextName = "AT_PAY_NO_BALANCE_AND_NOT_DISCONNECTED_NOTHING_HAPPENS";
                    try {
                        contextValueJsonString = "No balance and NOT disconnected so nothing happens for customer "+customer.getSystemCustomerNo();
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                }

            } else{
                boolean iprpConnStatus = checkIPRPConnectionStatus(hppSiteId);

                // If connected
                if(iprpConnStatus == true){
                    // Disconnect IPRP
                    String disableIPRPStatus = disableIPRP(customer, installationSite);

                    contextName = "AT_PAY_IPRP_DISCONNECTED";
                    try {
                        contextValueJsonString = "disableIPRPStatus " +disableIPRPStatus+" -- Customer No. -- " +customer.getSystemCustomerNo()+" -- Site Name "+installationSite.getHikFormattedCustomerSiteName();
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    if(disableIPRPStatus.equals("SUCCESS")) {
                        // Send disconnect email
                        String customerName = customer.getFirstName()+" "+customer.getLastName();
                        sendSiteDisableEmailToFinance(installationSite.getHikFormattedCustomerSiteName(), emailApiUrl);
                        sendSiteDisableEmailToTechnicalLead(installationSite.getHikFormattedCustomerSiteName(), emailApiUrl);
                        sendSiteDisconnectionEmailToCustomer(customer.getEmail(), customerName, payLinkForBalances, emailApiUrl);
                        sendSiteDisconnectionSMSToCustomer(customer.getPhone(), customerName, payLinkForBalancesForSMS, smsApiUrl);
                    } else {
//                      Send failure email
                        String emailSubject = "DSS - At Pay Failed disable IPRP";
                        String emailBody = "Sorry. Failed to disable IPRP after payment for customer "+customer.getFirstName()+" "+customer.getLastName()+" and customer no. "+customer.getSystemCustomerNo();
                        sendFailureEmail(emailSubject, emailBody, emailApiUrl);
                    }
                } else {
                    //Send email to  notify account still disconnected
                    sendEmailAccountStillDisconnected(emailApiUrl, customer);
                    sendSMSAccountStillDisconnected(smsApiUrl, customer);
                }
            }

//              Send Payment Confirmation to Customer
//                sendPaymentConfirmationCommsCustomer(customer, emailApiUrl, smsApiUrl);

//                if ((installationSite.getInstallationStatus()).equals("ACTIVE")) {
////               Send Payment Received Email to Customer Before Account is Disabled
//                    sendPaymentCommsToCustomerBeforeAccountIsDisabled(customer, emailApiUrl, smsApiUrl);
//                }

                try {
                    installationSite.setLastPaymentDate(today);
                    installationSite.setInstallationStatus("ACTIVE");
                    installationSite.setNextPaymentDate(nextPaymentDate);
                    installationSiteService.saveInstallationSite(installationSite);
                } catch (Exception e) {
                    common.logErrors("api", "RecurrentPaymentController", "pay", "Save InstallationSite Data", e.toString());
                }

                if (!Objects.isNull(newMonthService.getNewMonthByUniqueSiteId(uniqueSiteId))) {
                    newMonth = newMonthService.getNewMonthByUniqueSiteId(uniqueSiteId);
                    newMonth.setMonthCounterPaid(monthCounter);
                } else {
                    newMonth = new NewMonth();
                    newMonth.setUniqueSiteId(uniqueSiteId);
                    newMonth.setMonthCounterPaid(monthCounter);
                }

                newMonthService.saveNewMonth(newMonth);

                return "SUCCESS";

            } else {
                return "USED";
            }
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Pay Activities", e.toString());
            return "FAIL " + e.toString() + "\n\nemailApiUrl -- "+emailApiUrl;
        }
    }


    public void sendEmailAccountStillDisconnected(String emailApiUrl, Customer customer){
        common = new Common();
        try {
            ConfigData configDataAccStillDisconnectedHeader = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
            String emailHeader = configDataAccStillDisconnectedHeader.getConfigValue();
            ConfigData configDataAccStillDisconnectedFooter = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
            String emailFooter = configDataAccStillDisconnectedFooter.getConfigValue();
            ConfigData configDataAccStillDisconnectedSubject = configDataService.getConfigDataByConfigName("ACCOUNT_STILL_DISCONNECTED_EMAIL_SUBJECT");
            String emailSubject = configDataAccStillDisconnectedSubject.getConfigValue();
            ConfigData configDataAccStillDisconnectedBody = configDataService.getConfigDataByConfigName("ACCOUNT_STILL_DISCONNECTED_EMAIL_BODY");
            String emailBody = configDataAccStillDisconnectedBody.getConfigValue();

            emailBody = emailBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
            String fullEmail = emailHeader + emailBody + emailFooter;
            common.sendEmail(customer.getEmail(), emailSubject, fullEmail, "1", "", emailApiUrl, "1");
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send Email to Customer For Account Still Disconnected", e.toString());
        }

    }

    public void sendSMSAccountStillDisconnected(String smsApiUrl, Customer customer){
        common = new Common();
        try {
//          Send SMS to customer
            ConfigData configDataSMS = configDataService.getConfigDataByConfigName("ACCOUNT_STILL_DISCONNECTED_SMS_BODY");
            String smsBody = configDataSMS.getConfigValue();
            smsBody = smsBody.replace("#customername", customer.getFirstName()+" "+customer.getLastName());
            common.sendSMS(phone, smsBody, smsApiUrl);

            contextName = "AT_PAY_SEND_ACCOUNT_STILL_DISCONNECTED_SMS";
            try {
                contextValueJsonString = phone.concat(" --- ").concat(smsBody);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send SMS To Customer For Account Still Disconnected", e.toString());
            contextName = "AT_PAY_SEND_ACCOUNT_STILL_DISCONNECTED_SMS";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }

    public String enableIPRP(Customer customer, InstallationSite installationSite){
        common = new Common();
//      Enable IPRP
        try {
            ConfigData configDataUrl = configDataService.getConfigDataByConfigName("HIK_IPRP_STATUS_UPDATE_URL_PUBLIC");
//          ConfigData configDataUrl = configDataService.getConfigDataByConfigName("HIK_IPRP_STATUS_UPDATE_URL_LOCAL");
            String iprpUrl = configDataUrl.getConfigValue();
            ConfigData configDataHikIprpUsername = configDataService.getConfigDataByConfigName("HIK_IPRP_USERNAME");
            String hikIprpUsername = configDataHikIprpUsername.getConfigValue();
            ConfigData configDataHikIprpPwd = configDataService.getConfigDataByConfigName("HIK_IPRP_PASSWORD");
            String hikIprpPawd = configDataHikIprpPwd.getConfigValue();

            IPRPData iprpData = iprpDataService.getIPRPDataBySiteId(installationSite.getHppSiteId());

            if (!Objects.isNull(iprpData)) {
                String iprpstatus = iprpData.getActiveStatus();

                if (!iprpstatus.equals("active")) {
                    String accountID = iprpData.getAccountId();
                    String deviceName = iprpData.getDevName();
                    String deviceIndex = iprpData.getDevIndex();
                    String deviceSerial = iprpData.getDevSerial();
                    String customerName = customer.getFirstName() + " " + customer.getLastName();
                    String status = "active";

                    contextName = "AT_PAY_SET_IPRP_STATUS_TO_ACTIVE";
                    try {
                        contextValueJsonString = "accountID " +accountID+" -- deviceName "+deviceName+" -- deviceIndex "+deviceIndex+" -- deviceSerial "+deviceSerial + " -- customerName "+customerName+" -- status "+status;
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    JsonNode iprpResponseJson = null;

                    try {
                        iprpResponseJson = common.changeIPRPStatus(accountID, deviceName, deviceIndex, status, hikIprpUsername, hikIprpPawd, iprpUrl);
                        iprpData.setActiveStatus(status);
                        iprpDataService.saveIPRPData(iprpData);

                        contextName = "AT_PAY_SAVING_IPRP_DETAILS";
                        try {
                            contextValueJsonString = "Customer No. "+customer.getSystemCustomerNo()+" -- iprpResponseJson " +iprpResponseJson.toString()+" -- iprp  "+objectMapper.writeValueAsString(iprpData);
                            System.out.println(contextValueJsonString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    } catch (Exception e) {
                        common.logErrors("api", "RecurrentPaymentController", "pay", "Failed To Enable IPRP", e.toString());
//                      Send failure email
                        ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
                        String emailApiUrl = configDataEmail.getConfigValue();
                        String emailSubject = "DSS - Failed To Enable IPRP";
                        String emailBody = "Sorry. IPRP could not be enabled for " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName();
//                      String emailSubject = "SGA DSS - Failed To Enable IPRP";
//                      String emailBody = "Sorry. IPRP could not be enabled for Device Serial Number: " + deviceSerial + " for customer " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName();
                        sendFailureEmail(emailSubject, emailBody, emailApiUrl);

                        contextName = "AT_PAY_FAILED_TO_SAVE_IPRP_DETAILS";
                        try {
                            contextValueJsonString = e.toString();
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }

                    try {
                        hikEvent = new HikEvent();
                        hikEvent.setSystemCustomerNo(customer.getSystemCustomerNo());
                        hikEvent.setHppSiteId(installationSite.getHppSiteId());
                        hikEvent.setUniqueSiteId(installationSite.getUniqueSiteId());
                        hikEvent.setHikFormattedCustomerSiteName(installationSite.getHikFormattedCustomerSiteName());
                        hikEvent.setIprpResponse(iprpResponseJson.toString());
                        hikEvent.setEventType("ENABLE");
                        hikEventService.saveHikEvent(hikEvent);
                    } catch (Exception e) {
                        common.logErrors("api", "RecurrentPaymentController", "pay", "Save HIK Event", e.toString());
                        contextName = "AT_PAY_FAILED_TO_SAVE_HIK_EVENT";
                        try {
                            contextValueJsonString = objectMapper.writeValueAsString(hikEvent);
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }

//                                try {
////                                  Send email to Technical Lead and Finance
//                                    configData = configDataService.getConfigDataByConfigName("TECHNICAL_LEAD_EMAIL");
//                                    String techLeadEmail = configData.getConfigValue();
//                                    configData = configDataService.getConfigDataByConfigName("OMNI_FINANCE_EMAIL");
//                                    String financeEmail = configData.getConfigValue();
//                                    configData = configDataService.getConfigDataByConfigName("SITE_ENABLE_EMAIL_SUBJECT");
//                                    emailSubject = configData.getConfigValue();
//                                    configData = configDataService.getConfigDataByConfigName("SITE_ENABLE_EMAIL_BODY");
//                                    emailBody = configData.getConfigValue();
//                                    emailBody = emailBody.replace("#sitename", installationSite.getHikFormattedCustomerSiteName());
//                                    emailBody = emailBody.replace("#deviceserial", deviceSerial);
//                                    emailBody = emailBody.replace("#customerno", customerNo);
//                                    emailBody = emailBody.replace("#customername", customerName);
//                                    emailBody = emailBody.replace("#date", LocalDateTime.now().toString());
//                                    emailBody = emailBody.replace("#typeofevent", "ENABLE");
//                                    common.sendEmail(techLeadEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");
//                                    common.sendEmail(financeEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");
//                                } catch (Exception e) {
//                                    common.logErrors("api", "RecurrentPaymentController", "pay", "Send Email To Technician", e.toString());
//                                }
                }
            }
            return "SUCCESS";
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Enable Devices", e.toString());
            return "FAIL";
        }
    }

    public String disableIPRP(Customer customer, InstallationSite installationSite){
        common = new Common();
//      Disable IPRP
        try {
            ConfigData configDataUrl = configDataService.getConfigDataByConfigName("HIK_IPRP_STATUS_UPDATE_URL_PUBLIC");
//          ConfigData configDataUrl = configDataService.getConfigDataByConfigName("HIK_IPRP_STATUS_UPDATE_URL_LOCAL");
            String iprpUrl = configDataUrl.getConfigValue();
            ConfigData configDataHikIprpUsername = configDataService.getConfigDataByConfigName("HIK_IPRP_USERNAME");
            String hikIprpUsername = configDataHikIprpUsername.getConfigValue();
            ConfigData configDataHikIprpPwd = configDataService.getConfigDataByConfigName("HIK_IPRP_PASSWORD");
            String hikIprpPawd = configDataHikIprpPwd.getConfigValue();

            IPRPData iprpData = iprpDataService.getIPRPDataBySiteId(installationSite.getHppSiteId());

            if (!Objects.isNull(iprpData)) {
                String iprpstatus = iprpData.getActiveStatus();

                if (iprpstatus.equals("active")) {
                    String accountID = iprpData.getAccountId();
                    String deviceName = iprpData.getDevName();
                    String deviceIndex = iprpData.getDevIndex();
                    String deviceSerial = iprpData.getDevSerial();
                    String customerName = customer.getFirstName() + " " + customer.getLastName();
                    String status = "inactive";

                    contextName = "AT_PAY_SET_IPRP_STATUS_TO_INACTIVE";
                    try {
                        contextValueJsonString = "accountID " +accountID+" -- deviceName "+deviceName+" -- deviceIndex "+deviceIndex+" -- deviceSerial "+deviceSerial + " -- customerName "+customerName+" -- status "+status;
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    JsonNode iprpResponseJson = null;

                    try {
                        iprpResponseJson = common.changeIPRPStatus(accountID, deviceName, deviceIndex, status, hikIprpUsername, hikIprpPawd, iprpUrl);
                        iprpData.setActiveStatus(status);
                        iprpDataService.saveIPRPData(iprpData);

                        contextName = "AT_PAY_SAVING_IPRP_DETAILS";
                        try {
                            contextValueJsonString = "Customer No. "+customer.getSystemCustomerNo()+" -- iprpResponseJson " +iprpResponseJson.toString()+" -- iprp  "+objectMapper.writeValueAsString(iprpData);
                            System.out.println(contextValueJsonString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                    } catch (Exception e) {
                        common.logErrors("api", "RecurrentPaymentController", "pay", "Failed To Disable IPRP", e.toString());
//                      Send failure email
                        ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
                        String emailApiUrl = configDataEmail.getConfigValue();
                        String emailSubject = "DSS - Failed To Disable IPRP";
                        String emailBody = "Sorry. IPRP could not be disabled for " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName();
//                      String emailSubject = "SGA DSS - Failed To Enable IPRP";
//                      String emailBody = "Sorry. IPRP could not be enabled for Device Serial Number: " + deviceSerial + " for customer " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName();
                        sendFailureEmail(emailSubject, emailBody, emailApiUrl);

                        contextName = "AT_PAY_FAILED_TO_SAVE_IPRP_DETAILS";
                        try {
                            contextValueJsonString = e.toString();
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }

                    try {
                        hikEvent = new HikEvent();
                        hikEvent.setSystemCustomerNo(customer.getSystemCustomerNo());
                        hikEvent.setHppSiteId(installationSite.getHppSiteId());
                        hikEvent.setUniqueSiteId(installationSite.getUniqueSiteId());
                        hikEvent.setHikFormattedCustomerSiteName(installationSite.getHikFormattedCustomerSiteName());
                        hikEvent.setIprpResponse(iprpResponseJson.toString());
                        hikEvent.setEventType("DISABLE");
                        hikEventService.saveHikEvent(hikEvent);
                    } catch (Exception e) {
                        common.logErrors("api", "RecurrentPaymentController", "pay", "Save HIK Event", e.toString());
                        contextName = "AT_PAY_FAILED_TO_SAVE_HIK_EVENT";
                        try {
                            contextValueJsonString = objectMapper.writeValueAsString(hikEvent);
                            System.out.println(contextValueJsonString);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                    }
                }
            }
            return "SUCCESS";
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Enable Devices", e.toString());
            return "FAIL";
        }
    }

    public boolean checkIPRPConnectionStatus(String hppSiteId){
        IPRPData iprpData = iprpDataService.getIPRPDataBySiteId(hppSiteId);
        String connStatus = iprpData.getActiveStatus();
        if(connStatus.equals("active")){
            return true;
        } else {
            return false;
        }
    }

    public String postSalesJournalEntry(String invoiceNumber, String customerNo, double paymentAmount, LocalDate todayFormatYYYYMD, String companyName, String omniUsername, String omniPassword, String omniUrl){

        String newTransactionNo = "";

        String requestBodyJsonSalesJournalEntry = "{\n" +
                "  \"salesjournalentry\": {\n" +
                "    \"transaction_type\": \"Settlement Discount\",\n" +
                "    \"debtor_code\": \"" + customerNo + "\",\n" +
                "    \"debtor_branch_code\": \"HO\",\n" +
                "    \"transaction_date\": \"" + todayFormatYYYYMD + "\",\n" +
                "    \"reference\": \"" + invoiceNumber + "\",\n" +
                "    \"inclusive_amount\": " + paymentAmount + "\n" +
                "  }\n" +
                "}";

        contextName = "AT_PAY_SALES_JOURNAL_ENTRY_JSON_REQUEST_BODY";
        try {
            contextValueJsonString = requestBodyJsonSalesJournalEntry;
            System.out.println(contextValueJsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

//        newTransactionNo = salesJournalEntry(omniUrl, companyName, omniUsername, omniPassword, requestBodyJsonSalesJournalEntry);

        contextName = "AT_PAY_SALES_JOURNAL_ENTRY";
        try {
            contextValueJsonString = newTransactionNo;
            System.out.println(contextValueJsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        return newTransactionNo;
    }

    public void sendWeHaveReceivedYourPaymementEmail(String emailApiUrl, Customer customer, String amount){
        common = new Common();
        try {
            ConfigData configDataWeHaveReceivedYourPaymentHeader = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
            String emailHeader = configDataWeHaveReceivedYourPaymentHeader.getConfigValue();
            ConfigData configDataWeHaveReceivedYourPaymentFooter = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
            String emailFooter = configDataWeHaveReceivedYourPaymentFooter.getConfigValue();
            ConfigData configDataWeHaveReceivedYourPaymentSubject = configDataService.getConfigDataByConfigName("WE_HAVE_RECEIVED_YOUR_PAYMENT_EMAIL_SUBJECT");
            String emailSubject = configDataWeHaveReceivedYourPaymentSubject.getConfigValue();
            ConfigData configDataWeHaveReceivedYourPaymentBody = configDataService.getConfigDataByConfigName("WE_HAVE_RECEIVED_YOUR_PAYMENT_EMAIL_BODY");
            String emailBody = configDataWeHaveReceivedYourPaymentBody.getConfigValue();

            emailBody = emailBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
            emailBody = emailBody.replace("#amount", amount);
            String fullEmail = emailHeader + emailBody + emailFooter;
            common.sendEmail(customer.getEmail(), emailSubject, fullEmail, "1", "", emailApiUrl, "1");

            contextName = "AT_PAY_SEND_WE_HAVE_RECEIVED_YOUR_PAYMENT_EMAIL";
            try {
                contextValueJsonString =customer.getEmail().concat(" -- "+emailSubject).concat(" -- "+emailBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e){
            contextName = "AT_PAY_FAILED_TO_SEND_WE_HAVE_RECEIVED_YOUR_PAYMENT_EMAIL";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }

    public void sendWeHaveReceivedYourPaymementSMS(String smsApiUrl, Customer customer, String amount) {
        common = new Common();
        try {
//          Send SMS to customer
            ConfigData configDataSMS = configDataService.getConfigDataByConfigName("WE_HAVE_RECEIVED_YOUR_PAYMENT_SMS_BODY");
            String smsBody = configDataSMS.getConfigValue();
            smsBody = smsBody.replace("#customername", customer.getFirstName()+" "+customer.getLastName());
            smsBody = smsBody.replace("#amount", amount);
            common.sendSMS(customer.getPhone(), smsBody, smsApiUrl);

            contextName = "AT_PAY_SEND_WE_HAVE_RECEIVED_YOUR_PAYMENT_SMS";
            try {
                contextValueJsonString = customer.getPhone().concat(" --- ").concat(smsBody);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send SMS To Customer", e.toString());
            contextName = "AT_PAY_SEND_WE_HAVE_RECEIVED_YOUR_PAYMENT_SMS";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }

    ////////////////// ENABLE EMAILS
    public void sendSiteEnableEmailToFinance(String siteName, String emailApiUrl) {
        common = new Common();
        try {
//          Send email to Finance
            ConfigData configDataFinance = configDataService.getConfigDataByConfigName("OMNI_FINANCE_EMAIL");
            String financeEmail = configDataFinance.getConfigValue();
            ConfigData configDataSiteEnableEmailSubject = configDataService.getConfigDataByConfigName("SITE_ENABLE_EMAIL_SUBJECT");
            String emailSubject = configDataSiteEnableEmailSubject.getConfigValue();
            ConfigData configDataSiteEnableEmailBody = configDataService.getConfigDataByConfigName("SITE_ENABLE_EMAIL_BODY");
            String emailBody = configDataSiteEnableEmailBody.getConfigValue();
            emailBody = emailBody.replace("#sitename", siteName);
            common.sendEmail(financeEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");

            contextName = "AT_PAY_SEND_SITE_ENABLE_EMAIL_TO_FINANCE";
            try {
                contextValueJsonString =financeEmail.concat(" -- "+emailSubject).concat(" -- "+emailBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send Email To Finance", e.toString());

            contextName = "AT_PAY_FAILED_TO_SEND_SITE_ENABLE_EMAIL_TO_FINANCE";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        }
    }

    public void sendSiteEnableEmailToTechnicalLead(String siteName, String emailApiUrl) {
        common = new Common();
        try {
//          Send email to Technical Lead
            ConfigData configDataTechLeadEmail = configDataService.getConfigDataByConfigName("TECHNICAL_LEAD_EMAIL");
            String techLeadEmail = configDataTechLeadEmail.getConfigValue();
            ConfigData configDataEnableSiteEmailSubject = configDataService.getConfigDataByConfigName("SITE_ENABLE_EMAIL_SUBJECT");
            String emailSubject = configDataEnableSiteEmailSubject.getConfigValue();
            ConfigData configDataSiteEmailBody = configDataService.getConfigDataByConfigName("SITE_ENABLE_EMAIL_BODY");
            String emailBody = configDataSiteEmailBody.getConfigValue();
            emailBody = emailBody.replace("#sitename", siteName);
            common.sendEmail(techLeadEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");

            contextName = "AT_PAY_SEND_SITE_ENABLE_EMAIL_TO_TECHNICAL_LEAD";
            try {
                contextValueJsonString = techLeadEmail.concat(" -- "+emailSubject).concat(" -- "+emailBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send Email To Technical Lead", e.toString());

            contextName = "AT_PAY_FAILED_TO_SEND_SITE_ENABLE_EMAIL_TO_TECH_LEAD";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        }
    }

    public void sendSiteReconnectionEmailToCustomer(String customerEmail, String customerName, String emailApiUrl) {
        common = new Common();
        try {
//          Send email to Technical Lead
            configData = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
            emailHeader = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
            emailFooter = configData.getConfigValue();
            ConfigData configDataEnableSiteEmailSubject = configDataService.getConfigDataByConfigName("CUSTOMER_SITE_RECONNECT_EMAIL_SUBJECT");
            String emailSubject = configDataEnableSiteEmailSubject.getConfigValue();
            ConfigData configDataSiteEmailBody = configDataService.getConfigDataByConfigName("CUSTOMER_SITE_RECONNECT_EMAIL_BODY");
            String emailBody = configDataSiteEmailBody.getConfigValue();
            emailBody = emailBody.replace("#customername", customerName);
            fullEmail = emailHeader+emailBody+emailFooter;
            common.sendEmail(customerEmail, emailSubject, fullEmail, "1", "", emailApiUrl, "1");

            contextName = "AT_PAY_SEND_SITE_RECONNECTION_EMAIL_TO_CUSTOMER";
            try {
                contextValueJsonString = customerEmail.concat(" -- "+emailSubject).concat(" -- "+emailBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send Email To Customer", e.toString());

            contextName = "AT_PAY_FAILED_TO_SEND_SITE_RECONNECTION_EMAIL_TO_CUSTOMER";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        }
    }


public void sendSiteReconnectionSMSToCustomer(String phone, String customerName, String smsApiUrl) {
    common = new Common();
    try {
//          Send SMS to customer
        ConfigData configDataSMS = configDataService.getConfigDataByConfigName("CUSTOMER_SITE_RECONNECT_SMS_BODY");
        String smsBody = configDataSMS.getConfigValue();
        smsBody = smsBody.replace("#customername", customerName);
        common.sendSMS(phone, smsBody, smsApiUrl);

        contextName = "AT_PAY_SEND_CUSTOMER_SITE_RECONNECTION_SMS";
        try {
            contextValueJsonString = phone.concat(" --- ").concat(smsBody);
            System.out.println(contextValueJsonString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        captureAuditTrail(contextName, contextDesc, contextValueJsonString);

    } catch (Exception e) {
        common.logErrors("api", "RecurrentPaymentController", "pay", "Send SMS To Customer", e.toString());
        contextName = "AT_PAY_SEND_SITE_RECONNECTION_SMS_TO_CUSTOMER";
        try {
            contextValueJsonString = e.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
    }
}

    ////////////////// DISABLE EMAILS
    public void sendSiteDisableEmailToFinance(String siteName, String emailApiUrl) {
        common = new Common();
        try {
//          Send email to Finance
            configData = configDataService.getConfigDataByConfigName("OMNI_FINANCE_EMAIL");
            String financeEmail = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("SITE_DISABLE_EMAIL_SUBJECT");
            emailSubject = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("SITE_DISABLE_EMAIL_BODY");
            emailBody = configData.getConfigValue();
            emailBody = emailBody.replace("#sitename", siteName);
            common.sendEmail(financeEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");

            contextName = "AT_PAY_SEND_SITE_DISABLE_EMAIL_TO_FINANCE";
            try {
                contextValueJsonString = financeEmail.concat(" -- "+emailSubject).concat(" -- "+emailBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send Email To Finance", e.toString());

            contextName = "AT_PAY_SEND_SITE_DISABLE_EMAIL_TO_FINANCE";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }

    public void sendSiteDisableEmailToTechnicalLead(String siteName, String emailApiUrl) {
        common = new Common();
        try {
//          Send email to Technical Lead
            configData = configDataService.getConfigDataByConfigName("TECHNICAL_LEAD_EMAIL");
            String techLeadEmail = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("SITE_DISABLE_EMAIL_SUBJECT");
            emailSubject = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("SITE_DISABLE_EMAIL_BODY");
            emailBody = configData.getConfigValue();
            emailBody = emailBody.replace("#sitename", siteName);
            common.sendEmail(techLeadEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");

            contextName = "AT_PAY_SEND_SITE_DISABLE_EMAIL_TO_TECHNICAL_LEAD";
            try {
                contextValueJsonString = techLeadEmail.concat(" -- "+emailSubject).concat(" -- "+emailBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send Email To Technical Lead", e.toString());
            contextName = "AT_PAY_SEND_SITE_DISABLE_EMAIL_TO_TECHNICAL_LEAD";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }

    public void sendSiteDisconnectionEmailToCustomer(String email, String customerName, String paymentLink, String emailApiUrl) {
        common = new Common();
        try {
//          Send email to customer
            configData = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
            emailHeader = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
            emailFooter = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("ACCOUNT_SUSPENSION_EMAIL_SUBJECT");
            emailSubject = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("ACCOUNT_SUSPENSION_EMAIL_BODY");
            emailBody = configData.getConfigValue();
            emailBody = emailBody.replace("#customername", customerName);
            emailBody = emailBody.replace("#link", paymentLink);
            fullEmail = emailHeader + emailBody + emailFooter;
            common.sendEmail(email, emailSubject, fullEmail, "1", "", emailApiUrl, "1");

            contextName = "AT_PAY_SEND_SITE_DISCONNECT_EMAIL_TO_CUSTOMER";
            try {
                contextValueJsonString = email.concat(" -- "+emailSubject).concat(" -- "+emailBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send Email To Customer", e.toString());
            contextName = "AT_PAY_SEND_SITE_DISABLE_EMAIL_TO_CUSTOMER";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }

    public void sendSiteDisconnectionSMSToCustomer(String phone, String customerName, String paymentLink, String smsApiUrl) {
        common = new Common();
        try {
//          Send SMS to customer
            ConfigData configDataSMS = configDataService.getConfigDataByConfigName("ACCOUNT_SUSPENSION_SMS_BODY");
            String smsBody = configDataSMS.getConfigValue();
            smsBody = smsBody.replace("#customername", customerName);
            smsBody = smsBody.replace("#link", paymentLink);
            common.sendSMS(phone, smsBody, smsApiUrl);

            contextName = "AT_PAY_SEND_CUSTOMER_SITE_DISCONNECTION_SMS";
            try {
                contextValueJsonString = phone.concat(" --- ").concat(smsBody);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send SMS To Customer", e.toString());
            contextName = "AT_PAY_SEND_SITE_DISCONNECTION_SMS_TO_CUSTOMER";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }

    public String postingInvoiceToOmni(String domain, String userName, String password, String companyName, String postFields) {
        try {
            String url = domain + "/Invoice/?UserName=" + userName + "&Password=" + password + "&CompanyName=" + companyName;
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
            common.logErrors("api", "RecurrentPaymentController", "postingInvoiceToOmni", "Post Invoice To Omni", e.toString());
            return "INVOICE_ERROR: " + e.toString();
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
            common.logErrors("api", "RecurrentPaymentController", "getInvoiceResponse", "Get Invoice Response", e.toString());
            return null;
        }
    }

    public String postPaymentFromDPOToOmni(String omniDepositBankAccount, LocalDate todayFormatYYYYMD, Customer customer, PackageType packageType, String invoiceNumber, String companyName, String omniUsername, String omniPassword, String omniUrl, String emailApiUrl, String paymentLinkIdToOmni) {
//         1. POSTING PAYMENT(RECURRENT) FROM DPO TO OMNI
//         A. Prepare json request data
        try {
            requestBodyJson = "{ \"bank_transaction\" : { \n" +
                    "\"bank_account\" : \"" + omniDepositBankAccount + "\",\n" +
                    "\"transaction_date\" : \"" + todayFormatYYYYMD + "\",\n" +
                    "\"transaction_category\" : \"Receipt\",\n" +
                    "\"transaction_type\" : \"Cash\",\n" +
                    "\"amount\" : " + packageType.getMonthlyCostInclusive() + ",\n" +
                    "\"reference\" : \"" + paymentLinkIdToOmni + "\",\n" +
                    "\"payee\" : \"" + customer.getFirstName() + " " + customer.getLastName() + "\",\n" +
                    "\"narrative\" : \"" + packageType.getPackageTypeName() + " cost\",\n" +
                    "\"target_lines\" : [  { \n" +
                    "\"detail_reference\" : \"" + customer.getSystemCustomerNo() + "\",\n" +
                    "\"detail_narrative\" : \"" + packageType.getPackageTypeName() + " cost\",\n" +
                    "\"statement_description\" : \"\",\n" +
                    "\"dr_amount\" : 0,\n" +
                    "\"cr_amount\" : " + packageType.getMonthlyCostInclusive() + ",\n" +
                    "\"vat_code\" : \"Z\",\n" +
                    "\"vat_amount\" : 0,\n" +
                    "\"target_ledger\" : \"Customer\",\n" +
                    "\"target_ledger_account\" : \"" + customer.getSystemCustomerNo() + "\",\n" +
                    "\"target_ledger_amount\" : -" + packageType.getMonthlyCostInclusive() + ",\n" +
                    "\"pre_allocations\" : [{\"module\" : \"Invoice\" ,  \"reference\" : \"" + invoiceNumber + "\"}] }]}}";
        }
       catch (Exception e){
           common.logErrors("api", "RecurrentPaymentController", "pay", "Post Payment (Monthly) from DPO to Omni", e.toString());
//          Send failure email
           String emailSubject = "DSS - Failed To Create Payment (Monthly) Object from DPO to Omni";
           String emailBody = "Sorry. We were not able to Create Payment (Monthly) Object from DPO to Omni " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName();
           sendFailureEmail(emailSubject, emailBody, emailApiUrl);
       }

        String dpoToOmniTransactionalNo = "";

        try {
//          Post Payment (Monthly) from DPO to Omni
            dpoToOmniTransactionalNo = postingPaymentFromDPOToOmni(omniUrl, omniUsername, omniPassword, companyName, requestBodyJson);
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Post Payment (Monthly) from DPO to Omni", e.toString());
//          Send failure email
            String emailSubject = "DSS - Failed To Post Payment (Monthly) from DPO to Omni";
            String emailBody = "Sorry. We were not able to post payment (monthly) from DPO to Omni " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName();
            sendFailureEmail(emailSubject, emailBody, emailApiUrl);
        }
        return dpoToOmniTransactionalNo;
    }

    public String postingPaymentFromDPOToOmni(String domain, String userName, String password, String companyName, String postFields) {
        try {
            String url = domain + "/BankingTransaction?UserName=" + userName + "&Password=" + password + "&CompanyName=" + companyName;
            url = url.replace(" ", "%20");

            contextName = "AT_PAY_CAPTURE_URL_INSIDE_THE_postingPaymentFromDPOToOmni_FUNCTION";
            try {
                contextValueJsonString = url;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            contextName = "AT_PAY_CAPTURE_POST_DATA_INSIDE_THE_postingPaymentFromDPOToOmni_FUNCTION";
            try {
                contextValueJsonString = postFields;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

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
            common.logErrors("api", "RecurrentPaymentController", "postingPaymentFromDPOToOmni", "Post Payment From DPO To Omni", e.toString());
            return e.toString();
        }
    }

    public String salesJournalEntry(String domain, String companyName, String userName, String password, String postFields) {
        try {
            String url = domain + "/SalesJournalEntry/?UserName=" + userName + "&Password=" + password + "&CompanyName=" + companyName;
//            http://165.90.24.54:8080/SalesJournalEntry/?UserName=DSS&Password=machine&CompanyName=DSS
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
            common.logErrors("api", "RecurrentPaymentController", "postingPaymentFromDPOToOmni", "Post Payment From DPO To Omni", e.toString());

            contextName = "AT_PAY_ERROR_IN_SALES_JOURNEY_ENTRY";
            try {
                contextValueJsonString = "ERROR: "+e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return e.toString();
        }
    }//
    public String postJobTask(Customer customer, String jobNo, LocalDate todayFormatYYYYMD, String companyName, String omniUsername, String omniPassword, String omniUrl, String emailApiUrl) {
        common = new Common();
        //         POST JOB HERE
//         Prepare json request data
        requestBodyJson = "{\"job\" : \n" +
                "                        {\n" +
                "                        \"job_no\" : \"" + jobNo + "\",\n" +
                "                        \"job_description\" : \"DSS - " + customer.getFirstName() + " " + customer.getLastName() + "\",\n" +
                "                        \"job_memo\" :\"MEMO\",\n" +
                "                        \"job_category\" : \"DSS\",\n" +
                "                        \"customer_order_no\" : \"\",\n" +
                "                        \"delivery_details\" : \"\",\n" +
                "                        \"customer_account_code\" : \"" + customer.getSystemCustomerNo() + "\",\n" +
                "                        \"customer_branch_code\" : \"HO\",\n" +
                "                        \"customer_name\" : \"" + customer.getFirstName() + " " + customer.getLastName() + "\",\n" +
                "                        \"progress_date_1\" : \"" + todayFormatYYYYMD + "\", \n" +
                "                        \"progress_date_2\" : null,\n" +
                "                        \"progress_date_3\" : null,\n" +
                "                        \"warehouse_code\" : \"ISSP\",\n" +
                "                        \"progress_date_4\" : null,\n" +
                "                        \"default_markup_percent\" : 5,\n" +
                "                        \"stock_selling_price_method\" : \"Default\",\n" +
                "                        \"default_vat_code\" : \"1\",\n" +
                "                        \"wip_account\" : \"\",\n" +
                "                        \"invoice_method\" : \"Issued\",\n" +
                "                        \"active\" : true,\n" +
                "                        \"id\" : 1\n" +
                "                        }\n" +
                "                       }";

        String theJobNo = "";
        try {
//          Post Job
            theJobNo = postJob(omniUrl, omniUsername, omniPassword, companyName, requestBodyJson, jobNo);
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Post Job", e.toString());
            String emailSubject = "DSS - Failed To Post Job";
            String emailBody = "Sorry. We were not able to post job " + customer.getSystemCustomerNo() + " " + customer.getFirstName() + " " + customer.getLastName();
            sendFailureEmail(emailSubject, emailBody, emailApiUrl);
        }
        return theJobNo;
    }

    public String postJob(String domain, String userName, String password, String companyName, String postFields, String jobNo) {
        common = new Common();
        try {

            String url = domain + "/Job/" + jobNo + "?UserName=" + userName + "&Password=" + password + "&CompanyName=" + companyName;
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
            common.logErrors("api", "RecurrentPaymentController", "postJob", "Post Job", e.toString());
            return e.toString();
        }
    }

    private double getMonthlyExclusive(String packageName) {
        packageType = packageTypeService.getPackageType(packageName);
        return packageType.getMonthlyCostExclusive();
    }

    private double getMonthlyInclusive(String packageName) {
        packageType = packageTypeService.getPackageType(packageName);
        return packageType.getMonthlyCostInclusive();
    }

    public RecurrentPayment createRecurrentPaymentRecord(double paymentAmount, InstallationSite installationSite, String customerNo, String dpoToOmniTransactionalNo, String theJobNo, LocalDate today, String newTransactionNo) {
        common = new Common();

        try {
            recurrentPayment = new RecurrentPayment();
            recurrentPayment.setAmount(paymentAmount);
            recurrentPayment.setSystemCustomerNo(customerNo);
            recurrentPayment.setDpoToOmniTransactionalNo(dpoToOmniTransactionalNo);
            recurrentPayment.setJobNo(theJobNo);
            recurrentPayment.setInstallationId(installationSite.getId());
            recurrentPayment.setPackageTypeId(installationSite.getPackageTypeId());
            recurrentPayment.setDatePaid(today);
            recurrentPayment.setNarrative("Monthly amount paid");
            recurrentPayment.setAllocationTranxNo(newTransactionNo);
            recurrentPayment.setStatus("PAID");
            recurrentPaymentService.saveRecurrentPayment(recurrentPayment);
            return recurrentPayment;
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "createRecurrentPaymentRecord", "Save RecurrentPayment Record", e.toString());
            return null;
        }
    }

    public long saveInvoice(String tokenId, double amountInclusive, String invoiceNumber, String customerNo, long installationSiteId, int packageTypeId, String invoiceResponse, String narrative, int monthCounter){
        common = new Common();
        Invoice invoice = new Invoice();
        try {
            invoice.setTokenId(tokenId);
            invoice.setAmount(amountInclusive);
            invoice.setInvoiceRefNo(invoiceNumber);
            invoice.setSystemCustomerNo(customerNo);
            invoice.setInstallationId(installationSiteId);
            invoice.setPackageTypeId(packageTypeId);
            invoice.setInvoicingDate(LocalDate.now());
            invoice.setNarrative(narrative);
            invoice.setIsPaidOut(0); //Not yet paid out
            invoice.setIsPrinted("PENDING");
            invoice.setInvoiceResponse(invoiceResponse);
            invoice.setMonthCount(monthCounter);
            invoiceService.saveInvoice(invoice);

            contextName = "AT_DUE_DATE_SAVE_INVOICE_RECORD";
            try {
                contextValueJsonString = objectMapper.writeValueAsString(invoice);
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return invoice.getId();

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "sendDueDateComms", "Create a New Invoice", e.toString());
            System.out.println("Cannot create a new invoice: " + e.toString());

            contextName = "AT_DUE_DATE_SAVE_INVOICE_RECORD_FAILED";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return -1;
        }
    }

    public HikEvent enableDevice(String hppSiteId, String uniqueSiteId, String customerNo, String hikFormattedCustomerSiteName, String hikUrl, String emailApiUrl, String token) {
        common = new Common();
        //      Enable devices
        try {
            boolean hikResponse = common.device(hppSiteId, true, hikUrl, token);

            boolean hikResponseStatus = false;
            if(hikResponse){
                hikResponseStatus = true;
            }

            hikEvent = new HikEvent();
            hikEvent.setSystemCustomerNo(customerNo);
            hikEvent.setHppSiteId(hppSiteId);
            hikEvent.setUniqueSiteId(uniqueSiteId);
            hikEvent.setHikFormattedCustomerSiteName(hikFormattedCustomerSiteName);
            hikEvent.setEventResponse(hikResponseStatus);
            hikEvent.setEventType("ENABLE");
            hikEventService.saveHikEvent(hikEvent);

//            sendTechLeadEmail(hikFormattedCustomerSiteName, emailApiUrl);
//            sendFinanceEmail(hikFormattedCustomerSiteName, emailApiUrl);

            return hikEvent;

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Enable Devices", e.toString());
            return null;
        }
    }

    public void sendTechLeadEmail(String hikFormattedCustomerSiteName, String emailApiUrl) {
        common = new Common();
        //    Send email to Technical Lead
        configData = configDataService.getConfigDataByConfigName("TECHNICAL_LEAD_EMAIL");
        String techLeadEmail = configData.getConfigValue();
        configData = configDataService.getConfigDataByConfigName("SITE_ENABLE_EMAIL_SUBJECT");
        emailSubject = configData.getConfigValue();
        configData = configDataService.getConfigDataByConfigName("SITE_ENABLE_EMAIL_BODY");
        emailBody = configData.getConfigValue();
        emailBody = emailBody.replace("#sitename", hikFormattedCustomerSiteName);
        common.sendEmail(techLeadEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");

        contextName = "AT_RECURRENT_PAYMENT_SEND_TECHNICAL_LEAD_EMAIL";
        try {
            contextValueJsonString = techLeadEmail.concat(" --- ").concat(emailSubject).concat(" --- ").concat(emailBody);
            System.out.println(contextValueJsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
    }

    public void sendFinanceEmail(String hikFormattedCustomerSiteName, String emailApiUrl) {
//      Send email to Finance
        configData = configDataService.getConfigDataByConfigName("OMNI_FINANCE_EMAIL");
        String financeEmail = configData.getConfigValue();
        configData = configDataService.getConfigDataByConfigName("SITE_ENABLE_EMAIL_SUBJECT");
        emailSubject = configData.getConfigValue();
        configData = configDataService.getConfigDataByConfigName("SITE_ENABLE_EMAIL_BODY");
        emailBody = configData.getConfigValue();
        emailBody = emailBody.replace("#sitename", hikFormattedCustomerSiteName);
        common.sendEmail(financeEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");

        contextName = "AT_RECURRENT_PAYMENT_SEND_FINANCE_EMAIL";
        try {
            contextValueJsonString = financeEmail.concat(" --- ").concat(emailSubject).concat(" --- ").concat(emailBody);
            System.out.println(contextValueJsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
    }

    public void sendFailureEmail(String emailSubject, String emailBody, String emailApiUrl) {
//      Send failure email
        ConfigData configData = configDataService.getConfigDataByConfigName("FAILED_EMAIL");
        String failureEmailAddress = configData.getConfigValue();
        emailBody.concat("<br/>Regards,<br/>SGA Security Team");
        common.sendEmail(failureEmailAddress, emailSubject, emailBody, "0", "", emailApiUrl, "0");

        contextName = "AT_RECURRENT_PAYMENT_SEND_FAILURE_EMAIL";
        try {
            contextValueJsonString = failureEmailAddress.concat(" --- ").concat(emailBody);
            System.out.println(contextValueJsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
    }

    public void sendPaymentCommsToCustomerBeforeAccountIsDisabled(Customer customer, String emailApiUrl, String smsApiUrl) {
        common = new Common();
        //      1. Send Payment Received Email to Customer Before Account is Disabled
        try {
            configData = configDataService.getConfigDataByConfigName("AFTER_PAYMENT_RECEIVED_BEFORE_ACCOUNT_DISABLED_EMAIL_SUBJECT");
            emailSubject = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("AFTER_PAYMENT_RECEIVED_BEFORE_ACCOUNT_DISABLED_EMAIL_BODY");
            emailBody = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
            emailHeader = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
            emailFooter = configData.getConfigValue();
            emailBody = emailBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
            fullEmail = emailHeader + emailBody + emailFooter;
            common.sendEmail(customer.getEmail(), emailSubject, fullEmail, "1", "", emailApiUrl, "1");

            contextName = "AT_PAY_SEND_PAYMENT_RECEIVED_EMAIL_TO_CUSTOMER_BEFORE_ACCOUNT_IS_DISABLED";
            try {
                contextValueJsonString = customer.getEmail().concat(" --- ").concat(emailSubject).concat(" --- ").concat(emailHeader).concat(" --- ").concat(emailBody);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

//          Send SMS
            configData = configDataService.getConfigDataByConfigName("AFTER_PAYMENT_RECEIVED_BEFORE_ACCOUNT_DISABLED_SMS_BODY");
            smsBody = configData.getConfigValue();
            smsBody = smsBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
            common.sendSMS(customer.getPhone(), smsBody, smsApiUrl);

            contextName = "AT_PAY_SEND_PAYMENT_RECEIVED_SMS_TO_CUSTOMER_BEFORE_ACCOUNT_IS_DISABLED";
            try {
                contextValueJsonString = customer.getPhone().concat(" --- ").concat(smsBody);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send Payment Received Email To Customer Before Account Is Disabled", e.toString());

            contextName = "AT_PAY_SEND_PAYMENT_RECEIVED_EMAIL_TO_CUSTOMER_BEFORE_ACCOUNT_IS_DISABLED_FAILED";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }

//    public void sendPaymentConfirmationCommsCustomer(Customer customer, String emailApiUrl, String smsApiUrl) {
//        try {
//            configData = configDataService.getConfigDataByConfigName("SUBSCRIPTION_PAYMENT_CONFIRMATION_EMAIL_SUBJECT");
//            emailSubject = configData.getConfigValue();
//            configData = configDataService.getConfigDataByConfigName("SUBSCRIPTION_PAYMENT_CONFIRMATION_EMAIL_BODY");
//            emailBody = configData.getConfigValue();
//            configData = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
//            emailHeader = configData.getConfigValue();
//            configData = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
//            emailFooter = configData.getConfigValue();
//            emailBody = emailBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
//            fullEmail = emailHeader + emailBody + emailFooter;
//            common.sendEmail(customer.getEmail(), emailSubject, fullEmail, "1", "", emailApiUrl, "1");
//
//            contextName = "SEND_PAYMENT_CONFIRMATION_COMMS_TO_CUSTOMER";
//            try {
//                contextValueJsonString = objectMapper.writeValueAsString(customer);
//                System.out.println(contextValueJsonString);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//
////          Send SMS
//            configData = configDataService.getConfigDataByConfigName("AFTER_PAYMENT_RECEIVED_BEFORE_ACCOUNT_DISABLED_SMS_BODY");
//            smsBody = configData.getConfigValue();
//            smsBody = smsBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
//            common.sendSMS(customer.getPhone(), smsBody, smsApiUrl);
//
//
//
//        } catch (Exception e) {
//            common.logErrors("api", "RecurrentPaymentController", "pay", "Send Account Restored Email And SMS", e.toString());
//        }
//
//        try {
//            Thread.sleep(2000);
//        } catch (Exception e) {
//            common.logErrors("api", "RecurrentPaymentController", "pay", "Sending email and SMS Thread Sleep", e.toString());
//        }
//    }

    public void sendAccountRestoredCommsToCustomer(Customer customer, String emailApiUrl, String smsApiUrl, String localDate) {
        common = new Common();
        //      Send Account Restored Email to Customer
        try {
            ConfigData configDataEmailSubject = configDataService.getConfigDataByConfigName("ACCOUNT_RESTORED_EMAIL_SUBJECT");
            String emailSubject = configDataEmailSubject.getConfigValue();
            ConfigData configDataEmailBody = configDataService.getConfigDataByConfigName("ACCOUNT_RESTORED_EMAIL_BODY");
            String emailBody = configDataEmailBody.getConfigValue();
            ConfigData configDataEmailHeader = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
            String emailHeader = configDataEmailHeader.getConfigValue();
            ConfigData configDataEmailFooter = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
            String emailFooter = configDataEmailFooter.getConfigValue();
            emailBody = emailBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
            emailBody = emailBody.replace("#date", localDate);
            String fullEmail = emailHeader + emailBody + emailFooter;
            common.sendEmail(customer.getEmail(), emailSubject, fullEmail, "1", "", emailApiUrl, "1");

//          Send SMS
            ConfigData configDataSmsBody = configDataService.getConfigDataByConfigName("ACCOUNT_RESTORED_SMS_BODY");
            String smsBody = configDataSmsBody.getConfigValue();
            smsBody = smsBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
            smsBody = smsBody.replace("#date", localDate); // Change to full date for next month (USE ANNIVERSARY DATE)
            common.sendSMS(customer.getPhone(), smsBody, smsApiUrl);

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "pay", "Send Account Restored Email And SMS", e.toString());
        }
    }

    public void disableDevices(String hppSiteId, String customerNo, String uniqueSiteId, String hikFormattedCustomerSiteName, String emailApiUrl, String hikUrl, String token) {
        common = new Common();
//      Disable devices
        try {
            boolean hikResponse = common.device(hppSiteId, false, hikUrl, token);

            boolean hikResponseStatus = false;
            if(hikResponse){
                hikResponseStatus = true;
            }

            try {
                HikEvent hikEvent = new HikEvent();
                hikEvent.setSystemCustomerNo(customerNo);
                hikEvent.setHppSiteId(hppSiteId);
                hikEvent.setUniqueSiteId(uniqueSiteId);
                hikEvent.setHikFormattedCustomerSiteName(hikFormattedCustomerSiteName);
                hikEvent.setEventResponse(hikResponseStatus);
                hikEvent.setEventType("DISABLE");
                hikEventService.saveHikEvent(hikEvent);

                contextName = "AT_24_HOUR_INTERVAL_SAVE_DISABLE_HIK_EVENT_FOR_24_HOUR_PERIOD";
                try {
                    contextValueJsonString = objectMapper.writeValueAsString(hikEvent);
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            } catch (Exception e) {
                contextName = "AT_24_HOUR_INTERVAL_SAVE_DISABLE_HIK_EVENT_FOR_24_HOUR_PERIOD_FAILED";
                try {
                    contextValueJsonString = e.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }

//            try {
////                  Send email to Lead Technician and Finance
//                ConfigData configDataEmailSubject = configDataService.getConfigDataByConfigName("SITE_DISABLE_EMAIL_SUBJECT");
//                String emailSubject = configDataEmailSubject.getConfigValue();
//                ConfigData configDataEmailBody = configDataService.getConfigDataByConfigName("SITE_DISABLE_EMAIL_BODY");
//                String emailBody = configDataEmailBody.getConfigValue();
//                ConfigData configDataFinanceEmail = configDataService.getConfigDataByConfigName("OMNI_FINANCE_EMAIL");
//                String financeEmail = configDataFinanceEmail.getConfigValue();
//                ConfigData configDataTechLeadEmail = configDataService.getConfigDataByConfigName("TECHNICAL_LEAD_EMAIL");
//                String technicalLeadEmail = configDataTechLeadEmail.getConfigValue();
//                emailBody = emailBody.replace("#sitename", installationSite.getHikFormattedCustomerSiteName());
//                common.sendEmail(financeEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");
//
//                contextName = "AT_24_HOUR_INTERVAL_SEND_EMAIL_TO_FINANCE_FOR_24_HOUR_PERIOD";
//                try {
//                    contextValueJsonString = financeEmail.concat(" --- ").concat(emailSubject).concat(" --- ").concat(emailBody);
//                    System.out.println(contextValueJsonString);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//
//                common.sendEmail(technicalLeadEmail, emailSubject, emailBody, "0", "", emailApiUrl, "0");
//
//                contextName = "AT_24_HOUR_INTERVAL_SEND_EMAIL_TO_TECHNICAL_LEAD_FOR_24_HOUR_PERIOD";
//                try {
//                    contextValueJsonString = technicalLeadEmail.concat(" --- ").concat(emailSubject).concat(" --- ").concat(emailBody);
//                    System.out.println(contextValueJsonString);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//
//            } catch (Exception e) {
//                contextName = "AT_24_HOUR_INTERVAL_FAILED_IN_FINANCE_AND_TECHNICAL_LEAD_EMAIL_SENDING";
//                try {
//                    contextValueJsonString = e.toString();
//                    System.out.println(contextValueJsonString);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
//
//                common.logErrors("api", "RecurrentPaymentController", "disableDevices", "Send Email To Technician", e.toString());
//            }
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "disableDevices", "Disable Devices", e.toString());
            contextName = "AT_24_HOUR_INTERVAL_FAILED_IN_DISBALE_DEVICES_BLOCK";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }

    public void sendAccountSuspensionEmail(Customer customer, String shortenedPaymentUrl, String emailApiUrl) {
        common = new Common();
        try {
//          Send email account suspension email
            ConfigData configDataEmailSubject = configDataService.getConfigDataByConfigName("ACCOUNT_SUSPENSION_EMAIL_SUBJECT");
            String emailSubject = configDataEmailSubject.getConfigValue();
            ConfigData configDataEmailHeader = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
            String emailHeader = configDataEmailHeader.getConfigValue();
            ConfigData configDataEmailFooter = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
            String emailFooter = configDataEmailFooter.getConfigValue();
            ConfigData configDataEmailBody = configDataService.getConfigDataByConfigName("ACCOUNT_SUSPENSION_EMAIL_BODY");
            String emailBody = configDataEmailBody.getConfigValue();
            emailBody = emailBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
            emailBody = emailBody.replace("#link", shortenedPaymentUrl);
            String emailAddress = customer.getEmail();
            String fullEmail = emailHeader + emailBody + emailFooter;
            common.sendEmail(emailAddress, emailSubject, fullEmail, "1", "", emailApiUrl, "1");

            contextName = "AT_24_HOUR_INTERVAL_SEND_EMAIL_TO_CUSTOMER_TO_PAY";
            try {
                contextValueJsonString = emailAddress.concat(" --- ").concat(emailSubject).concat(" --- ").concat(fullEmail);
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e) {
            contextName = "AT_24_HOUR_INTERVAL_FAILED_TO_SEND_EMAIL_TO_CUSTOMER_TO_PAY";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            common.logErrors("api", "RecurrentPaymentController", "sendAccountSuspensionEmail", "Send Email To Notify Customer Of Suspension", e.toString());
        }
    }

    public void sendAccountSuspensionSMS(Customer customer, String shortenedPaymentUrl, String smsApiUrl) {
        common = new Common();
        try {
//              Send SMS
            ConfigData configDataSmsBody = configDataService.getConfigDataByConfigName("ACCOUNT_SUSPENSION_SMS_BODY");
            String smsBody = configDataSmsBody.getConfigValue();
            smsBody = smsBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
            smsBody = smsBody.replace("#link", shortenedPaymentUrl);
            common.sendSMS(customer.getPhone(), smsBody, smsApiUrl);

            customer.setCustomerStatus("SUSPENDED");  // Suspend customer
            customerService.saveCustomer(customer);

            contextName = "AT_24_HOUR_INTERVAL_SEND_SMS_TO_CUSTOMER_TO_PAY";
            try {
                contextValueJsonString = customer.getPhone().concat(" --- ").concat(smsBody);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "sendAccountSuspensionSMS", "Send Email To Notify Customer Of Suspension", e.toString());
            contextName = "AT_24_HOUR_INTERVAL_FAILED_TO_SEND_SMS_TO_CUSTOMER_TO_PAY";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }

    public Map<String, String> postInvoiceToOmniAtDueDate(String tokenId, LocalDate nextPaymentDate) {
        common = new Common();
        DateFunctions dateFunctions = new DateFunctions();

        Map<String, String> map = new HashMap<>();
        try {
            LocalDate startingDate = LocalDate.now();



            String todayDate = nextDateService.startingDate();
            todayFormatYYYYMD = nextDateService.startingDateYYYYMD();
            onThisDay = nextDateService.getDayValue(startingDate);
            plus30Days = nextDateService.add30DaysYYYYMD();


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


            ConfigData configDataServiceCode = configDataService.getConfigDataByConfigName("SAFE_HOME_SERVICE_CODE");
            String safeHomeServiceCode = configDataServiceCode.getConfigValue();
            ConfigData configDataPlusServiceCode = configDataService.getConfigDataByConfigName("SAFE_HOME_PLUS_SERVICE_CODE");
            String safeHomePlusServiceCode = configDataPlusServiceCode.getConfigValue();

            String narrative = "";

            if (installationSite.getPackageTypeName().equals("SAFE_HOME")) {
                pkgName = safeHomeServiceCode;
            } else {
                pkgName = safeHomePlusServiceCode;
            }

            paymentAmountExclusive = getMonthlyExclusive(installationSite.getPackageTypeName());

//          1. POST INVOICE TO OMNI
//             A. Prepare json request data
            String firstDescription = "Monthly Subscription Service charge for ";
            String secondDescription = "for the period " +finalStartingDateString + " to " + finalNextDateString;
            requestBodyJson = "{\"invoice\" : {\"customer_account_code\" : \"" + customerNo + "\", \"invoice_lines\": [{\"description\" : \"" + firstDescription + "\"}, {\"stock_code\": \"" + pkgName + "\",\"quantity\" : 1,\"selling_price\" : " + paymentAmountExclusive + "}, {\"description\" : \"" + secondDescription + "\"}]}}";

            contextName = "PUSH_OMNI_INVOICE_DATA_AT_OMNI_API_DUE_DATE_RECCUR";
            try {
                contextValueJsonString = requestBodyJson;
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);


//          B. Post Invoice to Omni
            String invoiceNumber = "NO_INVOICE_REF_NUMBER_ASSIGNED";
            try {
                invoiceNumber = postingInvoiceToOmni(omniUrl, omniUsername, omniPassword, companyName, requestBodyJson);
            } catch (Exception e) {
                invoiceNumber = "NO_INVOICE_REF_NUMBER_ASSIGNED";
            }

            String invoiceResponse = "{\"error\":\"No invoice response found\"}";
            try {
                invoiceResponse = getInvoiceResponse(omniUrl, omniUsername, omniPassword, companyName, invoiceNumber);
            } catch (Exception e) {
                invoiceResponse = "{\"error\":\"No invoice response found\"}";
            }

            if (!invoiceNumber.contains("NO_INVOICE_REF") || !invoiceNumber.contains("INVOICE_ERROR")) {
                narrative = "Success. Amount and Invoice Ref No. generated from Omni side. (Recurrent Payment)";
            } else {
//              transactionNo = JsonPath.read(invoiceResponse, "$.invoice.transaction_no");
                invoiceNumber = "NOT_YET_ASSIGNED";
                narrative = "Take Note: Amount and Invoice Ref No. generated from SGA API end. Check the availability of Omni and confirm the accuracy of these values.";
            }

            map.put("invoiceNumber", invoiceNumber);
            map.put("invoiceResponse", invoiceResponse);
            map.put("narrative", narrative);
            return map;
        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "sendDueDateComms", "Create a New Invoice", e.toString());
            System.out.println("Cannot create a new invoice: " + e.toString());

            contextName = "AT_DUE_DATE_SAVE_INVOICE_RECORD_FAILED";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            String narrative = "Take Note: Amount and Invoice Ref No. generated from SGA API end. Check the availability of Omni and confirm the accuracy of these values.";

            map.put("invoiceNumber", "NO_INVOICE_REF_NUMBER_ASSIGNED");
            map.put("invoiceResponse", "Error: "+e.toString());
            map.put("narrative", narrative);
            return map;
        }
    }

    public PaymentLink savePaymentLinkRecord(String customerNo, String shortenedPaymentUrl, String tokenId, double paymentAmount, long invoiceId, int monthCounter) {
        common = new Common();
        try {
            contextName = "AT_DUE_DATE_RECURRENT_TOKENIZED_AMOUNT";
            try {
                contextValueJsonString = "Customer No. "+ customerNo + " Amount "+paymentAmount;
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            PaymentLink paymentLink = new PaymentLink();
            paymentLink.setSystemCustomerNo(customerNo);
            paymentLink.setLink(shortenedPaymentUrl);
            paymentLink.setTokenId(tokenId);
            paymentLink.setIsUsed("NO");
            paymentLink.setAmount(paymentAmount);
            paymentLink.setInvoiceId(invoiceId);
            paymentLink.setMonthCount(monthCounter);
            paymentLinkService.savePaymentLink(paymentLink);

            contextName = "AT_DUE_DATE_SAVE_PAYMENT_LINK_RECORD";
            try {
                contextValueJsonString = objectMapper.writeValueAsString(paymentLink);
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return paymentLink;

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "sendDueDateComms", "Save PaymentLink Data", e.toString());

            contextName = "AT_DUE_DATE_SAVE_PAYMENT_LINK_RECORD_FAILED";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return null;
        }
    }

 public void sendPaymentDueEmail(Customer customer, String shortenedPaymentUrl, String emailApiUrl) {
     common = new Common();
        try {
//      Send email asking customer to pay
        ConfigData configDataEmailSubject = configDataService.getConfigDataByConfigName("PAYMENT_DUE_NOW_EMAIL_SUBJECT");
        String emailSubject = configDataEmailSubject.getConfigValue();
        ConfigData configDataEmailHeader = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
        String emailHeader = configDataEmailHeader.getConfigValue();
        ConfigData configDataEmailFooter = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
        String emailFooter = configDataEmailFooter.getConfigValue();
        ConfigData configDataDueEmailBody = configDataService.getConfigDataByConfigName("PAYMENT_DUE_NOW_EMAIL_BODY");
        String emailBody = configDataDueEmailBody.getConfigValue();
        emailBody = emailBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
        emailBody = emailBody.replace("#link", shortenedPaymentUrl);
        String emailAddress = customer.getEmail();
        String fullEmail = emailHeader + emailBody + emailFooter;
        common.sendEmail(emailAddress, emailSubject, fullEmail, "1", "", emailApiUrl, "1");

        contextName = "AT_DUE_DATE_SEND_EMAIL_TO_CUSTOMER_TO_PAY";
        try {
            contextValueJsonString = emailAddress.concat(" --- ").concat(emailSubject).concat(" --- ").concat(fullEmail);
            System.out.println(contextValueJsonString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
    } catch(Exception e) {
        common.logErrors("api", "RecurrentPaymentController", "sendDueDateComms", "Send Due Date Payment Email", e.toString());
        contextName = "AT_DUE_DATE_SEND_EMAIL_TO_CUSTOMER_TO_PAY_FAILED";
        try {
            contextValueJsonString = e.toString();
            System.out.println(contextValueJsonString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        captureAuditTrail(contextName, contextDesc, contextValueJsonString);
    }
  }

  public void sendPaymentDueSMS(Customer customer, String shortenedPaymentUrl, String smsApiUrl){
      common = new Common();
        try {
//        Send SMS
          ConfigData configDataDueSmsBody = configDataService.getConfigDataByConfigName("PAYMENT_DUE_NOW_SMS_BODY");
          String smsBody = configDataDueSmsBody.getConfigValue();
          smsBody = smsBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
          smsBody = smsBody.replace("#link", shortenedPaymentUrl);
          common.sendSMS(customer.getPhone(), smsBody, smsApiUrl);

          contextName = "AT_DUE_DATE_SEND_SMS_TO_CUSTOMER_TO_PAY";
          try {
              contextValueJsonString = customer.getPhone().concat(" --- ").concat(smsBody);
              System.out.println(contextValueJsonString);
          } catch (Exception ex) {
              ex.printStackTrace();
          }
          captureAuditTrail(contextName, contextDesc, contextValueJsonString);

      } catch (Exception e) {
          common.logErrors("api", "RecurrentPaymentController", "sendDueDateComms", "Send Due Date Payment SMS", e.toString());

          contextName = "AT_DUE_DATE_SEND_SMS_TO_CUSTOMER_TO_PAY_FAILED";
          try {
              contextValueJsonString = e.toString();
              System.out.println(contextValueJsonString);
          } catch (Exception ex) {
              ex.printStackTrace();
          }
          captureAuditTrail(contextName, contextDesc, contextValueJsonString);
      }
  }

    @CrossOrigin
    @GetMapping("/manualsend552hrcomms")
    @ResponseBody
    public String send552HrCommsManual(@RequestParam String customerName, @RequestParam String emailAddress, @RequestParam String phone, @RequestParam double paymentAmount, @RequestParam String anniversaryDate) throws JsonProcessingException {

        try {
            common = new Common();
            configData = new ConfigData();

            ConfigData configDataSms = configDataService.getConfigDataByConfigName("SMS_API_URL");
            smsApiUrl = configDataSms.getConfigValue();

            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
            emailApiUrl = configDataEmail.getConfigValue();

            ConfigData configDataBitlyToken = configDataService.getConfigDataByConfigName("BITLY_TOKEN");
            bitlyToken = configDataBitlyToken.getConfigValue();

            ConfigData configDataBitlyUrl = configDataService.getConfigDataByConfigName("BITLY_URL");
            bitlyUrl = configDataBitlyUrl.getConfigValue();

            try {
//              Send email asking customer to pay
                configData = configDataService.getConfigDataByConfigName("SEVEN_DAYS_TO_PAYMENT_DATE_EMAIL_SUBJECT");
                emailSubject = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
                emailHeader = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
                emailFooter = configData.getConfigValue();
                configData = configDataService.getConfigDataByConfigName("SEVEN_DAYS_TO_PAYMENT_DATE_EMAIL_BODY");
                emailBody = configData.getConfigValue();
                emailBody = emailBody.replace("#customername", customerName);
                emailBody = emailBody.replace("#amount", String.valueOf(paymentAmount));
                emailBody = emailBody.replace("#anniversarydate", anniversaryDate);

                fullEmail = emailHeader + emailBody + emailFooter;
                common.sendEmail(emailAddress, emailSubject, fullEmail, "1", "", emailApiUrl, "1");

                contextName = "AT_552_HOUR_SEND_EMAIL_ASKING_CUSTOMER_TO_PAY";
                try {
                    contextValueJsonString = emailAddress.concat(" --- ").concat(emailSubject).concat(" --- ").concat(fullEmail);
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            } catch (Exception e) {
                common.logErrors("api", "RecurrentPaymentController", "send552HrCommsManual", "Send Email To Notify Customer Of 7 Days To Due Date", e.toString());

                contextName = "AT_552_HOUR_SEND_EMAIL_ASKING_CUSTOMER_TO_PAY_FAILED";
                try {
                    contextValueJsonString = e.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            }

            try {
//              Send SMS
                configData = configDataService.getConfigDataByConfigName("SEVEN_DAYS_TO_PAYMENT_DATE_SMS_BODY");
                smsBody = configData.getConfigValue();
                smsBody = smsBody.replace("#customername", customerName);
                smsBody = smsBody.replace("#amount", String.valueOf(paymentAmount));
                smsBody = smsBody.replace("#anniversarydate", anniversaryDate);
                common.sendSMS(phone, smsBody, smsApiUrl);

                contextName = "AT_552_HOUR_SEND_SMS_ASKING_CUSTOMER_TO_PAY";
                try {
                    contextValueJsonString = phone.concat(" --- ").concat(smsBody);
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            } catch (Exception e) {
                common.logErrors("api", "RecurrentPaymentController", "send552HrCommsManual", "Send SMS To Notify Customer Of 7 Days To Due Date", e.toString());

                contextName = "AT_552_HOUR_SEND_SMS_ASKING_CUSTOMER_TO_PAY_FAILED";
                try {
                    contextValueJsonString = e.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }

            contextName = "AT_552_HOUR_EXECUTE_SUCCESSFUL";
            try {
                contextValueJsonString = "At 552 Hour Execution Successful And Returned SUCCESS";
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return "SUCCESS";

        } catch (Exception e) {
            common.logErrors("api", "RecurrentPaymentController", "send552HrCommsManual", "Send SMS To Notify Customer Of 7 Days To Due Date", e.toString());

            contextName = "AT_552_HOUR_EXECUTE_FAILED_AND_ERROR_CAUGHT";
            try {
                contextValueJsonString = e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            return "FAIL";
        }
    }

    // IPRP
//     @CrossOrigin
//     @PostMapping("/devicemgmt/devicelist")
//     @ResponseBody
//    public ResponseEntity<String> iprpDeviceDetails(@RequestParam String key) {

    public ResponseEntity<String> iprpDeviceDetails(String key) {

        JsonNode matchList = null;
         Map<String, String> mapResponse = null;

         String siteID = null;
         String siteName = null;
         String devName = null;
         String accountID = null;
         String activeStatus = null;
         String devIndex = null;
         String devSerial = null;
         String deviceID = null;
         String deviceKey = null;

//        ConfigData configDataUrl = configDataService.getConfigDataByConfigName("HIK_IPRP_DEVICE_URL_PUBLIC");
        ConfigData configDataUrl = configDataService.getConfigDataByConfigName("HIK_IPRP_DEVICE_URL_LOCAL");
        String apiUrl = configDataUrl.getConfigValue();
        ConfigData configDataHikIprpUsername = configDataService.getConfigDataByConfigName("HIK_IPRP_USERNAME");
        String hikIprpUsername = configDataHikIprpUsername.getConfigValue();
        ConfigData configDataHikIprpPwd = configDataService.getConfigDataByConfigName("HIK_IPRP_PASSWORD");
        String hikIprpPawd = configDataHikIprpPwd.getConfigValue();

        Map<String, Object> map = new HashMap<>();

        HttpHeaders responseHeaders = new HttpHeaders();

        ObjectMapper objectMapperEvent = new ObjectMapper();

        String requestBody = "{\n" +
                "  \"SearchDescription\": {\n" +
                "    \"position\": 0,\n" +
                "    \"maxResult\": 10,\n" +
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

            matchList = jsonResponse.path("SearchResult").path("MatchList").get(0);
            siteID = matchList.path("Device").path("siteInfo").path("siteID").asText();
            siteName = matchList.path("Device").path("siteInfo").path("siteName").asText();
            devName = matchList.path("Device").path("devName").asText();
            accountID = matchList.path("Device").path("accountID").asText();
            activeStatus = matchList.path("Device").path("activeStatus").asText();
            devIndex = matchList.path("Device").path("devIndex").asText();
            devSerial = matchList.path("Device").path("devSerial").asText();
            deviceID = matchList.path("Device").path("ISUP").path("deviceID").asText();
            deviceKey = matchList.path("Device").path("ISUP").path("deviceKey").asText();

//          Function to store details in the database table if they do not exist
            mapResponse = saveIPRPDetails(siteID, siteName, devName, accountID, activeStatus, devIndex, devSerial, deviceID, deviceKey);

            if(mapResponse == null){
                map.put("status", "FAIL");
                map.put("error", "1");
                map.put("errorMessage", "Null object");

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(responseHeaders)
                        .body("Null object");
            }

            if(mapResponse.get("status").equals("SUCCESS")){
                map.put("status", "SUCCESS");
                map.put("data", jsonResponse);
                map.put("error", "0");
                map.put("errorMessage", "");

                String json = objectMapperEvent.writeValueAsString(map);
                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body(json);

            } else {
                if(jsonResponse != null){
                    map.put("status", "FAIL");
                    map.put("error", "1");
                    map.put("errorMessage", mapResponse.get("errorMessage"));
                } else {
                    map.put("status", "FAIL");
                    map.put("error", "1");
                    map.put("errorMessage", mapResponse.get("errorMessage"));
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(responseHeaders)
                        .body(mapResponse.get("errorMessage"));
            }
        } catch (Exception e) {
            map.put("status", "FAIL");
            map.put("error", "1");
            map.put("errorMessage", "FAIL - OUTER_CATCH_ERROR: "+e.toString()+"\n\n requestBody: "+requestBody.toString()+"\n\n apiUrl " + apiUrl+" - hikIprpUsername "+hikIprpUsername+" - hikIprpPawd "+hikIprpPawd+"\n\n matchList: "+matchList+"\n\n siteID -"+siteID+" siteName "+siteName);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(responseHeaders)
                    .body("FAIL - OUTER_CATCH_ERROR: "+e.toString()+"\n\n requestBody: "+requestBody.toString()+"\n\n apiUrl " + apiUrl+" - hikIprpUsername "+hikIprpUsername+" - hikIprpPawd "+hikIprpPawd+"\n\n matchList: "+matchList+"\n\n siteID -"+siteID+" siteName "+siteName+"\n\n mapResponse: "+mapResponse);
        }
    }

    public Map<String, String> saveIPRPDetails(String siteId, String siteName, String devName, String accountId, String activeStatus, String devIndex, String devSerial, String deviceId, String deviceKey){

        Map<String, String> map = new HashMap<>();
        common = new Common();
        try {
            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
            emailApiUrl = configDataEmail.getConfigValue();
        } catch (Exception e){
            common.logErrors("api", "HikController", "saveHikDetails", "Failed to Fetch Email URL", e.toString());
        }

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

                    } catch (Exception e) {
                        map.put("status", "FAIL");
                        map.put("error", "1");
                        map.put("errorMessage", e.toString());

                        String emailSubject = "DSS - Failed To Save IPRP Record";
                        String emailBody = "Sorry. IPRP record could be saved for device " +devSerial+" for customer with details " + customerNo + " " + customerFirstName + " " + customerLastName;
                        sendFailureEmail(emailSubject, emailBody, emailApiUrl);
                        common.logErrors("api", "HikController", "saveHikDetails", "Save IPRP Record", e.toString());
                    }

                } else {
                    map.put("status", "FAIL");
                    map.put("error", "1");
                    map.put("errorMessage", "IPRP record with site ID "+siteId+" exists");

                    String emailSubject = "DSS - Cannot Save Duplicate IPRP Device Record";
                    String emailBody = "Sorry. IPRP record could be duplicated for device " +devSerial+" for customer with details " + customerNo + " " + customerFirstName + " " + customerLastName;
                    sendFailureEmail(emailSubject, emailBody, emailApiUrl);
                    common.logErrors("api", "HikController", "saveHikDetails", "Save IPRP Record", "Attempt to Save Duplicate IPRP Record");
                }
            } else {
                map.put("status", "FAIL");
                map.put("error", "1");
                map.put("errorMessage", "No installation record found "+siteId);

                String emailSubject = "DSS - No Installation Site Record Found";
                String emailBody = "Sorry. No installation record found for device with serial number " +devSerial;
                sendFailureEmail(emailSubject, emailBody, emailApiUrl);
                common.logErrors("api", "HikController", "saveHikDetails", "Save Installation Site Record", "No Installation Site Record Found");
            }
        } catch (Exception e){
            map.put("status", "FAIL");
            map.put("error", "1");
            map.put("errorMessage", e.toString());
            String emailSubject = "DSS - Failure To Save IPRP Details";
            String emailBody = "Sorry. There was error in saving IPRP details for device with serial number " +devSerial;
            sendFailureEmail(emailSubject, emailBody, emailApiUrl);
            common.logErrors("api", "SOSController", "saveHikDetails", "Save HIK Details If Installation Site Record With Site ID "+siteId+" Exists", e.toString());
        }
        return map;
    }

    private boolean checkCustomerAllPaymentsDone(String customerNo){
        try {
            Thread.sleep(6000);
        } catch (Exception e){}

        try {
            List<PaymentLink> paymentLinkList = paymentLinkService.getPaymentLinksBySystemCustomerNoIsNo(customerNo);
            if(paymentLinkList.size() > 0){
                return false;
            } else {
                return true;
            }
        } catch (Exception e){
            common.logErrors("api", "RecurrentPaymentController", "saveHikDetails", "Check Unused Payment Links", e.toString());
            return false;
        }
    }

    private PaymentLink getPaymeDetailsFromPaymentLink(String tokenId){
        try {
            PaymentLink paymentLink1 = paymentLinkService.getPaymentLinkByTokenId(tokenId);
            return paymentLink1;
        } catch (Exception e){
            common.logErrors("api", "RecurrentPaymentController", "getPaymeDetailsFromPaymentLink", "Get Payment Details", e.toString());
            return null;
        }
    }

    private String getPayLinkForBalances(String customerNo){
        try {
            List<PaymentLink> paymentLinkList = paymentLinkService.getPaymentLinksBySystemCustomerNoIsNo(customerNo);
            StringBuilder stringBuilder = new StringBuilder();
            if(paymentLinkList.size() > 0){
                stringBuilder.append("<table border=0>");
                stringBuilder.append("<tr><td>Payment Link</td><td>Month</td></tr>");
                for(PaymentLink paymentLink1: paymentLinkList){
                    stringBuilder.append("<tr><td>"+paymentLink1.getLink().toString()+"</td><td>"+paymentLink1.getMonthCount()+"</td></tr>");
                }
                stringBuilder.append("</table>");
                return stringBuilder.toString();
            } else {
                return "NO UNPAID PAYMENT LINKS";
            }
        } catch (Exception e){
            common.logErrors("api", "RecurrentPaymentController", "getPayLinkForBalances", "Error in Getting Unpaid Payment Links", e.toString());
            return "ERROR IN GETTING UNPAID PAYMENT LINKS";
        }
    }

    private String getPayLinkForBalancesForSMS(String customerNo){
        try {
            List<PaymentLink> paymentLinkList = paymentLinkService.getPaymentLinksBySystemCustomerNoIsNo(customerNo);
            StringBuilder stringBuilder = new StringBuilder();
            if(paymentLinkList.size() > 0){
                for(PaymentLink paymentLink1: paymentLinkList){
                    stringBuilder.append("Month "+paymentLink1.getMonthCount() +" - "+ paymentLink1.getLink().toString());
                }
                return stringBuilder.toString();
            } else {
                return "NO UNPAID PAYMENT LINKS";
            }
        } catch (Exception e){
            common.logErrors("api", "RecurrentPaymentController", "getPayLinkForBalances", "Error in Getting Unpaid Payment Links", e.toString());
            return "ERROR IN GETTING UNPAID PAYMENT LINKS";
        }
    }

    public void sendWeHaveReceivedYourPaymementEmailToFinanceAndTechLead(String emailApiUrl, Customer customer, String amount){
        common = new Common();
        try {
            ConfigData configDataWeHaveReceivedYourPaymentHeader = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
            String emailHeader = configDataWeHaveReceivedYourPaymentHeader.getConfigValue();
            ConfigData configDataWeHaveReceivedYourPaymentFooter = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
            String emailFooter = configDataWeHaveReceivedYourPaymentFooter.getConfigValue();

            ConfigData configDataFinance = configDataService.getConfigDataByConfigName("OMNI_FINANCE_EMAIL");
            String financeEmail = configDataFinance.getConfigValue();

            ConfigData configDataTechLead = configDataService.getConfigDataByConfigName("TECHNICAL_LEAD_EMAIL");
            String techLeadEmail = configDataTechLead.getConfigValue();

//            ConfigData configDataWeHaveReceivedYourPaymentSubject = configDataService.getConfigDataByConfigName("WE_HAVE_RECEIVED_YOUR_PAYMENT_EMAIL_SUBJECT");
//            String emailSubject = configDataWeHaveReceivedYourPaymentSubject.getConfigValue();
            String emailSubject = "DSS - Customer has made a payment";
//            ConfigData configDataWeHaveReceivedYourPaymentBody = configDataService.getConfigDataByConfigName("WE_HAVE_RECEIVED_YOUR_PAYMENT_EMAIL_BODY");
//            String emailBody = configDataWeHaveReceivedYourPaymentBody.getConfigValue();
            String emailBody = customer.getFirstName() + " " + customer.getLastName() + " "+customer.getSystemCustomerNo()+" has made payment of KES " + amount;

//            emailBody = emailBody.replace("#customername", customer.getFirstName() + " " + customer.getLastName());
//            emailBody = emailBody.replace("#amount", amount);
            String fullEmail = emailHeader + emailBody + emailFooter;
            common.sendEmail(financeEmail, emailSubject, fullEmail, "1", "", emailApiUrl, "1");
            common.sendEmail(techLeadEmail, emailSubject, fullEmail, "1", "", emailApiUrl, "1");

            contextName = "AT_PAY_SEND_WE_HAVE_RECEIVED_YOUR_PAYMENT_EMAIL_TO_TECH_LEAD_AND_FINANCE";
            try {
                contextValueJsonString =customer.getEmail().concat(" -- "+emailSubject).concat(" -- "+emailBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

        } catch (Exception e){
            contextName = "AT_PAY_FAILED_TO_SEND_WE_HAVE_RECEIVED_YOUR_PAYMENT_EMAIL_TO_TECH_LEAD_AND_FINANCE";
            try {
                contextValueJsonString = e.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
        }
    }
}
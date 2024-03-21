package com.sgasecurity.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Controller
public class InstallationReminderController {
    @Autowired
    InstallationSiteService installationSiteService;
    InstallationSite installationSite = null;
    @Autowired
    ConfigDataService configDataService;
    ConfigData configData = null;
    String emailSubject = null;
    String emailBody = null;
    String emailHeader = null;
    String emailFooter = null;
    String emailAddress = null;
    String fullEmail = null;
    String smsBody = null;
    @Autowired
    CustomerService customerService;
    Customer customer = null;
    Common common = null;
    String smsApiUrl = null;
    String emailApiUrl = null;
    TechnicianAssignment technicianAssignment = null;
    @Autowired
    TechnicianAssignmentService technicianAssignmentService;
    Technician technician = null;
    @Autowired
    TechnicianService technicianService;
    String customerName = null;
    String date = null;
    String technicianName = null;
    String technicianEmail = null;
    String technicianPhone = null;
    String phone = null;
    String photo = null;
    String imgUrl = null;
    StringBuilder info = new StringBuilder();

    List<TechnicianAssignment> technicianAssignments;

    @Autowired
    AuditTrailService auditTrailService;

    public void captureAuditTrail(String contextName, String contextDesc, String contextValue){
        try {
            AuditTrail auditTrail = new AuditTrail();
            auditTrail.setContextName(contextName);
            auditTrail.setContextDesc(contextDesc);
            auditTrail.setContextValue(contextValue);
            auditTrailService.saveAuditTrail(auditTrail);
        } catch (Exception e){
            common.logErrors("api", "InstallationReminderController", "captureAuditTrail", "Capture Audit Trail", e.toString());
        }
    }

    @CrossOrigin
    @GetMapping("/beforeinstallationreminder")
    @ResponseBody
    public void installationReminder() {
        common = new Common();
        LocalDate today = LocalDate.now();
        try {
            configData = configDataService.getConfigDataByConfigName("URL_IMAGES");
            imgUrl = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("EMAIL_HEADER");
            emailHeader = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("EMAIL_FOOTER");
            emailFooter = configData.getConfigValue();

            try {
                technicianAssignments = technicianAssignmentService.getTechnicianAssignmentsOneDayBeforeInstallation(today);

                String contextName = "DAY_BEFORE_INSTALLATION_LIST_OF_TECHNICIAN_ASSIGNMENTS";
                String contextDesc = "Day Before Installation Of Technician Assignments";
                String contextValueJsonString = "";
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    contextValueJsonString = objectMapper.writeValueAsString(technicianAssignments);
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            } catch (Exception e){
                common.logErrors("api", "InstallationReminderController", "installationReminder", "Technician Assignments List", e.toString());
            }

            for (TechnicianAssignment technicianAssignment : technicianAssignments) {
                try {
                    installationSite = installationSiteService.getInstallationSiteByUniqueSiteId(technicianAssignment.getUniqueSiteId());
                    date = installationSite.getInstallationDate().toString();

                    String contextName = "DAY_BEFORE_INSTALLATION_SITE_DETAILS";
                    String contextDesc = "Day Before Installation Site Details Record";
                    String contextValueJsonString = "";
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        contextValueJsonString = objectMapper.writeValueAsString(installationSite);
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                } catch (Exception e) {
                    System.out.println("Error querying installation: " + e.toString());
                    common.logErrors("api", "InstallationReminderController", "installationReminder", "Error querying installation", e.toString());
//                    return "Error querying installation: " + e.toString();
                }

                try {
                    customer = customerService.getCustomerBySystemCustomerNo(installationSite.getSystemCustomerNo());
                    customerName = customer.getFirstName() + " " + customer.getLastName();

                    String contextName = "DAY_BEFORE_INSTALLATION_CUSTOMER_DETAILS";
                    String contextDesc = "Day Before Installation Customer Details Record";
                    String contextValueJsonString = "";
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        contextValueJsonString = objectMapper.writeValueAsString(customer);
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                } catch (Exception e) {
                    System.out.println("Error querying customer: " + e.toString());
                    common.logErrors("api", "InstallationReminderController", "installationReminder", "Error querying customer", e.toString());
//                    return "Error querying customer: " + e.toString();
                }

                try {
                    ConfigData configDataSms = configDataService.getConfigDataByConfigName("SMS_API_URL");
                    smsApiUrl = configDataSms.getConfigValue();
                    ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
                    emailApiUrl = configDataEmail.getConfigValue();
                } catch (Exception e) {
                    System.out.println("Error querying email and SMS URLs: " + e.toString());
                    common.logErrors("api", "InstallationReminderController", "installationReminder", "Error querying email and SMS config URLs", e.toString());
//                    return "Error querying email and SMS config URLs: " + e.toString();
                }

                try {
                    int technicianId = technicianAssignment.getTechnicianId();
                    technician = technicianService.getTechnicianById(technicianId);
                    technicianName = technician.getFirstName() + " " + technician.getLastName();
                    phone = technician.getPhone();
                    photo = technician.getPhoto();
                    technicianEmail = technician.getEmail();
                    technicianPhone = technician.getPhone();

                    String contextName = "DAY_BEFORE_INSTALLATION_TECHNICIAN_DETAILS";
                    String contextDesc = "Day Before Installation Technician Details Record";
                    String contextValueJsonString = "";
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        contextValueJsonString = objectMapper.writeValueAsString(technician);
                        System.out.println(contextValueJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                } catch (Exception e) {
                    System.out.println("Error querying technician: " + e.toString());
                    common.logErrors("api", "InstallationReminderController", "installationReminder", "Error querying technician", e.toString());
//                    return "Error querying technician: " + e.toString();
                }

                try {
                    configData = configDataService.getConfigDataByConfigName("BEFORE_INSTALLATION_EMAIL_SUBJECT");
                    emailSubject = configData.getConfigValue();
                    configData = configDataService.getConfigDataByConfigName("BEFORE_INSTALLATION_EMAIL_BODY");
                    emailBody = configData.getConfigValue();
                } catch (Exception e) {
                    common.logErrors("api", "InstallationReminderController", "installationReminder", "Get Technician And Before Installation Details", e.toString());
                }

                try {
//                  Send Email to Customer
                    emailBody = emailBody.replace("#customername", customerName);
                    emailBody = emailBody.replace("#date", date);
                    emailBody = emailBody.replace("#technicianname", technicianName);
                    emailBody = emailBody.replace("#technicianphonenumber", technicianPhone);
                    emailBody = emailBody.replace("#technicianpicture", "<br/><img style='width:128px; height:128px;' src='" + imgUrl + photo + "'/>");
                    emailAddress = customer.getEmail();

                    fullEmail = emailHeader + emailBody + emailFooter;
                    common.sendEmail(emailAddress, emailSubject, fullEmail, "1", "", emailApiUrl, "1");

                    String contextName = "DAY_BEFORE_INSTALLATION_CUSTOMER_SEND_EMAIL_DETAILS";
                    String contextDesc = "Day Before Installation Customer Send Email Details Record";
                    String contextValueString = "Email Address: " + emailAddress + ", Email Subject: " + emailSubject + ", Full Email: " + fullEmail;

                    captureAuditTrail(contextName, contextDesc, contextValueString);

                } catch (Exception e){
                    common.logErrors("api", "InstallationReminderController", "installationReminder", "Send Installation Reminder Email To Customer", e.toString());
                    System.out.println("Error sending email: " + e.toString());
//                            return "Error sending email to customer: " + e.toString();
                }

                try {
//                  Send Email to Technician
                    ConfigData configDataTechnicianEmailSubject = configDataService.getConfigDataByConfigName("TECHNICIAN_EMAIL_SUBJECT");
                    String technicianEmailSubject = configDataTechnicianEmailSubject.getConfigValue();
                    ConfigData configDataTechnicianEmailBody = configDataService.getConfigDataByConfigName("TECHNICIAN_EMAIL_BODY");
                    String technicianEmailBody = configDataTechnicianEmailBody.getConfigValue();
                    technicianEmailBody = technicianEmailBody.replace("#firstname", technicianName);
                    technicianEmailBody = technicianEmailBody.replace("#customername", customerName);
                    technicianEmailBody = technicianEmailBody.replace("#date", date);
                    common.sendEmail(technicianEmail, technicianEmailSubject, technicianEmailBody, "0", "", emailApiUrl, "0");

                    String contextName = "DAY_BEFORE_INSTALLATION_TECHNICIAN_SEND_EMAIL_DETAILS";
                    String contextDesc = "Day Before Installation Technician Send Email Details Record";
                    String contextValueString = "Email Address: " + technicianEmail + ", Email Subject: " + technicianEmailSubject + ", Full Email: " + technicianEmailBody;

                    captureAuditTrail(contextName, contextDesc, contextValueString);

                } catch (Exception e){
                    common.logErrors("api", "InstallationReminderController", "installationReminder", "Send Installation Reminder Email To Technician", e.toString());
                    System.out.println("Error sending email: " + e.toString());
//                            return "Error sending email to technician: " + e.toString();
                }

                try {
//                  Send SMS to Customer
                    configData = configDataService.getConfigDataByConfigName("BEFORE_INSTALLATION_SMS_BODY");
                    smsBody = configData.getConfigValue();
                    smsBody = smsBody.replace("#customername", customerName);
                    smsBody = smsBody.replace("#technicianname", technicianName);
                    smsBody = smsBody.replace("#technicianphonenumber", technicianPhone);
                    smsBody = smsBody.replace("#date", date);
                    common.sendSMS(customer.getPhone(), smsBody, smsApiUrl);

                    String contextName = "DAY_BEFORE_INSTALLATION_CUSTOMER_SEND_SMS_DETAILS";
                    String contextDesc = "Day Before Installation Customer Send SMS Details Record";
                    String contextValueString = "SMS Body: " + smsBody;

                    captureAuditTrail(contextName, contextDesc, contextValueString);

                } catch (Exception e) {
                    common.logErrors("api", "InstallationReminderController", "installationReminder", "Send Installation Reminder SMS To Customer", e.toString());
                    System.out.println("Failed: " + e.toString());
//                          return "ONE: "+e.toString();
                }

//                info.append("Photo: <br/><img style='width:128px; height:128px;' src='" + imgUrl + photo + "'/>\n\n Image URL: "+imgUrl+ "\n\nHeader "+emailHeader+"\n\nFooter "+emailFooter);
                try {
//                  Send SMS to Technician
                    ConfigData configDataTechnicianSMSBody = configDataService.getConfigDataByConfigName("TECHNICIAN_SMS_BODY");
                    String technicianSMSBody = configDataTechnicianSMSBody.getConfigValue();
                    technicianSMSBody = technicianSMSBody.replace("#firstname", technician.getFirstName());
                    technicianSMSBody = technicianSMSBody.replace("#customername", customerName);
                    technicianSMSBody = technicianSMSBody.replace("#date", date);
                    common.sendSMS(technicianPhone, technicianSMSBody, smsApiUrl);

                    String contextName = "DAY_BEFORE_INSTALLATION_TECHNICIAN_SEND_SMS_DETAILS";
                    String contextDesc = "Day Before Installation Technician Send SMS Details Record";
                    String contextValueString = "SMS Body: " + technicianSMSBody;

                    captureAuditTrail(contextName, contextDesc, contextValueString);

                } catch (Exception e) {
                    common.logErrors("api", "InstallationReminderController", "installationReminder", "Send Installation Reminder SMS To Technician", e.toString());
                    System.out.println("Failed: " + e.toString());
//                          return "ONE: "+e.toString();
                }

            }
            System.out.println("One Day Before installation reminder loop running...");
//              return "Before installation reminder loop running...";
//              return "Date: "+date + "<br/>Customer Name: "+customerName;
//            return "Here "+info.toString();
        } catch (Exception e) {
            common.logErrors("api", "InstallationReminderController", "installationReminder", "Send One Day Before Installation Reminder Email And SMS - Outer Error Catch", e.toString());
            System.out.println("Failed: " + e.toString());
//            return "THREE: "+e.toString();
        }
    }
}
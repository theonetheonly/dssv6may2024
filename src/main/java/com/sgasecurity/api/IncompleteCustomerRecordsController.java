package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
public class IncompleteCustomerRecordsController {
    Common common = null;
    ConfigData configData = null;
    @Autowired
    ConfigDataService configDataService;
    @Autowired
    CustomerService customerService;
    String emailAddress = null;
    String emailSubject = null;
    String emailBody = null;
    String smsApiUrl = null;
    String emailApiUrl = null;
    String smsBody = null;
    String phone = null;

    @CrossOrigin
    @GetMapping("/generateincompletecustomerrecords")
    @ResponseBody
    public void generatePdfAndSendEmail() {
        common = new Common();
        try {
            configData = configDataService.getConfigDataByConfigName("DIGITAL_MANAGER_EMAIL");
            emailAddress = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("DIGITAL_MANAGER_EMAIL_SUBJECT");
            emailSubject = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("DIGITAL_MANAGER_EMAIL_BODY");
            emailBody = configData.getConfigValue();
            ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
            emailApiUrl = configDataEmail.getConfigValue();

            try {
               List<Customer> customers = customerService.getCustomersWithNullSystemCustomerNos();

               if(customers.size() > 0){

                StringBuilder formattedString = new StringBuilder();
                String top = "<!doctype html>\n" +
                        "<html lang=\"en\">\n" +
                        "  <head>\n" +
                        "    <meta charset=\"utf-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                        "    <title>Incomplete Customer Records</title>\n" +
                        "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" crossorigin=\"anonymous\">" +
                        "  </head>\n" +
                        "  <body>\n" +
                        "<p>Hello Digital Manager,</p>" +
                        "<p>Below is the incomplete customer records.</p>" +
                        " <table class=\"table table-striped table-hover table-bordered\" style=\"border:1px solid #f2f2f2; background-color:#f8f8f8; border-radius:10px; padding:10px;\">\n" +
                        "<caption>\n" +
                        "<span style=\"font-size:14px; color: #F15921;\">Incomplete Customer Records - " + LocalDate.now() + "</span>" +
                        "</caption>\n" +
                        "  <thead style=\"background-color:#f2f2f2;\">\n" +
                        "    <tr>\n" +
                        "      <th scope=\"col\">#</th>\n" +
                        "      <th scope=\"col\">First Name</th>\n" +
                        "      <th scope=\"col\">Last Name</th>\n" +
                        "      <th scope=\"col\">Phone</th>\n" +
                        "      <th scope=\"col\">Email</th>\n" +
                        "      <th scope=\"col\">Estate Name</th>\n" +
                        "      <th scope=\"col\">Home Type</th>\n" +
                        "      <th scope=\"col\">House Door No.</th>\n" +
                        "      <th scope=\"col\">Postal Address</th>\n" +
                        "      <th scope=\"col\">Postal Code</th>\n" +
                        "      <th scope=\"col\">Primary Contact</th>\n" +
                        "      <th scope=\"col\">Town City</th>\n" +
                        "      <th scope=\"col\">Road Street</th>\n" +
                        "      <th scope=\"col\">Occupation</th>\n" +
                        "    </tr>\n" +
                        "  </thead>\n" +
                        " <tbody>";

                for (Customer customer : customers) {
                    formattedString.append("<tr><td>" + customer.getId() + "</td>")
                            .append("<td>" + customer.getFirstName() + "</td>")
                            .append("<td>" + customer.getLastName() + "</td>")
                            .append("<td>" + customer.getPhone() + "</td>")
                            .append("<td>" + customer.getEmail() + "</td>")
                            .append("<td>" + customer.getEstateName() + "</td>")
                            .append("<td>" + customer.getHomeType() + "</td>")
                            .append("<td>" + customer.getHouseDoorNo() + "</td>")
                            .append("<td>" + customer.getPostalAddress() + "</td>")
                            .append("<td>" + customer.getPostalCode() + "</td>")
                            .append("<td>" + customer.getPrimaryContact() + "</td>")
                            .append("<td>" + customer.getTownCity() + "</td>")
                            .append("<td>" + customer.getRoadStreet() + "</td>")
                            .append("<td>" + customer.getOccupation() + "</td></tr>");
                }

                String bottom = "</tbody>" +
                        " </table>  " +
                        "<p><br/>Best Regards,<br/>" +
                        "SGA Security Team.</p>" +
                        "</body>" +
                        "</html>";

                String emailBody = top.concat(formattedString.toString()).concat(bottom);

                try {
                    common.sendEmail(emailAddress, emailSubject, emailBody, "1", "", emailApiUrl, "1");
                } catch (Exception e) {
                    common.logErrors("api", "IncompleteCustomerRecordsController", "generatePdfAndSendEmail", "Send Email To Digital Manager", e.toString());
                }
              }
            } catch (Exception e){
                common.logErrors("api", "IncompleteCustomerRecordsController", "generatePdfAndSendEmail", "Send Email To Digital Manager", e.toString());
            }
        } catch (Exception e){
            common.logErrors("api", "IncompleteCustomerRecordsController", "generatePdfAndSendEmail", "Send Email To Digital Manager", e.toString());
            System.out.println(e.toString());
        }
    }

    @CrossOrigin
    @GetMapping("/deleteincompletecustomerrecords")
    @ResponseBody
    public void deleteIncompleteCustomerRecords() {
        common = new Common();
        try {
            customerService.deleteCustomersWithNullSystemCustomerNo();
        } catch (Exception e){
            common.logErrors("api", "IncompleteCustomerRecordsController", "deleteincompletecustomerrecords", "Delete Incomplete Customer Records", e.toString());
            System.out.println("No action");
        }
    }
}


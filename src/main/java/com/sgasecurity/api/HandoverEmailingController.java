package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class HandoverEmailingController {
    Common common = null;
    ConfigData configData = null;
    @Autowired
    ConfigDataService configDataService;
    @Autowired
    CustomerService customerService;
    @Autowired
    InstallationSiteService installationSiteService;
    @Autowired
    TempHandedoverCustomersTodayService tempHandedoverCustomersTodayService;
    String emailAddress = null;
    String emailSubject = null;
    String emailBody = null;
    String emailApiUrl = null;

    @CrossOrigin
    @GetMapping("/addhandedovercustomers")
    @ResponseBody
    public void addHandedoverCustomers() {
        common = new Common();

        try {
                LocalDate date = LocalDate.now();
                List<InstallationSite> installationSiteList = installationSiteService.getAllTodayInstallationsWithHandoverStatusDone(date);

                if(!installationSiteList.isEmpty()){
                    for (InstallationSite installationSite : installationSiteList) {
                        Customer customer = customerService.getCustomerBySystemCustomerNo(installationSite.getSystemCustomerNo());
                        String packageTypeName = installationSite.getPackageTypeName();
                        String packageTypeNameResult = null;
                        if(packageTypeName.equals("SAFE_HOME")){
                            packageTypeNameResult = "Safe Home";
                        } else if(packageTypeName.equals("SAFE_HOME_PLUS")) {
                            packageTypeNameResult = "Safe Home Plus";
                        } else {
                            packageTypeNameResult = "N/A";
                        }

                        if(Objects.isNull(tempHandedoverCustomersTodayService.getTempHandedOverCustomersTodayBySystemCustomerNo(customer.getSystemCustomerNo()))){
                            TempHandedoverCustomersToday tempHandedoverCustomersToday = new TempHandedoverCustomersToday();
                            tempHandedoverCustomersToday.setSystemCustomerNo(customer.getSystemCustomerNo());
                            tempHandedoverCustomersToday.setSurname(customer.getLastName());
                            tempHandedoverCustomersToday.setFirstName(customer.getFirstName());
                            tempHandedoverCustomersToday.setIdPassport(customer.getIdPassport());
                            tempHandedoverCustomersToday.setKraPin(customer.getKraPin());
                            tempHandedoverCustomersToday.setPhone(customer.getPhone());
                            tempHandedoverCustomersToday.setEmail(customer.getEmail());
                            tempHandedoverCustomersToday.setEstateName(customer.getEstateName());
                            tempHandedoverCustomersToday.setRoadStreet(customer.getRoadStreet());
                            tempHandedoverCustomersToday.setTownCity(customer.getTownCity());
                            tempHandedoverCustomersToday.setPostalCode(customer.getPostalCode());
                            tempHandedoverCustomersToday.setPostalAddress(customer.getPostalAddress());
                            tempHandedoverCustomersToday.setHomeType(customer.getHomeType());
                            tempHandedoverCustomersToday.setHouseholdNumber(customer.getHouseholdNumber());
                            tempHandedoverCustomersToday.setPackageType(packageTypeNameResult);
                            tempHandedoverCustomersToday.setHandoverDate(installationSite.getHandoverDate());
                            tempHandedoverCustomersTodayService.saveTempHandedoverCustomersToday(tempHandedoverCustomersToday);
                        } else{
                            System.out.println("Customer with customer no. " + customer.getSystemCustomerNo()+" not found");
//                            return "Customer with customer no. " + customer.getSystemCustomerNo()+" exists in the list";
                        }
                    }
                } else {
                    System.out.println("No handed over customer found...");
//                    return "No handed over customer found...";
                }
            } catch (Exception e){
                common.logErrors("api", "HandoverEmailingController", "addHandedoverCustomers", "Add Handed Over Customers To The List", e.toString());
//                return e.toString();
            }
        System.out.println("End looping...");
//        return "End looping...";
    }

    @CrossOrigin
    @GetMapping("/generatehandedovercustomerslist")
    @ResponseBody
    public String generateHandedoverCustomersList() {
        common = new Common();

        try {
            LocalDate date = LocalDate.now();
            List<TempHandedoverCustomersToday> tempHandedoverCustomersTodayList = tempHandedoverCustomersTodayService.getTempHandedoverCustomersTodayByHandoverDate(date);

            if(!tempHandedoverCustomersTodayList.isEmpty()){
//                ArrayList<StringBuilder> stringBuilderArray = new ArrayList<>();

                StringBuilder formattedString = new StringBuilder();
                String top = "<!doctype html>\n" +
                        "<html lang=\"en\">\n" +
                        "  <head>\n" +
                        "    <meta charset=\"utf-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                        "    <title>Handed Over Customers</title>\n" +
                        "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" crossorigin=\"anonymous\">" +
                        "    <style>table tr td{font-size:12px;}</style>\n" +
                        "  </head>\n" +
                        "  <body>\n" +
                        "  <br/><br/>\n" +
                        " <table class=\"table table-striped table-hover table-bordered\" style=\"border:1px solid #f2f2f2; background-color:#f8f8f8; border-radius:10px; padding:10px;\">\n" +
                        "<caption>\n" +
                        "<span style=\"font-size:15px; color: #F15921; font-weight:600;\">Handed Over Customers On " + LocalDate.now().toString() + "</span>" +
                        "</caption>\n" +
                        "  <thead style=\"background-color:#f2f2f2; font-size:12px;\">\n" +
                        "    <tr>\n" +
                        "      <th scope=\"col\">Customer No.</th>\n" +
                        "      <th scope=\"col\">Surname</th>\n" +
                        "      <th scope=\"col\">First Name</th>\n" +
                        "      <th scope=\"col\">National ID/Passport</th>\n" +
                        "      <th scope=\"col\">KRA PIN</th>\n" +
                        "      <th scope=\"col\">Phone No.</th>\n" +
                        "      <th scope=\"col\">Email</th>\n" +
                        "      <th scope=\"col\">Estate</th>\n" +
                        "      <th scope=\"col\">Road/Street</th>\n" +
                        "      <th scope=\"col\">Town/City</th>\n" +
                        "      <th scope=\"col\">Postal Address</th>\n" +
                        "      <th scope=\"col\">Postal Code</th>\n" +
                        "      <th scope=\"col\">Residence Type</th>\n" +
                        "      <th scope=\"col\">Household Occupants Total</th>\n" +
                        "      <th scope=\"col\">Package Type</th>\n" +
                        "    </tr>\n" +
                        "  </thead>\n" +
                        " <tbody>";

                for (TempHandedoverCustomersToday tempHandedoverCustomersToday : tempHandedoverCustomersTodayList) {

                    formattedString.append("<tr><td>" + tempHandedoverCustomersToday.getSystemCustomerNo() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getSurname() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getFirstName() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getIdPassport() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getKraPin() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getPhone() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getEmail() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getEstateName() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getRoadStreet() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getTownCity() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getPostalAddress() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getPostalCode() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getHomeType() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getHouseholdNumber() + "</td>")
                            .append("<td>" + tempHandedoverCustomersToday.getPackageType() + "</td></tr>");

//                            stringBuilderArray.add(formattedString);
                }

                String bottom = "<br/>" +
                        "</tbody>" +
                        " </table>  " +
                        "<p><br/>Best Regards,<br/>" +
                        "SGA Security Team.</p>" +
                        "</body>" +
                        "</html>";

                String handedOverCustomers = top.concat(formattedString.toString()).concat(bottom);

                try {
                    configData = configDataService.getConfigDataByConfigName("SANLAM_EMAIL_ADDRESS");
                    emailAddress = configData.getConfigValue();

                    configData = configDataService.getConfigDataByConfigName("HANDOVER_DURATION");
                    String handedOverDurationStr = configData.getConfigValue();
                    int handedOverDuration = Integer.parseInt(handedOverDurationStr);

                    configData = configDataService.getConfigDataByConfigName("SANLAM_HANDED_OVER_EMAIL_SUBJECT");
                    emailSubject = configData.getConfigValue();
                    configData = configDataService.getConfigDataByConfigName("SANLAM_HANDED_OVER_EMAIL_BODY");
                    emailBody = configData.getConfigValue();
                    emailBody = emailBody.replace("#handedovercustomerslist", handedOverCustomers);
                    ConfigData configDataEmail = configDataService.getConfigDataByConfigName("EMAIL_API_URL");
                    emailApiUrl = configDataEmail.getConfigValue();

                    common.sendEmail(emailAddress, emailSubject, emailBody, "1", "", emailApiUrl, "1");

                } catch (Exception e){
                    common.logErrors("api", "HandoverEmailingController", "handedoverCustomers", "Cannot send email for handed over customers to Sanlam", e.toString());
                }
            } else {
                System.out.println("No handed over customer found...");
                return "No handed over customer found...";
            }
        } catch (Exception e){
            common.logErrors("api", "HandoverEmailingController", "generateHandedoverCustomersList", "Generate Handed Over Customers List", e.toString());
            return e.toString();
        }
        return "End..";
    }
}

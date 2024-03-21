package com.sgasecurity.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
public class SOSController {
    @Autowired
    HandoverEventService handoverEventService;
    HandoverEvent handoverEvent;
    @Autowired
    CustomerService customerService;
    Customer customer = null;
    @Autowired
    EmergencyService emergencyService;
    Emergency emergency = null;
    @Autowired
    InstallationSiteService installationSiteService;
    InstallationSite installationSite;
    Common common = null;
    @Autowired
    ConfigDataService configDataService;
    ConfigData configData = null;

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
            common.logErrors("api", "SOSController", "captureAuditTrail", "Capture Audit Trail", e.toString());
        }
    }

    @CrossOrigin
    @GetMapping("/sos/request/send")
    @ResponseBody
    public String createTheSOSRequest(@RequestParam("eventid") String eventid) {
        common = new Common();
        try {


            String result = createSOSRequest(eventid);
            contextName = "BEFORE_AND_AFTER_CREATESOSREQUEST_FUNCTION_CALL";
            try {
                contextValueJsonString = "Event ID: "+eventid+ " Rescue Response: " + String.valueOf(result);
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);


            return result;
        } catch (Exception e){
            contextName = "ERROR_BEFORE_AND_AFTER_CREATESOSREQUEST_FUNCTION_CALL";
            try {
                contextValueJsonString = "Event ID: "+eventid+ " Error: " + e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            common.logErrors("api", "SOSController", "createTheSOSRequest", "Create SOS Request", e.toString());
            return "FAIL";
        }
    }

//  Create SOS Request/ Rescue emergency
//    @CrossOrigin
//    @GetMapping("/sos/request/sendtest")
//    @ResponseBody
//    public String createSOSRequest(String eventid) throws JsonProcessingException {
//
//        configData = configDataService.getConfigDataByConfigName("RESCUE_URL");
//        String rescueUrl = configData.getConfigValue();
//        configData = configDataService.getConfigDataByConfigName("RESCUECO_TOKEN");
//        String rescueToken = configData.getConfigValue();
//
//        emergency = emergencyService.getEmergencyByEventId(eventid);
//        String customerNo = emergency.getSystemCustomerNo();
//        String rescueMemberId = findPersonId(customerNo);
//        String latitude = emergency.getLatitude();
//        String longitude = emergency.getLongitude();
//        String measuredAt = emergency.getMeasuredAt().toString();
//
//        customer = customerService.getCustomerBySystemCustomerNo(customerNo);
//
//        String firstName = customer.getFirstName();
//        String lastName = customer.getLastName();
//        String otherNames = "";
//        String phoneNo = customer.getPhone();
//        phoneNo = phoneNo.replaceFirst("^\\+\\d{4}", "");
//        String address = customer.getPostalAddress() + " "+customer.getRoadStreet() +", "+customer.getTownCity();
//
//        Client client = ClientBuilder.newClient();
//        Entity payload = Entity.json("{\"source\": \"SGA App\", \"location\": {\"latitude\": \""+latitude+"\",\"longitude\": \""+longitude+"\",    \"measuredAt\": \""+measuredAt+"\",    \"provider\": \"GPS\"  },  \"phoneNumber\": {\"countryCode\": \"254\", \"description\": \"Mobile phone number.\", \"nationalNumber\": \""+phoneNo+"\"  },  \"caller\": {\"memberId\": \""+rescueMemberId+"\", \"firstName\": \""+firstName+"\",\"lastName\": \""+lastName+"\",\"otherNames\": \""+otherNames+"\"},\"address\": \""+address+"\",\"issue\":\"There appears to have been an emergency\"}");
//        Response response = client.target(rescueUrl+"/sos_requests")
//                .request(MediaType.APPLICATION_JSON_TYPE)
//                .header("authorization", "Bearer "+rescueToken)
//                .post(payload);
//
////      Save the response in database
//        int rescueResponseStatus = response.getStatus();
//        String rescueResponseHeaders = response.getHeaders().toString();
//        String rescueResponseBody = response.readEntity(String.class);
//
//        emergency.setRescueResponseStatus(rescueResponseStatus);
//        emergency.setRescueResponseHeaders(rescueResponseHeaders);
//        emergency.setRescueResponseBody(rescueResponseBody);
//        emergencyService.saveEmergency(emergency);
//
//        return "SUCCESS";
//
////        return "Status: " + response.getStatus() + "\n\n" + "Headers: " + response.getHeaders() + "\n\n" + "Body:" + response.readEntity(String.class);
//    }

    public String createSOSRequest(String eventid) throws JsonProcessingException {

        try {

            Response response = null;

            ObjectMapper OM  = new ObjectMapper();

            String rescueMemberId ="";

            configData = configDataService.getConfigDataByConfigName("RESCUE_URL");
            String rescueUrl = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("RESCUECO_TOKEN");
            String rescueToken = configData.getConfigValue();

            String latitude = "";  // emergency.getLatitude();
            String longitude = ""; // emergency.getLongitude();
            String measuredAt = ""; //emergency.getMeasuredAt().toString();

            String firstName = ""; // customer.getFirstName();
            String lastName = ""; // customer.getLastName();
            String otherNames = "";
            String phoneNo = ""; // customer.getPhone();
            String address ="";


            int steps =0;
            try {
                emergency = emergencyService.getEmergencyByEventId(eventid);
                String emergencyData =  OM.writeValueAsString(OM);


                latitude = emergency.getLatitude();
                longitude = emergency.getLongitude();
                measuredAt = emergency.getMeasuredAt().toString();



                contextName = "CREATESOSREQUEST_FUNCTION_CALL_EMERGENCY_DATA";
                try {
                    contextValueJsonString = "Event ID: "+eventid+ " Emergency Data: " + String.valueOf(emergencyData);
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
                
            }
            catch (Exception ex)
            {
                contextName = "ERROR_CREATESOSREQUEST_FUNCTION_CALL_EMERGENCY_DATA";
                try {
                    contextValueJsonString = "Event ID: "+eventid+ " Error: " +ex.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }


            try {
                String customerNo = emergency.getSystemCustomerNo();
                customer = customerService.getCustomerBySystemCustomerNo(customerNo);
                rescueMemberId = customer.getRescueMemberId();
                //            String rescueMemberId = findPersonId(customerNo);
             /*   String latitude = emergency.getLatitude();
                String longitude = emergency.getLongitude();
                String measuredAt = emergency.getMeasuredAt().toString();
               */

                 firstName = customer.getFirstName();
                 lastName = customer.getLastName();
                 otherNames = "";
                 phoneNo = customer.getPhone();
                phoneNo = phoneNo.replaceFirst("^\\+\\d{4}", "");
                address = customer.getPostalAddress() + " "+customer.getRoadStreet() +", "+customer.getTownCity();

                String customerData = OM.writeValueAsString(customer);
                contextName = "CREATESOSREQUEST_FUNCTION_CALL_CUSTOMER_DATA";
                try {
                    contextValueJsonString = "Event ID: "+eventid+ " Customer Data: " + customerData;
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            }
            catch (Exception ex)
            {

                contextName = "ERROR_CREATESOSREQUEST_FUNCTION_CALL_CUSTOMER_DATA";
                try {
                    contextValueJsonString = "Event ID: "+eventid+ " Error: " + ex.toString();
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);
            }




            String payloadString = "{\"source\": \"SGA App\", \"location\": {\"latitude\": \"" + latitude + "\",\"longitude\": \"" + longitude + "\",    \"measuredAt\": \"" + measuredAt + "\",    \"provider\": \"GPS\"  },  \"phoneNumber\": {\"countryCode\": \"254\", \"description\": \"Mobile phone number.\", \"nationalNumber\": \"" + phoneNo + "\"  },  \"caller\": {\"memberId\": \"" + rescueMemberId + "\", \"firstName\": \"" + firstName + "\",\"lastName\": \"" + lastName + "\",\"otherNames\": \"" + otherNames + "\"},\"address\": \"" + address + "\",\"issue\":\"SGA Kenya Customer Emergency Call\"}";

            try {
                Client client = ClientBuilder.newClient();

                Entity payload = Entity.json(payloadString);
//                Entity payload = Entity.json("{\"source\": \"SGA App\", \"location\": {\"latitude\": \"" + latitude + "\",\"longitude\": \"" + longitude + "\",    \"measuredAt\": \"" + measuredAt + "\",    \"provider\": \"GPS\"  },  \"phoneNumber\": {\"countryCode\": \"254\", \"description\": \"Mobile phone number.\", \"nationalNumber\": \"" + phoneNo + "\"  },  \"caller\": {\"memberId\": \"" + rescueMemberId + "\", \"firstName\": \"" + firstName + "\",\"lastName\": \"" + lastName + "\",\"otherNames\": \"" + otherNames + "\"},\"address\": \"" + address + "\",\"issue\":\"SGA Kenya Customer Emergency Call\"}");

                String rescuePayloadURL = rescueUrl + "/sos_requests";
                response = client.target(rescuePayloadURL)
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .header("authorization", "Bearer " + rescueToken)
                        .post(payload);

                try {
                    Thread.sleep(2000);
                }
                catch (Exception et)
                {

                }
                contextName = "INVOKE_RESCUE_EMERGENCY_REQUEST_API";
                try {
                    contextValueJsonString = "Event ID: "+eventid+ " Payload: " + payloadString +" Response : " +String.valueOf(response) ;
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);


            }
            catch (Exception ex)
            {

                contextName = "ERROR_INVOKE_RESCUE_EMERGENCY_REQUEST_API";
                try {
                    contextValueJsonString = "Event ID: "+eventid+ " Payload String: "+ payloadString+ " Error : " +ex.toString() ;
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);


            }


//      Save the response in database
            try {
                int rescueResponseStatus = response.getStatus();
                String rescueResponseHeaders = response.getHeaders().toString();
                String rescueResponseBody = response.readEntity(String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(rescueResponseBody);
                String callerId = rootNode.get("id").asText();
                String rescueCallStatus = rootNode.get("status").asText();


                emergency.setRescueResponseStatus(rescueResponseStatus);
                emergency.setRescueResponseHeaders(rescueResponseHeaders);
                emergency.setRescueResponseBody(rescueResponseBody);
                emergency.setRescueCallerId(callerId);
                emergency.setRescueCallStatus(rescueCallStatus);
                emergencyService.saveEmergency(emergency);

                contextName = "UPDTATE_EMERGENCY_REQUEST_RESPONSE_IN_DB";
                try {
                    contextValueJsonString = "Event ID: "+eventid+ " Response Headers: " + rescueResponseHeaders+ " Response Body: "+ rescueResponseBody;
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);


            }
            catch (Exception ex)
            {

                contextName = "ERROR_UPDTATE_EMERGENCY_REQUEST_RESPONSE_IN_DB";
                try {
                    contextValueJsonString = "Event ID: "+eventid+ " Error : " +ex.toString() ;
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);



            }


            contextName = "CREATESOSREQUEST_FUNCTION_CALL";
            try {
                contextValueJsonString = "Event ID: "+eventid+ " Rescue Response: " + String.valueOf(response);
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

//          return "Status: " + response.getStatus() + "\n\n" + "Headers: " + response.getHeaders() + "\n\n" + "Body:" + response.readEntity(String.class);
            return "SUCCESS";

        } catch (Exception e){


            contextName = "ERROR_IN CREATESOSREQUEST_FUNCTION_CALL";
            try {
                contextValueJsonString = "ERROR: "+e.toString() ;
                System.out.println(contextValueJsonString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);




            return "FAIL";
        }
    }

    @CrossOrigin
    @GetMapping("/sos/process/request/status")
    @ResponseBody
    public  Map<String, String> processSOSRequestStatus(String eventid) throws JsonProcessingException {

        Map<String, String> map = new HashMap<>();
        try {
            configData = configDataService.getConfigDataByConfigName("RESCUE_URL");
            String rescueUrl = configData.getConfigValue();
            configData = configDataService.getConfigDataByConfigName("RESCUECO_TOKEN");
            String rescueToken = configData.getConfigValue();

            emergency = emergencyService.getEmergencyByEventId(eventid);

            contextName = "CHECKING_SOS_CALL_STATUS";
            try {
                contextValueJsonString = "STARTING SOS FOR EVENT ID : "+eventid;
                System.out.println(contextValueJsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);


            if(!Objects.isNull(emergency)){

                String rescueResponseBody = emergency.getRescueResponseBody();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(rescueResponseBody);
                String id = rootNode.get("id").asText();

                Client client = ClientBuilder.newClient();

                String rescueFullUri = rescueUrl+"/sos_requests/"+id;
                Response response = client.target(rescueFullUri)
                        .request(javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE)
                        .header("Authorization", "Bearer " + rescueToken)
                        .get();

                String jsonResponse = response.readEntity(String.class);

                ObjectMapper objectMapperSOS = new ObjectMapper();
                JsonNode rootNodeSOS = objectMapperSOS.readTree(jsonResponse);
                String status = rootNodeSOS.get("status").asText();

                contextName = "EXECUTED_RESCUE_API_URL";
                try {
                    contextValueJsonString = "Event ID: "+ eventid+" - Rescue Full URL: "+ rescueFullUri + " - Respons Status :"+status +" Raw response: "+ String.valueOf(jsonResponse);
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);



                if(status.equals("CLOSED")){
                    map.put("status", "NOT_ONGOING");
                    map.put("rescueresponse", status);
                    map.put("extrainfo", "");
                    return map;
                } else if(status.equals("CURRENT")){
                    map.put("status", "ONGOING");
                    map.put("rescueresponse", status);
                    map.put("extrainfo", "");
                    return map;
                } else {
                    map.put("status", "NOT_ONGOING");
                    map.put("rescueresponse", status);
                    return map;
                }
            } else {

                contextName = "STARTING_SOS_CALL_NULL_EMERGENCY_OBJECT";
                try {
                    contextValueJsonString = "Event ID: "+ eventid+" - Null emergency object";
                    System.out.println(contextValueJsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                captureAuditTrail(contextName, contextDesc, contextValueJsonString);

                map.put("status", "NOT_ONGOING");
                map.put("rescueresponse", "");
                map.put("extrainfo", "Emergency Object Empty");
                return map;
            }
        } catch (Exception e){


            contextName = "ERROR_STARTING_SOS_CALL";
            try {
                contextValueJsonString = "Event ID: "+ eventid+" - Error: -- "+e.toString();
                System.out.println(contextValueJsonString);
            } catch (Exception ez) {
                ez.printStackTrace();
            }
            captureAuditTrail(contextName, contextDesc, contextValueJsonString);

            map.put("status", "FAILED");
            map.put("rescueresponse", "");
            map.put("extrainfo", "Error: "+e.toString());
            return map;
        }
    }

    @CrossOrigin
    @ResponseBody
    @GetMapping("/sos/checksoscallstatus")
    public String checkSosCallStatus() {
        common = new Common();
        try {
            ConfigData configDataApiAppUrl = configDataService.getConfigDataByConfigName("API_APP_URL");
            String apiAppUrl = configDataApiAppUrl.getConfigValue();

            List<Emergency> emergencyList = emergencyService.getAllEmergencies();

            System.out.println("Number of emergency records found: " + emergencyList.size());

            if (emergencyList.size() > 0) {
                for (Emergency emergency1 : emergencyList) {

                    String rescueResponseBody = emergency1.getRescueResponseBody();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(rescueResponseBody);
                    String id = rootNode.get("id").asText();

//                  Update SOS call status
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    String url = apiAppUrl + "/sos/checkstatus?id="+id;
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

                    return response.getBody();
                }
            } else {
//                System.out.println("No emergency record found...\n");
                return "No emergency record found...<br/>";
            }
        } catch (Exception e){
            common.logErrors("api", "SOSController", "checkSosCallStatus", "Check SOS Call Status", e.toString());
//            System.out.println("FAIL: " + e.toString()+"\n");
            return "FAIL: " + e.toString()+"\n";
        }
        return "Running...";
    }

    @CrossOrigin
    @GetMapping("/sos/checkstatus")
    @ResponseBody
    public String checkSosStatus(@RequestParam String id) throws JsonProcessingException {
        common = new Common();
        try {
            configData = configDataService.getConfigDataByConfigName("RESCUE_URL");
            String rescueUrl = configData.getConfigValue();

            configData = configDataService.getConfigDataByConfigName("RESCUECO_TOKEN");
            String rescueToken = configData.getConfigValue();

            Client client = ClientBuilder.newClient();

            Response response = client.target(rescueUrl+"/sos_requests/"+id)
                    .request(javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "Bearer " + rescueToken)
                    .get();

            String jsonResponse = response.readEntity(String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            return  rootNode.get("status").asText();

        } catch (Exception e){
            common.logErrors("api", "SOSController", "checkSosStatus", "Check SOS Status", e.toString());
            return "FAIL: - SOS Request ID "+id+" : "+e.toString()+"\n";
        }
    }

    @CrossOrigin
    @GetMapping("/sos/findpersonid")
    @ResponseBody
    public String findPersonId(String customerNo) throws JsonProcessingException {
      common = new Common();
      try {
          configData = configDataService.getConfigDataByConfigName("RESCUE_URL");
          String rescueUrl = configData.getConfigValue();

          configData = configDataService.getConfigDataByConfigName("RESCUECO_TOKEN");
          String rescueToken = configData.getConfigValue();

          Client client = ClientBuilder.newClient();

          Response response = client.target(rescueUrl+"/memberships")
                  .request(javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE)
                  .header("Authorization", "Bearer " + rescueToken)
                  .get();

          String jsonResponse = response.readEntity(String.class);
          JsonNode jsonNode = null;
          ObjectMapper objectMapper = new ObjectMapper();
          JsonNode[] jsonNodes = objectMapper.readValue(jsonResponse, JsonNode[].class);
          String alternativeIdentifier = null;
          for (JsonNode obj : jsonNodes) {
               alternativeIdentifier = objectMapper
                      .convertValue(obj, Map.class)
                      .toString();

              if (alternativeIdentifier.contains(customerNo.toString())) {
                  jsonNode = obj;
                  break;
              }
          }
          JsonNode rootNode = objectMapper.readTree(jsonNode.toString());
          String id = rootNode.get("mainMember").get("id").asText();

          return id;

      } catch (Exception e){
          common.logErrors("api", "SOSController", "findPersonId", "Find Person Id", e.toString());
          return "FAIL " + e.toString();
      }
    }

    @CrossOrigin
    @GetMapping("/rescue/memberships/list")
    @ResponseBody
    public String listRescueMembership() {
        common = new Common();
        try {
            return listMemberships();
        } catch (Exception e) {
            common.logErrors("api", "SOSController", "listRescueMembership", "List Rescue Membership", e.toString());
            return "FAIL";
        }
    }

    public String listMemberships() throws JsonProcessingException {

        configData = configDataService.getConfigDataByConfigName("RESCUE_URL");
        String rescueUrl = configData.getConfigValue();

        configData = configDataService.getConfigDataByConfigName("RESCUECO_TOKEN");
        String rescueToken = configData.getConfigValue();

        Client client = ClientBuilder.newClient();

        Response response = client.target(rescueUrl+"/memberships")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization","Bearer "+rescueToken)
                .header("User-Agent","SGA Panic Button")
                .get();

        return response.readEntity(String.class);
    }
}

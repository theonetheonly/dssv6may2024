package com.sgasecurity.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgasecurity.api.device.Data;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Common {
    public String doBase64Encode(String dataToEncode) {
        try {
            // Encoding
            String encodedString = Base64.getEncoder().encodeToString(dataToEncode.getBytes());
            return encodedString;
        } catch (Exception e){
            logErrors("api", "Common", "doBase64Encode", "Do Base64 Encode", e.toString());
            return "FAIL";
        }
    }

    public String doBase64Decode(String dataToDecode) {
        try {
            // Decoding
            byte[] decodedBytes = Base64.getDecoder().decode(dataToDecode);
            String decodedString = new String(decodedBytes);
            return decodedString;
        } catch (Exception e){
            logErrors("api", "Common", "doBase64Decode", "Do Base64 Decode", e.toString());
            return "Error: " + e.toString();
        }
    }

    public void sendEmail(String email, String subject, String message, String html, String cc, String url, String base64Encode)
    {
        try {
            if(base64Encode == "1"){
                message = doBase64Encode(message);
            }

            Map<String, Object> post_data =  new LinkedHashMap<>();
            post_data.put("email",email);
            post_data.put("subject",subject);
            post_data.put("message", message);
            post_data.put("html",html);
            post_data.put("accesskey","sga2022now");
            post_data.put("base64decode",base64Encode);

            NetworkActivity networkActivity = new NetworkActivity();
            networkActivity.executeURL(url, post_data);
        }
        catch (Exception e)
        {
            logErrors("api", "Common", "sendEmail", "Send Email", e.toString());
        }
    }

    public void sendSMS(String mobilephone, String message, String url) throws IOException {
        try {
            Map<String, Object> post_data = new LinkedHashMap<>();
            post_data.put("mobile", mobilephone);
            post_data.put("message", message);
            post_data.put("gatewayip", "192.168.0.1");
            post_data.put("accesscode", "4321");
            NetworkActivity networkActivity = new NetworkActivity();
            networkActivity.smsExecuteURL(url, post_data);

        } catch (Exception e) {
            logErrors("api", "Common", "sendSMS", "Send SMS", e.toString());
        }
    }

    //  Get HIK token
    public String getHikToken(String appKey, String secretKey, String url) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        String requestBody = "{\"appKey\":\"" + appKey + "\",\"secretKey\":\"" + secretKey + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody().toString());
        String accessToken = jsonNode.get("data").get("accessToken").asText();

        return accessToken;
    }

    public String shortenAnyUrl(String urlToBeShortened, String token) {
        try {
            String apiUrl = "https://api-ssl.bitly.com/v4/shorten";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            String requestBody = "{\"long_url\":\"" + urlToBeShortened + "\"}";
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseData = response.getBody();
                JsonNode responseJson = objectMapper.readTree(responseData);
                String shortUrl = responseJson.get("id").asText();
                return shortUrl;
            } else {
                String error = "Bitly API error occurred";
                logErrors("api", "Common", "shortenAnyUrl", "Shorten Url", error + "(if else block)");
                return error;
            }
        } catch (Exception e) {
            logErrors("api", "Common", "shortenAnyUrl", "Shorten Url", e.toString());
            return e.toString();
        }
    }

    public String shortenUrl(String urlToBeShortened, String token, String apiUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            String requestBody = "{\"long_url\":\"" + urlToBeShortened + "\"}";
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseData = response.getBody();
                JsonNode responseJson = objectMapper.readTree(responseData);
                String shortUrl = responseJson.get("id").asText();
                return shortUrl;
            } else {
                String error = "Bitly API error occurred";
                logErrors("api", "Common", "shortenUrl", "Shorten Url", error + "(if else block)");
                return error;
            }
        } catch (Exception e) {
            logErrors("api", "Common", "shortenUrl", "Shorten Url", e.toString());
            return e.toString();
        }
    }

    public String getUrlToBeShortened(String textBody, String searchString) {
        try {
            int startIndex = textBody.indexOf(searchString);
            if (startIndex != -1) {
                int endIndex = startIndex + searchString.length();
                String extractedString = textBody.substring(startIndex, endIndex);
                return extractedString;
            } else {
                logErrors("api", "Common", "getUrlToBeShortened", "Get Url To Be Shortened", "String not found (if else block)");
                return null;
            }
        } catch (Exception e){
            logErrors("api", "Common", "getUrlToBeShortened", "Get Url To Be Shortened", e.toString());
            return e.toString();
        }
    }

//    this.customerId = customerId;
//        this.dynamicCompanyRef = dynamicCompanyRef;
//        this.companyToken = companyToken;

    public DPOPaymentRequest getDPORequest(String firstName, String lastName, String townCity, String fullName, String email, String phone, String packageTypeName, double paymentAmount, String dpoRedirectUrl, String dpoBackUrl, double vat, String dpoMarkup, int expiryPeriod, String tokenId, int customerId, String companyRef, String companyToken, String dpoCurrency, String dssCountry, String dpoServiceType) {
        try {
            String invoicePeriod = LocalDate.now().toString();
            DPOPaymentRequest dpoPaymentRequest = new DPOPaymentRequest();
            dpoPaymentRequest.setPaymentAmount(paymentAmount);
            dpoPaymentRequest.setPaymentCurrency(dpoCurrency);
            dpoPaymentRequest.setCompanyRef(companyRef);
            dpoPaymentRequest.setRedirectURL(dpoRedirectUrl);
            dpoPaymentRequest.setBackURL(dpoBackUrl);
            dpoPaymentRequest.setCompanyRefUnique("0");
            dpoPaymentRequest.setPTL(expiryPeriod);
            dpoPaymentRequest.setPTLType("NA");
            dpoPaymentRequest.setDefaultPayment(String.valueOf(paymentAmount));
            dpoPaymentRequest.setAllowRecurrent("NA");
            dpoPaymentRequest.setCustomerFirstName(firstName);
            dpoPaymentRequest.setCustomerLastName(lastName);
            dpoPaymentRequest.setCustomerCity(townCity);
            dpoPaymentRequest.setCustomerCountry(dssCountry); // Put as config
            dpoPaymentRequest.setCardHolderName(fullName);
            dpoPaymentRequest.setCustomerEmail(email);
            dpoPaymentRequest.setCustomerPhone(phone);
            dpoPaymentRequest.setDefaultPaymentCountry(dssCountry); // Put as config
            dpoPaymentRequest.setPackageName(packageTypeName);
            dpoPaymentRequest.setVat(vat);
            dpoPaymentRequest.setInvoicePeriod(invoicePeriod);
            dpoPaymentRequest.setDPOMarkup(dpoMarkup);
            dpoPaymentRequest.setServiceType(dpoServiceType);
            dpoPaymentRequest.setCompanyToken(companyToken);
            dpoPaymentRequest.setTokenId(tokenId);

            return dpoPaymentRequest;

        } catch (Exception e){
           logErrors("api", "Common", "getDPORequest", "Get DPO Request", e.toString());
           return null;
        }
    }

//    public String getInvoicePeriod() {
//        LocalDate startingDate = LocalDate.now();
//        LocalDate nextPaymentDate = startingDate.plusDays(30);
//        return startingDate.toString() + " " + nextPaymentDate.toString();
//    }

    //  Enable or disable the site
    public boolean device(String siteId, boolean cloudEnable, String hikUrl, String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer "+token);

            String requestBody = "{\n" +
                    "                \"siteId\": \""+siteId+"\",\n" +
                    "                \"cloudEnable\": "+cloudEnable+"\n" +
                    "            }";

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(hikUrl, HttpMethod.POST, entity, String.class);

            if((response.getBody()).equals("0")){
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            logErrors("api", "Common", "device", "Enable Or Disable The Site", e.toString());
            return false;
        }
    }

    public String arc(String siteId, String url, String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer "+token);

            String requestBody = "{\n" +
                    " \"page\": 1,\n" +
                    " \"pageSize\": 20\n" +
                    "}";

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
            Data data = deviceData.getData();
            List<DeviceRow> rows = data.getRows();

            for (DeviceRow row : rows) {
                if((row.getDeviceType()).equals(targetDeviceType) && (row.getSiteID()).equals(siteId)){
                    deviceSerial = row.getDeviceSerial();
                    break;
                }
            }

            return deviceSerial;

        } catch (Exception e) {
            logErrors("api", "Common", "arc", "Enable Or Disable The ARC", e.toString());
            return "ERROR " + e.toString();
        }
    }

    public ResponseEntity<String> convertToJSON(Object obj, HttpHeaders responseHeaders) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            json = e.toString();
        }
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(json);
    }

    public boolean disableARC(String deviceSerial, String url, String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer "+token);

            String requestBody = "{\"deviceSerial\":\""+deviceSerial+"\"}";

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if(response.getBody().equals("0")){
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            logErrors("api", "Common", "disableARC", "Disable The ARC", e.toString());
            return false;
        }
    }

    public void logErrors(String directory, String file, String function, String errorTitle, String errorMessage) {
        String filePath = "/opt/dssfiles/dsserrors.log";
        try {
            FileWriter writer = new FileWriter(filePath, true);
            LocalDateTime now = LocalDateTime.now();
            writer.write("["+now+"] ["+directory+":"+file+":"+function+"] ["+errorTitle+"] - "+errorMessage);
            writer.write("\n");
            writer.close();
        } catch (IOException e) {

        }
    }

    public String getTheTimestamp() {
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            return formattedDateTime;
        } catch (Exception e){
            return "UNKNOWN_TIME";
        }
    }

    public JsonNode changeIPRPStatus(String accountID, String deviceName, String deviceIndex, String status, String username, String pwd, String url){

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

        // Create an HttpPut request
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

}
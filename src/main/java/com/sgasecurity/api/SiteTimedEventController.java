package com.sgasecurity.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class SiteTimedEventController {
    @Autowired
    private InstallationSiteRepository installationSiteRepository;
    @Autowired
    private SiteTimedEventRepository siteTimedEventRepository;

    @Autowired
    private TimedEventsTrackerRepository timedEventsTrackerRepository;
    @Autowired
    ConfigDataService configDataService;
    ConfigData configData = null;
    Common common = null;
    @Autowired
    NewMonthService newMonthService;
    NewMonth newMonth = null;
    @Autowired
    SiteTimedEventService siteTimedEventService;
    SiteTimedEvent siteTimedEvent = null;
    String apiAppUrl = null;

    @CrossOrigin
    @ResponseBody
    @GetMapping("/timedeventsim")
    public String executeTimerEventsLoop()
    {
        common = new Common();

        try {
          configData = configDataService.getConfigDataByConfigName("API_APP_URL");
          apiAppUrl = configData.getConfigValue();
        } catch (Exception e){
            common.logErrors("api", "SiteTimedEventController", "executeTimerEventsLoop", "Fetch API App URL", e.toString());
        }

        try{
            TimedEventsTracker timedEventsTracker = new TimedEventsTracker();

            RestTemplate restTemplate = new RestTemplate();

            configData = configDataService.getConfigDataByConfigName("TPLUS0");
            long TPLUS0 = Long.parseLong(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("TPLUS24");
            long TPLUS24 = Long.parseLong(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("TPLUS552");
            long TPLUS552 = Long.parseLong(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("TPLUS720");
            long TPLUS720 = Long.parseLong(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("TOLERANCE");
            long tolerance = Long.parseLong(configData.getConfigValue());

            // Look at the installations table and get unique_site_ids and their installation_dates
            Date current_time  =  new Date();

            timedEventsTracker.setContextName("CURRENT_TIME");
            timedEventsTracker.setContextValue(current_time.toString());
//            timedEventsTrackerRepository.save(timedEventsTracker);

            List<InstallationSite> installationSitesList =  installationSiteRepository.getInstallationSitesReady("DONE");

            System.out.println("Installation sites found: "+installationSitesList.size());


            if (installationSitesList.size() > 0)
            {
                timedEventsTracker = new TimedEventsTracker();
                timedEventsTracker.setContextName("INSTALLATION_SITES_COUNT");
                timedEventsTracker.setContextValue(Integer.toString(installationSitesList.size()));
                timedEventsTrackerRepository.save(timedEventsTracker);

                // Loop through each installation site
                for (InstallationSite installationSite: installationSitesList) {
                    String unique_site_id = installationSite.getUniqueSiteId();
                    SiteTimedEvent siteTimedEventMain = siteTimedEventRepository.getSiteTimedEventRecordbySiteID(unique_site_id);

                    timedEventsTracker = new TimedEventsTracker();
                    timedEventsTracker.setContextName("START_UNIQUE_SITE_ID");
                    timedEventsTracker.setContextValue(unique_site_id);
                    timedEventsTrackerRepository.save(timedEventsTracker);

                    if (siteTimedEventMain != null) {
                        // Loop through and get the values of this record
                        int currentNumericPosition = siteTimedEventMain.getLastTimedEventNumericPosition();
                        int month_count = siteTimedEventMain.getMonthCounter();

                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("SITE_TIMED_MAIN_EVENT");
                        timedEventsTracker.setContextValue(unique_site_id+ " >> NOT_NULL");
                        timedEventsTrackerRepository.save(timedEventsTracker  );

                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("MONTH_COUNT");
                        timedEventsTracker.setContextValue(unique_site_id+ " >> " + Long.toString(month_count));
                        timedEventsTrackerRepository.save(timedEventsTracker);

                        Date reference_trigger_date_time = siteTimedEventMain.getReferenceTriggerDateTime();

                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("REFERENCE_DATE_TIME_TRIGGER");
                        timedEventsTracker.setContextValue(unique_site_id + " >> " +reference_trigger_date_time.toString());
                        timedEventsTrackerRepository.save(timedEventsTracker);

                        Date last_timed_event_executed_datetime = siteTimedEventMain.getLastTimedEventExecutedDatetime();

                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("LAST_TIMED_EVENT_EXECUTED_DATE_TIME");
                        timedEventsTracker.setContextValue(unique_site_id +" >> " +last_timed_event_executed_datetime.toString());
                        timedEventsTrackerRepository.save(timedEventsTracker);

                        long ct = current_time.getTime();
                        long rtdt = reference_trigger_date_time.getTime();
                        long TPLUS720_in_milliseconds = TPLUS720*60*1000;
                        long updated_ref_time = rtdt + ((TPLUS720_in_milliseconds) * (month_count - 1));

                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("UPDATED_REF_TIME");
                        timedEventsTracker.setContextValue(unique_site_id + ">> " +Long.toString(updated_ref_time));
                        timedEventsTrackerRepository.save(timedEventsTracker);

                        System.out.println("UPDATED REF TIME: "+updated_ref_time);
                        long elapsed_time = current_time.getTime() - updated_ref_time;

                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("ELAPSED_TIME_MILLISECONDS");
                        timedEventsTracker.setContextValue(unique_site_id + " >> " + Long.toString(elapsed_time));
                        timedEventsTrackerRepository.save(timedEventsTracker);

                        long divide_denominator = 1000 * 60;
                        long elapsed_time_minutes = elapsed_time/divide_denominator;



                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("ELAPSED_TIME_MINUTES");
                        timedEventsTracker.setContextValue(unique_site_id+ " >> "+ Long.toString(elapsed_time_minutes));
                        timedEventsTrackerRepository.save(timedEventsTracker);


                        long elapsed_time_hours =  elapsed_time_minutes /60;

                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("ELAPSED_TIME_HOURS");
                        timedEventsTracker.setContextValue(unique_site_id+ " >> "+ Long.toString(elapsed_time_hours));
                        timedEventsTrackerRepository.save(timedEventsTracker);

                        long elapsed_time_days = elapsed_time_hours / 24;

                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("ELAPSED_TIME_DAYS");
                        timedEventsTracker.setContextValue(unique_site_id+ " >> "+ Long.toString(elapsed_time_days));
                        timedEventsTrackerRepository.save(timedEventsTracker);

                        System.out.println("ELAPSED TIME IN MINUTES: "+elapsed_time_minutes);

                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("CURRENT_NUMERIC_POSITION");
                        timedEventsTracker.setContextValue(unique_site_id + " >> " +Long.toString(currentNumericPosition));
                        timedEventsTrackerRepository.save(timedEventsTracker);

                        long tplus24ToleranceDiffLower = tolerance - TPLUS24;
                        long tplus24ToleranceDiffUpper = tolerance + TPLUS24;

                        switch (currentNumericPosition)
                        {
                            case 0:
                                if (elapsed_time_minutes >= (TPLUS24 - tolerance)  && elapsed_time_minutes <= (TPLUS24 + tolerance))
                                {
                                    timedEventsTracker = new TimedEventsTracker();
                                    timedEventsTracker.setContextName("TPLUS24-TOLERANCE-EXECUTED");
                                    timedEventsTracker.setContextValue(unique_site_id+ " >> "+ String.valueOf(tplus24ToleranceDiffLower).concat(" --- ").concat(String.valueOf(tplus24ToleranceDiffUpper).concat(" --- ").concat(String.valueOf(TPLUS24))));
                                    timedEventsTrackerRepository.save(timedEventsTracker);

                                    // Do update code tor 24hr
                                    siteTimedEventMain.setLastTimedEventNumericPosition(1);
                                    siteTimedEventMain.setLastTimedEventExecutedName("TPLUS24");
                                    siteTimedEventMain.setLastTimedEventExecutedDatetime(current_time);
                                    siteTimedEventRepository.save(siteTimedEventMain);

                                    boolean ctlps = checkTimerLoopPaymentStatus(unique_site_id);

                                    timedEventsTracker = new TimedEventsTracker();
                                    timedEventsTracker.setContextName("PAYMENT-STATUS-TPLUS24");
                                    timedEventsTracker.setContextValue(unique_site_id+" >> "+Boolean.toString(ctlps));
                                    timedEventsTrackerRepository.save(timedEventsTracker);

                                    if(ctlps){
                                        // Payment is done, no need to do anything except check HIK site enable/disable status
                                        // If site is diasbled - enable and exit case


                                        timedEventsTracker = new TimedEventsTracker();
                                        timedEventsTracker.setContextName("PAYMENT-STATUS-TPLUS24");
                                        timedEventsTracker.setContextValue(unique_site_id+" >> ALREADY PAID ");
                                        timedEventsTrackerRepository.save(timedEventsTracker);
                                        System.out.println("24hr paid already... do nothing");

                                    } else {
                                        timedEventsTracker = new TimedEventsTracker();
                                        timedEventsTracker.setContextName("PAYMENT-STATUS-TPLUS24");
                                        timedEventsTracker.setContextValue(unique_site_id+" >> NOT PAID SHOULD DISCONNECT ");
                                        timedEventsTrackerRepository.save(timedEventsTracker);
                                        System.out.println("24hr paid already... do nothing");

                                        // Send 24hr notification
 //
                                        //                                     EXECUTE THE T24 RELATED TASKS NOW -  EG SEND MAIL
//                                      String apiUrl = apiAppUrl+"/send24hrcomms?uniqueSiteId=" + unique_site_id;
                                        String apiUrl = apiAppUrl+"/send24hrcomms?uniqueSiteId="+unique_site_id;
                                        String response = restTemplate.getForObject(apiUrl, String.class);
                                        if (response.equals("SUCCESS")) {

                                            timedEventsTracker = new TimedEventsTracker();
                                            timedEventsTracker.setContextName("SEND-TPLUS24");
                                            timedEventsTracker.setContextValue(unique_site_id+ " >> "+"SUCCESS");
                                            timedEventsTrackerRepository.save(timedEventsTracker);

                                            System.out.println("Successfully sent 24 hour email and SMS");
                                        } else {

                                            timedEventsTracker = new TimedEventsTracker();
                                            timedEventsTracker.setContextName("SEND-TPLUS24");
                                            timedEventsTracker.setContextValue(unique_site_id + ">> FAIL");
                                            timedEventsTrackerRepository.save(timedEventsTracker);

                                            common.logErrors("api", "SiteTimedEventController", "executeTimerEventsLoop", "Send 24 Hour Email And SMS", "Failed to send 24 hour email and SMS (if else block)");
                                            System.out.println("Failed to send 24 hour email and SMS");
                                        }
                                    }
                                }
                                break;
                            case 1:
                                if (elapsed_time_minutes >= (TPLUS552 -tolerance)  && elapsed_time_minutes <= (TPLUS552 + tolerance))
                                {
//                                  Do update code tor 552hr
                                    siteTimedEventMain.setLastTimedEventNumericPosition(2);
                                    siteTimedEventMain.setLastTimedEventExecutedName("TPLUS552");
                                    siteTimedEventMain.setLastTimedEventExecutedDatetime(current_time);
                                    siteTimedEventRepository.save(siteTimedEventMain);

//                                  EXECUTE THE T552 RELATED TASKS NOW -  EG SEND MAIL
//                                      String apiUrl = apiAppUrl+"/send552hrcomms?uniqueSiteId=" + unique_site_id;
                                        String apiUrl = apiAppUrl+"/send552hrcomms?uniqueSiteId="+unique_site_id;
                                        String response = restTemplate.getForObject(apiUrl, String.class);
                                        if (response.equals("SUCCESS")) {

                                            timedEventsTracker = new TimedEventsTracker();
                                            timedEventsTracker.setContextName("SEND-TPLUS552");
                                            timedEventsTracker.setContextValue(unique_site_id+ " >> SUCCESS");
                                            timedEventsTrackerRepository.save(timedEventsTracker);

                                            System.out.println("Successfully sent 7 days before due date email and SMS");
                                        } else {
                                            timedEventsTracker = new TimedEventsTracker();
                                            timedEventsTracker.setContextName("SEND-TPLUS552");
                                            timedEventsTracker.setContextValue(unique_site_id+ " >> FAIL");
                                            timedEventsTrackerRepository.save(timedEventsTracker);

                                            common.logErrors("api", "SiteTimedEventController", "executeTimerEventsLoop", "Send 7 Days Before Due Date email And SMS", "Failed to send 7 days before due date email and SMS (if else block)");
                                            System.out.println("Failed to send 7 days before due date email and SMS");
                                        }
                                }

                                break;

                                case 2:

                                if (elapsed_time_minutes >= (TPLUS720 -tolerance)  && elapsed_time_minutes <= (TPLUS720 + tolerance))
                                {
//                                  Do update code tor 720Hr
                                    siteTimedEventMain.setLastTimedEventNumericPosition(0);
                                    siteTimedEventMain.setLastTimedEventExecutedName("TPLUS720");
                                    siteTimedEventMain.setLastTimedEventExecutedDatetime(current_time);
                                    siteTimedEventMain.setIsPaid("NOT_PAID");

                                    int new_month_count = month_count+1;
                                    siteTimedEventMain.setMonthCounter(new_month_count);
                                    siteTimedEventRepository.save(siteTimedEventMain);

//                                    try {
////                                      Function to update payment status
//                                        executeNewMonthsWithPaymentLoop(unique_site_id);
//                                    } catch (Exception e){
//                                        common.logErrors("Failed to execute executeNewMonthsWithPaymentLoop function at TPLUS720 " + e.toString());
//                                        System.out.println("Failed to execute executeNewMonthsWithPaymentLoop function at TPLUS720 " + e.toString());
//                                    }
                                    boolean ctlps = checkTimerLoopPaymentStatus(unique_site_id);

                                    timedEventsTracker = new TimedEventsTracker();
                                    timedEventsTracker.setContextName("PAYMENT-STATUS-TPLUS720");
                                    timedEventsTracker.setContextValue(unique_site_id+ " >> " + Boolean.toString(ctlps));
                                    timedEventsTrackerRepository.save(timedEventsTracker);

                                    if(ctlps){
                                        timedEventsTracker = new TimedEventsTracker();
                                        timedEventsTracker.setContextName("PAYMENT-STATUS-TPLUS720");
                                        timedEventsTracker.setContextValue(unique_site_id+ " ALREADY PAID - DO NOTHING");
                                        timedEventsTrackerRepository.save(timedEventsTracker);

                                        // Payment is done, no need to do anything except check HIK site enable/disable status
                                        // If site is diasbled - enable and exit case
                                        System.out.println("24hr paid already... do nothing");
                                    } else {
//                                      EXECUTE THE T720 RELATED TASKS NOW -  EG SEND MAIL
//                                      String apiUrl = apiAppUrl+"/sendduedatecomms?uniqueSiteId=" + unique_site_id;
                                        String apiUrl = apiAppUrl+"/sendduedatecomms?uniqueSiteId="+unique_site_id;
                                        String response = restTemplate.getForObject(apiUrl, String.class);
                                        if (response.equals("SUCCESS")) {
                                            timedEventsTracker = new TimedEventsTracker();
                                            timedEventsTracker.setContextName("SEND-TPLUS720");
                                            timedEventsTracker.setContextValue(unique_site_id+ ">> SUCCESS");
                                            timedEventsTrackerRepository.save(timedEventsTracker);

                                            System.out.println("Successfully sent due date email and SMS");
                                        } else {
                                            timedEventsTracker = new TimedEventsTracker();
                                            timedEventsTracker.setContextName("SEND-TPLUS720");
                                            timedEventsTracker.setContextValue(unique_site_id+ " >> FAIL");
                                            timedEventsTrackerRepository.save(timedEventsTracker);

                                            common.logErrors("api", "SiteTimedEventController", "executeTimerEventsLoop", "Send Due Date Email And SMS", "Failed to send due date email and SMS (if else block)");
                                            System.out.println("Failed to send due date email and SMS");
                                        }
                                    }

                                }
                                break;
                                default:
                                break;
                        }
                    } else {
                        // Create a record in the site timed event
                        Date referenceTriggerDateTime = new Date();
                        SiteTimedEvent siteTimedEvent = new SiteTimedEvent();

                        siteTimedEvent.setReferenceTriggerEvent("HANDOVER");
                        siteTimedEvent.setReferenceTriggerDateTime(referenceTriggerDateTime);

                        siteTimedEvent.setLastTimedEventExecutedName("Tplus0 event");
                        siteTimedEvent.setLastTimedEventExecutedDatetime(referenceTriggerDateTime);

                        siteTimedEvent.setLastTimedEventNumericPosition(0);
                        siteTimedEvent.setMonthCounter(1);
                        siteTimedEvent.setUniqueSiteId(unique_site_id);
                        siteTimedEvent.setIsPaid("NOT_PAID");
                        siteTimedEvent.setNotes("NO NOTES");
                        siteTimedEventRepository.save(siteTimedEvent);

                        timedEventsTracker = new TimedEventsTracker();
                        timedEventsTracker.setContextName("HANDOVER-EVENT");
                        timedEventsTracker.setContextValue(unique_site_id+ " >> SUCCESS");
                        timedEventsTrackerRepository.save(timedEventsTracker);
                    }
                }
                return "Completed the loop..";
            }
            else
            {
                return "No installation sites found...";
            }
        }catch (Exception e) {
            common.logErrors("api", "SiteTimedEventController", "executeTimerEventsLoop", "Running Timer Loop", e.toString());
            return  "Error running timer loop: "+e.getMessage();
        }
    }

//    public void executeNewMonthsWithPaymentLoop(String uniqueSiteId)
//    {
//        common = new Common();
//
//        try{
//            SiteTimedEvent siteTimedEventMain = siteTimedEventRepository.getSiteTimedEventRecordbySiteID(uniqueSiteId);
//
//            siteTimedEventMain.setIsPaid("NOT_PAID");
//            siteTimedEventService.saveSiteNamedEvent(siteTimedEventMain);
//
//        } catch (Exception e) {
//            common.logErrors("Error running timer loop: " + e.getMessage());
//            System.out.println("Error running timer loop: "+e.getMessage());
//        }
//    }

    public boolean checkTimerLoopPaymentStatus(String uniqueSiteId)
    {
        try {
            SiteTimedEvent siteTimedEventMain = siteTimedEventRepository.getSiteTimedEventRecordbySiteID(uniqueSiteId);
            if(siteTimedEventMain.getIsPaid().equals("PAID")){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }
}
package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommonComponent {
    private final ConfigDataService configDataService;

    @Autowired
    public CommonComponent(ConfigDataService configDataService) {
        this.configDataService = configDataService;
    }

    public Map<String, Integer> getConfigs() {
        try {
            ConfigData configData = null;

            configData = configDataService.getConfigDataByConfigName("PERIOD_TO_SEND_INCOMPLETE_CUSTOMER_RECORDS");
            int sendIncompleteRecordsDelay = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("PERIOD_TO_DELETE_INCOMPLETE_CUSTOMER_RECORDS");
            int deleteIncompleteRecordsDelay = Integer.parseInt(configData.getConfigValue());

//          First month reminders
            configData = configDataService.getConfigDataByConfigName("BEFORE_FIRST_REMINDER");
            int beforeFirstReminder = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("SECOND_REMINDER_FIRST_MONTH");
            int secondReminderDelay = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("THIRD_REMINDER_FIRST_MONTH");
            int thirdReminderDelay = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("DISABLE_REMINDER_FIRST_MONTH");
            int disableReminderDelay = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("PRICES_UPDATE");
            int pricesUpdateDelay = Integer.parseInt(configData.getConfigValue());

//          Monthly reminders
            configData = configDataService.getConfigDataByConfigName("SEVEN_DAYS_TO_PAYMENT_DATE");
            int sevenDaysToPaymentDate = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("SECOND_REMINDER_MONTHLY");
            int secondReminderMonthly = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("THIRD_REMINDER_MONTHLY");
            int thirdReminderMonthly = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("FOURTH_REMINDER_MONTHLY");
            int fourthReminderMonthly = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("DISABLE_REMINDER_MONTHLY");
            int disableReminderMonthly = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("BEFORE_INSTALLATION");
            int beforeInstallation = Integer.parseInt(configData.getConfigValue());

            configData = configDataService.getConfigDataByConfigName("INSTALLATION_REMINDER");
            int installationReminder = Integer.parseInt(configData.getConfigValue());

            Map<String, Integer> map = new HashMap<>();
//          First month
            map.put("PERIOD_TO_SEND_INCOMPLETE_CUSTOMER_RECORDS", sendIncompleteRecordsDelay);
            map.put("PERIOD_TO_DELETE_INCOMPLETE_CUSTOMER_RECORDS", deleteIncompleteRecordsDelay);
            map.put("SECOND_REMINDER_FIRST_MONTH", secondReminderDelay);
            map.put("THIRD_REMINDER_FIRST_MONTH", thirdReminderDelay);
            map.put("DISABLE_REMINDER_FIRST_MONTH", disableReminderDelay);
            map.put("PRICES_UPDATE", pricesUpdateDelay);
            map.put("BEFORE_FIRST_REMINDER", beforeFirstReminder);

//          Monthly
            map.put("SEVEN_DAYS_TO_PAYMENT_DATE", sevenDaysToPaymentDate);
            map.put("SECOND_REMINDER_MONTHLY", secondReminderMonthly);
            map.put("THIRD_REMINDER_MONTHLY", thirdReminderMonthly);
            map.put("FOURTH_REMINDER_MONTHLY", fourthReminderMonthly);
            map.put("DISABLE_REMINDER_MONTHLY", disableReminderMonthly);

//          Installation Reminder
            map.put("BEFORE_INSTALLATION", beforeInstallation);
            map.put("INSTALLATION_REMINDER", installationReminder);

            return map;

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.toString());
            return null;
        }
    }
}

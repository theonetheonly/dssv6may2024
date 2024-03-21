package com.sgasecurity.api;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class NextDateService {
    public String startingDate() {
        LocalDate startingDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        String localDate = startingDate.format(formatter);
        return localDate;
    }

    public LocalDate startingDateYYYYMD() {
        return LocalDate.now();
    }

    public String todayDMYYYY() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    public String startingDateDMYYYY() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String localDate = currentDate.format(formatter);
        return localDate;
    }

    public LocalDate add30Days(LocalDate date) {
        LocalDate after30Days = date.plusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = after30Days.format(formatter);
        LocalDate localDate = LocalDate.parse(formattedDate, formatter);
        return localDate;
    }

    public LocalDate add30DaysYYYYMD() {
        LocalDate after30Days = LocalDate.now().plusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = after30Days.format(formatter);
        LocalDate localDate = LocalDate.parse(formattedDate, formatter);
        return localDate;
    }

    public LocalDate add31DaysYYYYMD() {
        LocalDate after31Days = LocalDate.now().plusDays(31);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = after31Days.format(formatter);
        LocalDate localDate = LocalDate.parse(formattedDate, formatter);
        return localDate;
    }

    public LocalDate add33DaysYYYYMD() {
        LocalDate after33Days = LocalDate.now().plusDays(33);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = after33Days.format(formatter);
        LocalDate localDate = LocalDate.parse(formattedDate, formatter);
        return localDate;
    }

//    public String dateAndTime(){
//        LocalDate now = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        LocalDateTime dateTime = LocalDateTime.parse(now.toString(), formatter);
//        return dateTime.toString();
//    }

    public String getDayValue(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");
        return date.format(formatter);
    }

    public String startingDateYYYYMDStr() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startingDate = LocalDate.now();
        return startingDate.format(formatter);
    }

    public String add30DaysStr(LocalDate date) {
        LocalDate after30Days = date.plusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return after30Days.format(formatter);
    }
}

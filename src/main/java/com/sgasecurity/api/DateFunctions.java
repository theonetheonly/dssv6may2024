package com.sgasecurity.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class DateFunctions {
    // Class to manage date functions

    Common common = new Common();

    public String getNextAnniversayReturnString(LocalDate mydate)
    {
        try {
            //               LocalDate mydate = LocalDate.now();
            System.out.println(mydate);
            String dataString = String.valueOf(mydate);

            String[] dateParts = dataString.split("-");
            String dayNo = dateParts[2];
            String monthNo = dateParts[1];
            String yearNo = dateParts[0];


            int dayNoInt = Integer.parseInt(dayNo);
            int monthNoInt = Integer.parseInt(monthNo);

            // Force days and year
        //    dayNoInt = 31;
//            monthNoInt =3;

            int nextMonthInt = monthNoInt + 1;
            int yearNoInt = Integer.parseInt(yearNo);

            if (nextMonthInt > 12)
            {
                yearNoInt = yearNoInt+1;
            }

            String nextMonthString = String.valueOf(nextMonthInt);

            if (nextMonthString.length() ==1)
            {
                nextMonthString ="0"+nextMonthString;
            }

            String yearNoString  = String.valueOf(yearNoInt);
            // Get numner of days in next month

            String dateForCalcNextMonthDays = yearNoString +"-"+nextMonthString+"-"+dayNo+" 11:00";

            //            String str = "2016-03-04 11:30";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(dateForCalcNextMonthDays, formatter);
            //           LocalDateTime dateTime2 = LocalDateTime.now();
            ChronoField chronoField = ChronoField.DAY_OF_MONTH;
            long daysInnextMonth = dateTime.range(chronoField).getMaximum();

            System.out.println("===========================");
            System.out.println("Current date: "+dataString);
            System.out.println("Current Month: "+monthNo);
            System.out.println("Next Month: "+nextMonthString);
            System.out.println("Next month  max daysdays: "+daysInnextMonth);
            System.out.println("===========================");

            int nextMonthDayNoInt = correctLastDayOfMonth(dayNoInt,(int)daysInnextMonth);

            String nextMonthDayNoIntStr = String.valueOf(nextMonthDayNoInt);

            if (nextMonthDayNoIntStr.length() ==1)
            {
                nextMonthDayNoIntStr ="0"+nextMonthDayNoIntStr;
            }

            String newFullDate = String.valueOf(yearNoInt)+ "-"+nextMonthString+"-"+nextMonthDayNoIntStr;
            System.out.println("New Full Date: "+newFullDate+"\n");

            return  newFullDate;
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
            return null;
        }
    }


    public LocalDate getNextAnniversayReturnDate(LocalDate mydate)
    {


        try {
            //               LocalDate mydate = LocalDate.now();
            System.out.println(mydate);
            String dataString = String.valueOf(mydate);

            System.out.println("Initial date: "+ mydate);

            String[] dateParts = dataString.split("-");

            String dayNo = dateParts[2];
            System.out.println("Day No: "+ dayNo);

            String monthNo = dateParts[1];
            System.out.println("Month No: "+ monthNo);

            String yearNo = dateParts[0];
            System.out.println("Year No: "+ yearNo);


            int dayNoInt = Integer.parseInt(dayNo);



            int monthNoInt = Integer.parseInt(monthNo);

            // Force days and year
            //     dayNoInt = 31;
            //   monthNoInt =3;


            //

            int nextMonthInt = monthNoInt + 1;

            int yearNoInt = Integer.parseInt(yearNo);

            System.out.println("Next Month Int: "+ nextMonthInt);


            if (nextMonthInt > 12)
            {
                yearNoInt = yearNoInt+1;
            }
            System.out.println("Final year: "+ yearNoInt);



            String nextMonthString = String.valueOf(nextMonthInt);




            if (nextMonthString.length() ==1)
            {
                nextMonthString ="0"+nextMonthString;
            }

            System.out.println("Final next month: "+nextMonthString);

            String yearNoString  = String.valueOf(yearNoInt);
            // Get numner of days in next month

            String dateForCalcNextMonthDays = yearNoString +"-"+nextMonthString+"-"+dayNo+" 11:00";

            System.out.println("Date for calc next month: "+dateForCalcNextMonthDays);

            //            String str = "2016-03-04 11:30";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            LocalDateTime dateTime = LocalDateTime.parse(dateForCalcNextMonthDays, formatter);
            //           LocalDateTime dateTime2 = LocalDateTime.now();

            ChronoField chronoField = ChronoField.DAY_OF_MONTH;

            long daysInnextMonth = dateTime.range(chronoField).getMaximum();

            System.out.println("===========================");
            System.out.println("Current date: "+dataString);
            System.out.println("Current Month: "+monthNo);
            System.out.println("Next Month: "+nextMonthString);
            System.out.println("Next month  max daysdays: "+daysInnextMonth);
            System.out.println("===========================");

            int nextMonthDayNoInt = correctLastDayOfMonth(dayNoInt,(int)daysInnextMonth);

            String nextMonthDayNoIntStr = String.valueOf(nextMonthDayNoInt);

            if (nextMonthDayNoIntStr.length() ==1)
            {
                nextMonthDayNoIntStr ="0"+nextMonthDayNoIntStr;
            }

            String newFullDate = String.valueOf(yearNoInt)+ "-"+nextMonthString+"-"+nextMonthDayNoIntStr;
            System.out.println("New Full Date: "+newFullDate+"\n");


            LocalDate newFullLocalDate = LocalDate.parse(dateForCalcNextMonthDays, formatter);

            // Subtract 1 day

            newFullLocalDate = newFullLocalDate.minusDays(1);

            System.out.println("New full localdate date: "+ newFullLocalDate);

            return  newFullLocalDate;
        }
        catch (Exception ex)
        {
      //      System.out.println(ex.toString());
            System.out.println("Error calculating next: "+ex.toString());
            return null;
        }
    }


    public static int correctLastDayOfMonth(int subjectDay, int maxDaysInMonth)
    {
        int result = subjectDay;
        try{
            switch (maxDaysInMonth)
            {
                case 30:
                    if (subjectDay > 30)
                    {
                        result = 30;
                    }
                    break;
                case 31:
                    result = subjectDay;
                    break;
                case 28:
                    if (subjectDay > 28)
                    {
                        result = 28;
                    }
                    break;
                case 29:
                    if (subjectDay >29)
                    {
                        result = 29;
                    }
                    break;
                default:
                    result = subjectDay;
                    break;
            }

            return result;
        }
        catch (Exception ex)
        {
            System.out.println("Error correct last date of month: "+ex.toString());
            return  -1;
        }
    }


    public String doFormatDate(int pattern, LocalDate myDate)
    {
        try{

            String formattedDate = myDate.toString();
            if (pattern ==1) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
                formattedDate = myDate.format(formatter);
            }
            else if (pattern==2)
            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
                formattedDate = myDate.format(formatter);

            }
            else if(pattern ==3)
            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy", Locale.ENGLISH);
                formattedDate = myDate.format(formatter);
            }
            else {
                formattedDate =myDate.toString();
            }
            return formattedDate;
        }
        catch (Exception ex)
        {
            System.out.println("Error reformatting date: "+ex.toString());
            return null;
        }
    }



}

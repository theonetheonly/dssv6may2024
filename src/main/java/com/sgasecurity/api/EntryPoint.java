package com.sgasecurity.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class EntryPoint {

    @Autowired
    public EntryPoint() {
        System.out.println("...Entry poing no-args constructor loaded...");
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping("/")

    public String landingPage(Model model, HttpServletRequest request) {

        try {
            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.now();
            return "HPP API loaded at: " + time.toString() + " on " + date.toString() + "\nYour IP address is: " + request.getRemoteAddr();

        } catch (Exception ex) {
            String err_msg = "Error (landingPage) in (Entrypoint.java) " + ex.getMessage().toString();
            System.out.println(err_msg);
            return  err_msg;
        }
    }

    public boolean EmailCheck(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        return mat.matches();
    }
}

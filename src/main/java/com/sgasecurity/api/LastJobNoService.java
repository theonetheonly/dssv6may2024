package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LastJobNoService {
    @Autowired
    LastJobNoRepository lastJobNoRepository;
    String letters = null;
    String digitsStr = null;
    int digits;
    int digitsCount;
    String digitsResult = null;
    String result = null;

    public String getLastJobNo() {
        if(!Objects.isNull(getTheLastJobNo())){
            LastJobNo lastJobNo = lastJobNoRepository.findTopByOrderByIdDesc();
            String jobNo = lastJobNo.getJobNo();

            letters = jobNo.substring(0, 3);
            digitsStr = jobNo.substring(3);
            digits = Integer.parseInt(digitsStr);
            digits = digits + 1;
            digitsCount = String.valueOf(digits).length();

            switch (digitsCount) {
                case 1:
                    digitsResult = "0000" + digits;
                    break;
                case 2:
                    digitsResult = "000" + digits;
                    break;
                case 3:
                    digitsResult = "00" + digits;
                    break;
                case 4:
                    digitsResult = "0" + digits;
                    break;
                default:
                    digitsResult = String.valueOf(digits);
                    break;
            }
            result = letters + digitsResult;

            return result;
        } else {
            return "DSS00001";
        }
    }

    public void saveJobNo(LastJobNo jobNo){
        lastJobNoRepository.save(jobNo);
    }

    public LastJobNo getTheLastJobNo(){
        return lastJobNoRepository.findTopByOrderByIdDesc();
    }
}

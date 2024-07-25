package com.megaloans.lms.service;

import com.megaloans.lms.model.EMISchedule;
import com.megaloans.lms.model.LoanDetails;
import com.megaloans.lms.repository.EMIScheduleRepository;
import com.megaloans.lms.repository.LoanDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EMIService {
    // this can be in different microservice which just manages our workflows

    @Autowired
    private EMIScheduleRepository emiScheduleRepository;
    @Autowired
    private LoanDetailsRepository loanDetailsRepository;

    public void sendMonthlyEMIsToKafka() {
        int pageSize = 1000;
        int page = 0;
        boolean hasNextPage = true;

        while (hasNextPage) {
            Pageable pageable = PageRequest.of(page, pageSize);
            List<EMISchedule> monthlyEMIs = emiScheduleRepository.findMonthlyEMIsDueToday(pageable);

            for (EMISchedule emi : monthlyEMIs) {
                // all emi's will have a object of loan as well
               // String emiJson = objectMapper.writeValueAsString(emi); // convert to JSON
                //kafkaTemplate.send("emi-topic", emiJson); // send to Kafka topic
            }

            hasNextPage = monthlyEMIs.size() == pageSize;
            page++;
        }
    }

    public void recieveEmiStatusMsg(EMISchedule emiSchedule){
        boolean update = updateLoanDetails(emiSchedule);
        if(!update){
            //mark for failed and add to retry queue;
        }
    }

    public boolean updateLoanDetails(EMISchedule emiSchedule) {
        LoanDetails loanDetails = emiSchedule.getLoan();
        LoanDetails savedLoanDetails = loanDetailsRepository.findByLoanId(loanDetails.getLoanId());
        if (loanDetails.getVersion() == savedLoanDetails.getVersion()) {
            //loan updated with new data
            loanDetailsRepository.save(loanDetails);
            return true;
        } else {
            //add this emi to process again as loan had changed
            return false;
        }
    }
}
package com.megaloans.lms.repository;
import com.megaloans.lms.model.EMISchedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EMIScheduleRepository extends JpaRepository<EMISchedule, Integer> {

    // Find EMIs due today (complex query)
    @Query("SELECT es FROM EMISchedule es WHERE " +
            "(es.paymentFrequency = 'WEEKLY' AND es.paymentDay = DAY_OF_WEEK(CURRENT_DATE)) OR " +
            "(es.paymentFrequency = 'MONTHLY' AND es.paymentDay = DAY(CURRENT_DATE)) OR " +
            "(es.paymentFrequency = 'ANNUAL' AND es.paymentDay = DAY(CURRENT_DATE) AND MONTH(CURRENT_DATE) = MONTH(es.startDate))")
    List<EMISchedule> findEMIsDueToday(Pageable pageable);

    // Find EMIs due today (separate queries for each frequency)
    @Query("SELECT es FROM EMISchedule es WHERE es.paymentFrequency = 'WEEKLY' AND es.paymentDay = DAY_OF_WEEK(CURRENT_DATE)")
    List<EMISchedule> findWeeklyEMIsDueToday(Pageable pageable);

    @Query("SELECT es FROM EMISchedule es WHERE es.paymentFrequency = 'MONTHLY' AND es.paymentDay = DAY(CURRENT_DATE)")
    List<EMISchedule> findMonthlyEMIsDueToday(Pageable pageable);

    @Query("SELECT es FROM EMISchedule es WHERE es.paymentFrequency = 'ANNUAL' AND es.paymentDay = DAY(CURRENT_DATE) AND MONTH(CURRENT_DATE) = MONTH(es.startDate)")
    List<EMISchedule> findAnnualEMIsDueToday(Pageable pageable);

}
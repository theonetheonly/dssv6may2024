package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TechnicianAssignmentRepository extends JpaRepository<TechnicianAssignment, Integer> {
    TechnicianAssignment findByUniqueSiteId(String uniqueSiteId);
//    TechnicianAssignment findByTechnicianId(int technicianId);
    List<TechnicianAssignment> findByReminderDate(LocalDate reminderDate);

    @Query("SELECT t FROM TechnicianAssignment t WHERE t.reminderDate = :date")
    List<TechnicianAssignment> findTechnicianAssignmentsOneDayBeforeInstallation(@Param("date") LocalDate date);
}

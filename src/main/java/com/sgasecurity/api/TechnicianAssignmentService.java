package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TechnicianAssignmentService {
    @Autowired
    TechnicianAssignmentRepository technicianAssignmentRepository;

    public TechnicianAssignment getTechnicianAssignmentByUniqueSiteId(String uniqueSiteId) {
        return (TechnicianAssignment)this.technicianAssignmentRepository.findByUniqueSiteId(uniqueSiteId);
    }

//    public TechnicianAssignment getTechnicianAssignmentByTechnicianId(int technicianId) {
//        return (TechnicianAssignment)this.technicianAssignmentRepository.findByTechnicianId(technicianId);
//    }

    public TechnicianAssignment saveTechnicianAssignment(TechnicianAssignment technicianAssignment)
    {
        return technicianAssignmentRepository.save(technicianAssignment);
    }

    public List<TechnicianAssignment> getTechnicianAssignmentByReminderDate(LocalDate date)
    {
        return technicianAssignmentRepository.findByReminderDate(date);
    }

    public List<TechnicianAssignment> getTechnicianAssignmentsOneDayBeforeInstallation(LocalDate date)
    {
        return technicianAssignmentRepository.findTechnicianAssignmentsOneDayBeforeInstallation(date);
    }
}

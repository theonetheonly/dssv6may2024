package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicianService {
    @Autowired
    TechnicianRepository technicianRepository;

    public Technician saveTechnician(Technician technician)
    {
        return technicianRepository.save(technician);
    }

    public Technician getTechnicianById(int id) {
        return this.technicianRepository.findById(id).get();
    }

    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

}

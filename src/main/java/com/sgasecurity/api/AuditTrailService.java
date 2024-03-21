package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditTrailService {
    @Autowired
    AuditTrailRepository auditTrailRepository;
    public AuditTrail saveAuditTrail(AuditTrail auditTrail)
    {
        return auditTrailRepository.save(auditTrail);
    }
}

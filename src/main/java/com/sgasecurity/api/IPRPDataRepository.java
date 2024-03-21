package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface IPRPDataRepository extends JpaRepository<IPRPData, Long> {
    IPRPData findBySiteId(String siteId);
}

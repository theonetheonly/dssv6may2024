package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HikEventRepository extends JpaRepository<HikEvent, Long> {
    HikEvent findBySystemCustomerNo(String systemCustomerNo);
    HikEvent findByHppSiteId(String hppSiteId);
    HikEvent findByUniqueSiteId(String uniqueSiteId);

}

package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewMonthRepository extends JpaRepository<NewMonth, Long> {
    NewMonth findByUniqueSiteId(String uniqueSiteId);
}

package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SiteTimedEventRepository extends JpaRepository<SiteTimedEvent, Long> {
    SiteTimedEvent findByUniqueSiteId(String uniqueSiteId);
    List<SiteTimedEvent> findAllByUniqueSiteId(String uniqueSiteId);
    List<SiteTimedEvent> findByIsPaid(String isPaid);
    @Query(value = "SELECT * FROM site_timed_events WHERE unique_site_id=?1 ORDER BY id ASC LIMIT 1", nativeQuery = true)
    public SiteTimedEvent getSiteTimedEventRecordbySiteID(String unique_site_id);

    @Query("SELECT e FROM SiteTimedEvent e WHERE e.isPaid = 'PAID'")
    List<SiteTimedEvent> findSiteTimedEventsWithPaidMonths();
}

package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SiteTimedEventService {
    @Autowired
    SiteTimedEventRepository siteNamedEventRepository;
    public SiteTimedEvent getSiteNamedEventByUniqueSiteId(String uniqueSiteId) {
        return this.siteNamedEventRepository.findByUniqueSiteId(uniqueSiteId);
    }

    public SiteTimedEvent saveSiteNamedEvent(SiteTimedEvent siteTimedEvent)
    {
        return siteNamedEventRepository.save(siteTimedEvent);
    }

    public List<SiteTimedEvent> getSiteTimedEventsByUniqueSiteId(String uniqueSiteId) {
        return siteNamedEventRepository.findAllByUniqueSiteId(uniqueSiteId);
    }

    public List<SiteTimedEvent> getSiteTimedEventsWithPaidMonths() {
        return siteNamedEventRepository.findSiteTimedEventsWithPaidMonths();
    }

    public List<SiteTimedEvent> getSiteTimedEventByIsPaid(String isPaid)
    {
        return siteNamedEventRepository.findByIsPaid(isPaid);
    }
}

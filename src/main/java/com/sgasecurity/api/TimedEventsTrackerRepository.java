package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TimedEventsTrackerRepository extends JpaRepository<TimedEventsTracker, Integer>, JpaSpecificationExecutor<TimedEventsTracker> {

}
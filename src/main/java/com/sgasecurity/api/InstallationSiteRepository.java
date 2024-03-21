package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InstallationSiteRepository extends JpaRepository<InstallationSite, Long> {
    InstallationSite findBySystemCustomerNo(String systemCustomerNo);
    InstallationSite findByHppSiteId(String hppSiteId);
    InstallationSite findByUniqueSiteId(String uniqueSiteId);
    InstallationSite findByHikFormattedCustomerSiteName(String hikFormattedCustomerSiteName);
    long countByPackageTypeName(String packageTypeName);

    @Query("SELECT r FROM InstallationSite r WHERE r.installationDate = :date AND r.handoverStatus = 'DONE'")
    List<InstallationSite> findInstallationsWithHandoverStatusDone(@Param("date") LocalDate date);

    @Query("SELECT r FROM InstallationSite r WHERE r.installationDate = :date AND r.handoverStatus = 'NOT_DONE'")
    List<InstallationSite> findAllInstallationsWithHandoverStatusNotDone(@Param("date") LocalDate date);

    @Query("SELECT r FROM InstallationSite r WHERE r.installationDate = :date AND r.handoverStatus = 'DONE'")
    List<InstallationSite> findAllInstallationsWithHandoverStatusDone(@Param("date") LocalDate date);

    @Query("SELECT r FROM InstallationSite r WHERE r.nextPaymentDate = :date AND r.handoverStatus = 'DONE'")
    List<InstallationSite> findBySystemCustomerNoWithHandoverStatusDone(@Param("date") LocalDate date);

    @Query("SELECT r FROM InstallationSite r WHERE r.installationDate = :date AND r.handoverStatus = 'NOT_DONE'")
    List<InstallationSite> findInstallationsOneDayBeforeInstallationDate(@Param("date") LocalDate date);

    @Query("SELECT r FROM InstallationSite r WHERE r.installationDate = :date AND r.handoverStatus = 'DONE' AND r.installationStatus = 'ACTIVE'")
    List<InstallationSite> findTodaysInstallationSitesHandedOver(@Param("date") LocalDate date);

    @Query(value = "SELECT * FROM installation_sites WHERE handover_status=?1 ORDER BY id ASC", nativeQuery = true)
//    @Query(value = "SELECT * FROM installation_sites ", nativeQuery = true)
    public List<InstallationSite> getInstallationSitesReady(String handover_status);

    @Query("SELECT r FROM InstallationSite r WHERE r.handoverDate = :date AND r.handoverStatus = 'DONE'")
    List<InstallationSite> findAllTodayInstallationsWithHandoverStatusDone(@Param("date") LocalDate date);
}

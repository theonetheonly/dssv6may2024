package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class InstallationSiteService {
    @Autowired
    InstallationSiteRepository installationSiteRepository;

    public InstallationSite getInstallationSiteBySystemCustomerNo(String systemCustomerNo) {
        return (InstallationSite)this.installationSiteRepository.findBySystemCustomerNo(systemCustomerNo);
    }

    public InstallationSite getInstallationSiteByHppSiteId(String hppSiteId) {
        return (InstallationSite)this.installationSiteRepository.findByHppSiteId(hppSiteId);
    }

    public InstallationSite getInstallationSiteByUniqueSiteId(String uniqueSiteId) {
        return (InstallationSite)this.installationSiteRepository.findByUniqueSiteId(uniqueSiteId);
    }

    public InstallationSite getInstallationSiteByHikFormattedCustomerSiteName(String hikFormattedCustomerSiteName) {
        return (InstallationSite)this.installationSiteRepository.findByHikFormattedCustomerSiteName(hikFormattedCustomerSiteName);
    }

    public InstallationSite saveInstallationSite(InstallationSite installationSite)
    {
        return installationSiteRepository.save(installationSite);
    }

    public List<InstallationSite> getInstallationsWithHandoverStatusDone(LocalDate date)
    {
        return installationSiteRepository.findInstallationsWithHandoverStatusDone(date);
    }

    public List<InstallationSite> getAllInstallationsWithHandoverStatusDone(LocalDate date)
    {
        return installationSiteRepository.findAllInstallationsWithHandoverStatusDone(date);
    }

    public List<InstallationSite> getAllInstallationsWithHandoverStatusNotDone(LocalDate date)
    {
        return installationSiteRepository.findAllInstallationsWithHandoverStatusNotDone(date);
    }

    public List<InstallationSite> getBySystemCustomerNoWithHandoverStatusDone(LocalDate date)
    {
        return installationSiteRepository.findBySystemCustomerNoWithHandoverStatusDone(date);
    }

    public List<InstallationSite> getInstallationsOneDayBeforeInstallationDate(LocalDate date)
    {
        return installationSiteRepository.findInstallationsOneDayBeforeInstallationDate(date);
    }

    public List<InstallationSite> getAllInstallationSites() {
        return installationSiteRepository.findAll();
    }

    public List<InstallationSite> getTodaysInstallationSitesHandedOver(LocalDate date)
    {
        return installationSiteRepository.findTodaysInstallationSitesHandedOver(date);
    }

    public List<InstallationSite> getAllTodayInstallationsWithHandoverStatusDone(LocalDate date)
    {
        return installationSiteRepository.findAllTodayInstallationsWithHandoverStatusDone(date);
    }
}

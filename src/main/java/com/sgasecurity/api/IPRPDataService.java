package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class
IPRPDataService {
    @Autowired
    IPRPDataRepository iprpDataRepository;

    public IPRPData getIPRPDataBySiteId(String siteId) {
        return this.iprpDataRepository.findBySiteId(siteId);
    }

    public IPRPData saveIPRPData(IPRPData iprpData)
    {
        return iprpDataRepository.save(iprpData);
    }
}

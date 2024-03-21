package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigDataRepository extends JpaRepository<ConfigData, Integer> {
    ConfigData findByConfigName(String configName);
}

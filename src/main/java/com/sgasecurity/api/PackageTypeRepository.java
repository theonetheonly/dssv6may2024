package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageTypeRepository extends JpaRepository<PackageType, Integer> {
    PackageType findByPackageTypeName(String packageTypeName);
}

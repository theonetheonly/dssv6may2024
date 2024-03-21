package com.sgasecurity.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LastJobNoRepository extends JpaRepository<LastJobNo, Integer> {
    LastJobNo findTopByOrderByIdDesc();
}

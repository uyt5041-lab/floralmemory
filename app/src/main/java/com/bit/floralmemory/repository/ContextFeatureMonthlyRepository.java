package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.ContextFeatureMonthly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContextFeatureMonthlyRepository
        extends JpaRepository<ContextFeatureMonthly, ContextFeatureMonthly.ContextFeatureId> {
}

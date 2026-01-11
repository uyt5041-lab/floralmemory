package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.ForecastEnsemble;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForecastEnsembleRepository extends JpaRepository<ForecastEnsemble, Long> {
    List<ForecastEnsemble> findByRun_RunIdOrderByMonthAsc(Long runId);
}

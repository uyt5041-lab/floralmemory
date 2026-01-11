package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.ForecastResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForecastResultRepository extends JpaRepository<ForecastResult, Long> {

    List<ForecastResult> findByRun_RunIdAndModelFamilyOrderByMonthAsc(Long runId, String modelFamily);

    List<ForecastResult> findByRun_RunIdAndModelNameOrderByMonthAsc(Long runId, String modelName);
}

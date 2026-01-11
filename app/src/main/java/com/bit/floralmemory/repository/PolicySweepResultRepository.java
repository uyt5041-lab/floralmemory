package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.PolicySweepResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PolicySweepResultRepository extends JpaRepository<PolicySweepResult, Long> {
    List<PolicySweepResult> findBySweepRun_SweepIdOrderByExpectedTotalLossAsc(Long sweepId);

    List<PolicySweepResult> findBySweepRun_SweepIdAndIsBestTrue(Long sweepId);
}

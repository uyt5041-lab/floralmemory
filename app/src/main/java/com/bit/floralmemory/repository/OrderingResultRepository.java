package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.OrderingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderingResultRepository extends JpaRepository<OrderingResult, Long> {
    List<OrderingResult> findByOrderingRun_OrderRunId(Long orderRunId);
}

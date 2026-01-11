package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.IngestionJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionJobRepository extends JpaRepository<IngestionJob, Long> {
}

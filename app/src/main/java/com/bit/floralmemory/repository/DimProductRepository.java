package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.DimProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DimProductRepository extends JpaRepository<DimProduct, Long> {
    Optional<DimProduct> findByProductSlug(String productSlug);
}

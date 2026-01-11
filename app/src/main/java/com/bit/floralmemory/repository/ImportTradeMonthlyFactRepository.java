package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.ImportTradeMonthlyFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ImportTradeMonthlyFactRepository extends JpaRepository<ImportTradeMonthlyFact, Long> {

    @Query("""
        select f from ImportTradeMonthlyFact f
        where f.month between :from and :to
          and f.importer.countryCode = :importer
          and f.exporter.countryCode = :exporter
          and f.product.productSlug = :productSlug
        order by f.month asc
    """)
    List<ImportTradeMonthlyFact> findSeries(LocalDate from, LocalDate to, String importer, String exporter, String productSlug);
}

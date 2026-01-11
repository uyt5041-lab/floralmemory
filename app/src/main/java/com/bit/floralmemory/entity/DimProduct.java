package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dim_product")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DimProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_slug", nullable = false, unique = true)
    private String productSlug;
}

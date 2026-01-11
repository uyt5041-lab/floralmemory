package com.bit.floralmemory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_product")
public class DimProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_slug", nullable = false, unique = true)
    private String productSlug;

    public DimProduct() {
    }

    public DimProduct(Long productId, String productName, String productSlug) {
        this.productId = productId;
        this.productName = productName;
        this.productSlug = productSlug;
    }

    public static DimProductBuilder builder() {
        return new DimProductBuilder();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }

    public static class DimProductBuilder {
        private Long productId;
        private String productName;
        private String productSlug;

        DimProductBuilder() {
        }

        public DimProductBuilder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public DimProductBuilder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public DimProductBuilder productSlug(String productSlug) {
            this.productSlug = productSlug;
            return this;
        }

        public DimProduct build() {
            return new DimProduct(productId, productName, productSlug);
        }
    }
}

package com.bit.floralmemory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_hs_code")
public class DimHsCode {
    @Id
    @Column(name = "hs_code", length = 10)
    private String hsCode;

    @Column(name = "hs_desc")
    private String hsDesc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_hs_code")
    private DimHsCode parent;

    public DimHsCode() {
    }

    public DimHsCode(String hsCode, String hsDesc, DimHsCode parent) {
        this.hsCode = hsCode;
        this.hsDesc = hsDesc;
        this.parent = parent;
    }

    public static DimHsCodeBuilder builder() {
        return new DimHsCodeBuilder();
    }

    public String getHsCode() {
        return hsCode;
    }

    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public String getHsDesc() {
        return hsDesc;
    }

    public void setHsDesc(String hsDesc) {
        this.hsDesc = hsDesc;
    }

    public DimHsCode getParent() {
        return parent;
    }

    public void setParent(DimHsCode parent) {
        this.parent = parent;
    }

    public static class DimHsCodeBuilder {
        private String hsCode;
        private String hsDesc;
        private DimHsCode parent;

        DimHsCodeBuilder() {
        }

        public DimHsCodeBuilder hsCode(String hsCode) {
            this.hsCode = hsCode;
            return this;
        }

        public DimHsCodeBuilder hsDesc(String hsDesc) {
            this.hsDesc = hsDesc;
            return this;
        }

        public DimHsCodeBuilder parent(DimHsCode parent) {
            this.parent = parent;
            return this;
        }

        public DimHsCode build() {
            return new DimHsCode(hsCode, hsDesc, parent);
        }
    }
}

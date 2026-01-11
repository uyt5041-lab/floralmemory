package com.bit.floralmemory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_country")
public class DimCountry {
    @Id
    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    public DimCountry() {
    }

    public DimCountry(String countryCode, String countryName) {
        this.countryCode = countryCode;
        this.countryName = countryName;
    }

    public static DimCountryBuilder builder() {
        return new DimCountryBuilder();
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public static class DimCountryBuilder {
        private String countryCode;
        private String countryName;

        DimCountryBuilder() {
        }

        public DimCountryBuilder countryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public DimCountryBuilder countryName(String countryName) {
            this.countryName = countryName;
            return this;
        }

        public DimCountry build() {
            return new DimCountry(countryCode, countryName);
        }
    }
}

package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dim_country")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DimCountry {
    @Id
    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Column(name = "country_name", nullable = false)
    private String countryName;
}

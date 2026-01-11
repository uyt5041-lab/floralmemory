package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dim_hs_code")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DimHsCode {
    @Id
    @Column(name = "hs_code", length = 10)
    private String hsCode;

    @Column(name = "hs_desc")
    private String hsDesc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_hs_code")
    private DimHsCode parent;
}

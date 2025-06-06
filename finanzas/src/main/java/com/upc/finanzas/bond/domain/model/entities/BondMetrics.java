package com.upc.finanzas.bond.domain.model.entities;

import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BondMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "bond_id", nullable = false)
    private Bond bond;
    /**
     * Ratios de decisión del bono
     */
    @NotNull
    @Digits(integer = 5, fraction = 2)
    private BigDecimal duration;
    @NotNull
    @Digits(integer = 6, fraction = 2)
    private BigDecimal convexity;
    @NotNull
    @Digits(integer = 6, fraction = 2)
    private BigDecimal totalDurationConvexity;
    @NotNull
    @Digits(integer = 5, fraction = 2)
    private BigDecimal modifiedDuration;
    /**
     * Indicadores de rentabilidad del bono
     */
    @NotNull
    @Digits(integer = 3, fraction = 4)
    private BigDecimal tcea;
    @NotNull
    @Digits(integer = 3, fraction = 4)
    private BigDecimal trea;
}

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
     * Precio máximo del bono
     */
    @NotNull
    @Digits(integer = 5, fraction = 2)
    private BigDecimal maxPrice;
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
    @Digits(integer = 5, fraction = 2)
    private BigDecimal modifiedDuration;
    /**
     * Indicadores de rentabilidad del bono
     */
    @NotNull
    @Digits(integer = 3, fraction = 4)
    private BigDecimal tcea; // Tasa de Costo Efectivo Anual (Emisor)
    @NotNull
    @Digits(integer = 3, fraction = 4)
    private BigDecimal trea; // Tasa de Rentabilidad Efectiva Anual (Bonista)

    public void update(BondMetrics updatedMetrics) {
        this.duration = updatedMetrics.getDuration();
        this.convexity = updatedMetrics.getConvexity();
        this.modifiedDuration = updatedMetrics.getModifiedDuration();
        this.tcea = updatedMetrics.getTcea();
        this.trea = updatedMetrics.getTrea();
    }
}

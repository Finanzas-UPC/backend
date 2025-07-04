package com.upc.finanzas.bond.domain.model.entities;

import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "bond_id", nullable = false)
    private Bond bond;
    /**
     * Información de un flujo de caja específico
     */
    @PositiveOrZero
    private int period; // Número del periodo de flujo de caja (0, 1, 2, 3, ...)
    @NotNull
    private LocalDate paymentDate; // Fecha en la que se realiza el pago
    private boolean isGracePeriod; // Indica si este periodo es de gracia
    @NotNull
    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero
    private BigDecimal initialBalance; // Saldo del bono antes del pago de este periodo
    @NotNull
    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero
    private BigDecimal interest; // Interés generado en este periodo (cupón)
    @NotNull
    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero
    private BigDecimal amortization; // Amortización del capital en este periodo (como es metodo aleman es el mismo en todos los periodos)
    @NotNull
    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero
    private BigDecimal finalBalance; // Saldo restante después del pago
    @NotNull
    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero
    private BigDecimal totalPayment; // Cuota total pagada en este periodo (interés + amortización)
    /**
     * Flujo de caja del emisor y del bonista
     */
    @NotNull
    private BigDecimal issuerCashFlow; // Flujo del emisor (egreso para el emisor)
    @NotNull
    private BigDecimal bondHolderCashFlow; // Flujo del bonista (ingreso para el inversionista)
    /**
     * Valores intermedios para cálculos financieros (del bonista)
     */
    @NotNull
    @Digits(integer = 8, fraction = 2)
    private BigDecimal presentCashFlow; // Flujo actualizado descontado con la COK
    @NotNull
    @Digits(integer = 8, fraction = 2)
    private BigDecimal presentCashFlowTimesPeriod; // Producto del flujo actualizado por el periodo
    @NotNull
    @Digits(integer = 8, fraction = 2)
    private BigDecimal convexityFactor; // Producto de presentCashFlowTimesPeriod por el periodo + 1
}

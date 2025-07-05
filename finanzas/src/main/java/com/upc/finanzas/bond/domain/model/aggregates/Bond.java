package com.upc.finanzas.bond.domain.model.aggregates;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.upc.finanzas.bond.domain.model.commands.CreateBondCommand;
import com.upc.finanzas.bond.domain.model.commands.UpdateBondCommand;
import com.upc.finanzas.bond.domain.model.valueobjects.CurrencyType;
import com.upc.finanzas.bond.domain.model.valueobjects.GracePeriodType;
import com.upc.finanzas.bond.domain.model.valueobjects.InterestType;
import com.upc.finanzas.iam.domain.model.aggregates.User;
import com.upc.finanzas.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
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
public class Bond extends AuditableAbstractAggregateRoot<Bond> {
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @NotNull(message = "Asignarle un nombre al bono es obligatorio")
    private String name;
    @Positive
    private BigDecimal nominalValue; // Valor nominal del bono
    @Positive
    private BigDecimal marketValue; // Valor de mercado del bono
    @Positive
    private int duration; // Plazo total en años
    @Max(365)
    @Min(1)
    private int frequency; // Frecuencia del cupon (1: anual, 2: semestral, 3: trimestral, 4: cuatrimestral, 6: bimestral, 12: mensual, 365: diario)
    @NotNull(message = "Indicar el tipo de tasa de interés es obligatorio (efectiva o nominal)")
    @Enumerated(value = EnumType.STRING)
    private InterestType interestType; // Tipo de interés (efectiva o nominal)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 10, scale = 7)
    private BigDecimal interestRate; // Tasa de interés del bono (%) ANUAL
    @Max(365)
    @Min(1)
    private int capitalization; // Capitalización del interés (1: diaria, 7: semanal, 15: quincenal, 30: mensual, 60: bimestral, 90: trimestral, 180: semestral, 360: anual)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 10, scale = 7)
    private BigDecimal discountRate; // Tasa COK (%) ANUAL
    @NotNull(message = "La fecha de emisión es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate emissionDate;
    @NotNull(message = "El tipo de periodo de gracia es obligatorio (total, parcial o ninguno)")
    @Enumerated(EnumType.STRING)
    private GracePeriodType gracePeriodType; // Tipo de periodo de gracia (total, parcial o sin periodo de gracia)
    @Min(0)
    private int gracePeriodDuration;
    @NotNull
    @Enumerated(value = EnumType.STRING)
    private CurrencyType currency;
    /**
     * Gastos adicionales del bono
     **/
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 10, scale = 7)
    private BigDecimal primeRate; // Tasa prima (%)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 10, scale = 7)
    private BigDecimal structuringRate; // Tasa de estructuración (%)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 10, scale = 7)
    private BigDecimal placementRate; // Tasa de colocación (%)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 10, scale = 7)
    private BigDecimal floatRate; // Tasa de flotación (%)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 10, scale = 7)
    private BigDecimal cavaliRate; // Tasa de CAVALI (%)

    public Bond (User user, CreateBondCommand command) {
        this.user = user;
        this.name = command.name();
        this.nominalValue = command.nominalValue();
        this.marketValue = command.marketValue();
        this.duration = command.duration();
        this.frequency = command.frequency();
        this.interestType = InterestType.valueOf(command.interestType().toUpperCase());
        this.interestRate = command.interestRate();
        this.capitalization = command.capitalization();
        this.discountRate = command.discountRate();
        this.emissionDate = command.emissionDate();
        this.gracePeriodType = GracePeriodType.valueOf(command.gracePeriodType().toUpperCase());
        this.gracePeriodDuration = command.gracePeriodDuration();
        this.currency = CurrencyType.valueOf(command.currency().toUpperCase());
        this.primeRate = command.primeRate();
        this.structuringRate = command.structuringRate();
        this.placementRate = command.placementRate();
        this.floatRate = command.floatRate();
        this.cavaliRate = command.cavaliRate();
    }

    public void update(UpdateBondCommand command) {
        this.name = command.name();
        this.nominalValue = command.nominalValue();
        this.marketValue = command.marketValue();
        this.duration = command.duration();
        this.frequency = command.frequency();
        this.interestType = InterestType.valueOf(command.interestType().toUpperCase());
        this.interestRate = command.interestRate();
        this.capitalization = command.capitalization();
        this.discountRate = command.discountRate();
        this.emissionDate = command.emissionDate();
        this.gracePeriodType = GracePeriodType.valueOf(command.gracePeriodType().toUpperCase());
        this.gracePeriodDuration = command.gracePeriodDuration();
        this.currency = CurrencyType.valueOf(command.currency().toUpperCase());
        this.primeRate = command.primeRate();
        this.structuringRate = command.structuringRate();
        this.placementRate = command.placementRate();
        this.floatRate = command.floatRate();
        this.cavaliRate = command.cavaliRate();
    }
}

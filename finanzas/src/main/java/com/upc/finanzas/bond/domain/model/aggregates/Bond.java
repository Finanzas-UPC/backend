package com.upc.finanzas.bond.domain.model.aggregates;

import com.upc.finanzas.bond.domain.model.commands.CreateBondCommand;
import com.upc.finanzas.bond.domain.model.valueobjects.GracePeriodType;
import com.upc.finanzas.bond.domain.model.valueobjects.InterestType;
import com.upc.finanzas.iam.domain.model.aggregates.User;
import com.upc.finanzas.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
public class Bond extends AuditableAbstractAggregateRoot<Bond> {
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @NotNull(message = "Asignarle un nombre al bono es obligatorio")
    private String name;
    @Digits(integer = 10, fraction = 2)
    @Positive
    private BigDecimal amount; // Valor nominal del bono
    @Positive
    private int duration; // Plazo total en años
    @Max(365)
    @Min(1)
    private int frequency; // Frecuencia del cupon (1 diario, 30 mensual, ...)
    @NotNull(message = "Indicar el tipo de interés es obligatorio (efectiva o nominal)")
    @Enumerated(value = EnumType.STRING)
    private InterestType interestType; // Tipo de interés (efectiva o nominal)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Digits(integer = 8, fraction = 4)
    private BigDecimal interestRate; // Tasa de interés del bono (%)
    @Max(365)
    @Min(1)
    private int capitalization;
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Digits(integer = 8, fraction = 4)
    private BigDecimal annualDiscountRate;
    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate emissionDate;
    @NotNull(message = "El tipo de periodo de gracia es obligatorio (total, parcial o ninguno)")
    @Enumerated(EnumType.STRING)
    private GracePeriodType gracePeriodType; // Tipo de periodo de gracia (total, parcial o sin periodo de gracia)
    @Min(0)
    private int gracePeriodDuration;

    public Bond() {}

    public Bond(User user, String name, BigDecimal amount, int duration, int frequency, InterestType interestType,
                BigDecimal interestRate, int capitalization, BigDecimal annualDiscountRate, LocalDate emissionDate,
                GracePeriodType gracePeriodType, int gracePeriodDuration) {
        this.user = user;
        this.name = name;
        this.amount = amount;
        this.duration = duration;
        this.frequency = frequency;
        this.interestType = interestType;
        this.interestRate = interestRate;
        this.capitalization = capitalization;
        this.annualDiscountRate = annualDiscountRate;
        this.emissionDate = emissionDate;
        this.gracePeriodType = gracePeriodType;
        this.gracePeriodDuration = gracePeriodDuration;
    }

    public Bond (User user, CreateBondCommand command) {
        this.user = user;
        this.name = command.name();
        this.amount = command.amount();
        this.duration = command.duration();
        this.frequency = command.frequency();
        this.interestType = InterestType.valueOf(command.interestType());
        this.interestRate = command.interestRate();
        this.capitalization = command.capitalization();
        this.annualDiscountRate = command.annualDiscountRate();
        this.emissionDate = command.emissionDate();
        this.gracePeriodType = GracePeriodType.valueOf(command.gracePeriodType());
        this.gracePeriodDuration = command.gracePeriodDuration();
    }
}

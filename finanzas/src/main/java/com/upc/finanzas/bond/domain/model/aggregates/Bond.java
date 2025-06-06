package com.upc.finanzas.bond.domain.model.aggregates;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.upc.finanzas.bond.domain.model.commands.CreateBondCommand;
import com.upc.finanzas.bond.domain.model.commands.UpdateBondCommand;
import com.upc.finanzas.bond.domain.model.valueobjects.GracePeriodType;
import com.upc.finanzas.bond.domain.model.valueobjects.InterestType;
import com.upc.finanzas.iam.domain.model.aggregates.User;
import com.upc.finanzas.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
    @Digits(integer = 8, fraction = 2)
    @Positive
    private BigDecimal amount; // Valor nominal del bono
    @Positive
    private BigDecimal marketValue; // Valor de mercado del bono
    @Positive
    private int duration; // Plazo total en años
    @Max(365)
    @Min(1)
    private int frequency; // Frecuencia del cupon (1 diario, 30 mensual, ...)
    @NotNull(message = "Indicar el tipo de tasa de interés es obligatorio (efectiva o nominal)")
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
    private BigDecimal marketRate;
    @NotNull(message = "La fecha de emisión es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate emissionDate;
    @NotNull(message = "El tipo de periodo de gracia es obligatorio (total, parcial o ninguno)")
    @Enumerated(EnumType.STRING)
    private GracePeriodType gracePeriodType; // Tipo de periodo de gracia (total, parcial o sin periodo de gracia)
    @Min(0)
    private int gracePeriodDuration;

    public Bond (User user, CreateBondCommand command) {
        this.user = user;
        this.name = command.name();
        this.amount = command.amount();
        this.marketValue = command.marketValue();
        this.duration = command.duration();
        this.frequency = command.frequency();
        this.interestType = InterestType.valueOf(command.interestType().toUpperCase());
        this.interestRate = command.interestRate();
        this.capitalization = command.capitalization();
        this.marketRate = command.marketRate();
        this.emissionDate = command.emissionDate();
        this.gracePeriodType = GracePeriodType.valueOf(command.gracePeriodType().toUpperCase());
        this.gracePeriodDuration = command.gracePeriodDuration();
    }

    public void update(UpdateBondCommand command) {
        this.name = command.name();
        this.amount = command.amount();
        this.marketValue = command.marketValue();
        this.duration = command.duration();
        this.frequency = command.frequency();
        this.interestType = InterestType.valueOf(command.interestType().toUpperCase());
        this.interestRate = command.interestRate();
        this.capitalization = command.capitalization();
        this.marketRate = command.marketRate();
        this.emissionDate = command.emissionDate();
        this.gracePeriodType = GracePeriodType.valueOf(command.gracePeriodType().toUpperCase());
        this.gracePeriodDuration = command.gracePeriodDuration();
    }
}

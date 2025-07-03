package com.upc.finanzas.bond.domain.model.aggregates;

import com.upc.finanzas.bond.domain.model.commands.CreateConfigurationCommand;
import com.upc.finanzas.bond.domain.model.commands.UpdateConfigurationCommand;
import com.upc.finanzas.bond.domain.model.valueobjects.CurrencyType;
import com.upc.finanzas.bond.domain.model.valueobjects.InterestType;
import com.upc.finanzas.iam.domain.model.aggregates.User;
import com.upc.finanzas.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Configuration extends AuditableAbstractAggregateRoot<Configuration> {
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @NotNull(message = "Indicar el tipo de tasa de interés es obligatorio (efectiva o nominal)")
    @Enumerated(value = EnumType.STRING)
    private InterestType interestType; // Tipo de interés (efectiva o nominal)
    @Max(365)
    @Min(1)
    private int capitalization; // Capitalización del interés (1: diaria, 7: semanal, 15: quincenal, 30: mensual, 60: bimestral, 90: trimestral, 180: semestral, 360: anual)
    @NotNull
    @Enumerated(value = EnumType.STRING)
    private CurrencyType currency;

    public Configuration(User user, CreateConfigurationCommand command) {
        this.user = user;
        this.interestType = InterestType.valueOf(command.interestType());
        this.capitalization = command.capitalization();
        this.currency = CurrencyType.valueOf(command.currency());
    }

    public void update(UpdateConfigurationCommand command) {
        this.interestType = InterestType.valueOf(command.interestType());
        this.capitalization = command.capitalization();
        this.currency = CurrencyType.valueOf(command.currency());
    }
}

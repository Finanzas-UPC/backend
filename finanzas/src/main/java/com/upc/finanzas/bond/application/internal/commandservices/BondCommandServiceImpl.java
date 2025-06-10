package com.upc.finanzas.bond.application.internal.commandservices;

import com.upc.finanzas.bond.application.internal.outboundservices.acl.ExternalUserService;
import com.upc.finanzas.bond.domain.exceptions.BondCashFlowException;
import com.upc.finanzas.bond.domain.exceptions.BondMetricsException;
import com.upc.finanzas.bond.domain.exceptions.BondNotFoundException;
import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import com.upc.finanzas.bond.domain.model.commands.CreateBondCommand;
import com.upc.finanzas.bond.domain.model.commands.DeleteBondCommand;
import com.upc.finanzas.bond.domain.model.commands.UpdateBondCommand;
import com.upc.finanzas.bond.domain.services.BondCalculatorService;
import com.upc.finanzas.bond.domain.services.BondCommandService;
import com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories.BondMetricsRepository;
import com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories.BondRepository;
import com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories.CashFlowItemRepository;
import com.upc.finanzas.shared.domain.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BondCommandServiceImpl implements BondCommandService {
    private final BondRepository bondRepository;
    private final BondMetricsRepository bondMetricsRepository;
    private final CashFlowItemRepository cashFlowItemRepository;
    private final BondCalculatorService bondCalculatorService;
    private final ExternalUserService externalUserService;

    public BondCommandServiceImpl(
            BondRepository bondRepository,
            BondMetricsRepository bondMetricsRepository,
            CashFlowItemRepository cashFlowItemRepository,
            BondCalculatorService bondCalculatorService,
            ExternalUserService externalUserService) {
        this.bondRepository = bondRepository;
        this.bondMetricsRepository = bondMetricsRepository;
        this.cashFlowItemRepository = cashFlowItemRepository;
        this.bondCalculatorService = bondCalculatorService;
        this.externalUserService = externalUserService;
    }

    @Override
    @Transactional
    public Long handle(CreateBondCommand command) {
        // Se verifica si el usuario existe
        var user = externalUserService.fetchUserById(command.userId());
        if (user.isEmpty()) throw new UserNotFoundException(command.userId());
        // Si el usuario existe, se crea el bono
        var bond = new Bond(user.get(), command);
        bondRepository.save(bond);
        // Se generan los flujos de caja del bono
        var cashFlowItems = bondCalculatorService.generateCashFlowItems(bond);
        if (cashFlowItems.isEmpty()) throw new BondCashFlowException();
        // Se guardan los flujos de caja generados
        cashFlowItemRepository.saveAll(cashFlowItems);
        // Se generan las métricas del bono
        var bondMetrics = bondCalculatorService.generateBondMetrics(bond, cashFlowItems)
                .orElseThrow(BondMetricsException::new);
        // Se guardan las métricas del bono
        bondMetricsRepository.save(bondMetrics);
        // Se retorna el ID del bono creado
        return bond.getId();
    }

    @Override
    @Transactional
    public Long handle(UpdateBondCommand command) {
        var existingBond = bondRepository.findById(command.bondId());
        if (existingBond.isEmpty()) throw new BondNotFoundException(command.bondId());

        var bond = existingBond.get();
        bond.update(command);
        bondRepository.save(bond);
        // Eliminar flujos anteriores
        cashFlowItemRepository.deleteAllByBond_Id(bond.getId());
        // Recalcular flujos
        var cashFlowItems = bondCalculatorService.generateCashFlowItems(bond);
        if (cashFlowItems.isEmpty()) throw new BondCashFlowException();
        cashFlowItemRepository.saveAll(cashFlowItems);
        // Recalcular métricas
        var bondMetrics = bondCalculatorService.generateBondMetrics(bond, cashFlowItems)
                .orElseThrow(BondMetricsException::new);
        var existingMetrics = bondMetricsRepository.findByBond_Id(bond.getId())
                        .orElseThrow(BondMetricsException::new);
        existingMetrics.update(bondMetrics);
        bondMetricsRepository.save(existingMetrics);
        // Retornar el ID del bono actualizado
        return bond.getId();
    }

    @Override
    @Transactional
    public void handle(DeleteBondCommand command) {
        var bond = bondRepository.findById(command.bondId());
        if (bond.isEmpty()) throw new BondNotFoundException(command.bondId());

        // Eliminar dependencias antes del bono
        cashFlowItemRepository.deleteAllByBond_Id(command.bondId());
        bondMetricsRepository.deleteByBond_Id(command.bondId());
        bondRepository.delete(bond.get());
    }
}

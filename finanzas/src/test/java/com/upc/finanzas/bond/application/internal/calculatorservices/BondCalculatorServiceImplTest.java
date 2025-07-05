package com.upc.finanzas.bond.application.internal.calculatorservices;

import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import com.upc.finanzas.bond.domain.model.commands.CreateBondCommand;
import com.upc.finanzas.bond.domain.model.entities.BondMetrics;
import com.upc.finanzas.bond.domain.model.entities.CashFlowItem;
import com.upc.finanzas.bond.domain.model.valueobjects.GracePeriodType;
import com.upc.finanzas.bond.domain.model.valueobjects.InterestType;
import com.upc.finanzas.iam.domain.model.aggregates.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BondCalculatorServiceImplTest {

    private final BondCalculatorServiceImpl service = new BondCalculatorServiceImpl();

    private Bond createSampleBond() {
        User user = new User("piero", "12345678");
        CreateBondCommand command = new CreateBondCommand(
                user.getId(),
                "Bono de prueba 2",
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(10500),
                5,
                3,
                "EFECTIVA",
                BigDecimal.valueOf(8.0),
                1,
                BigDecimal.valueOf(4.5),
                LocalDate.of(2025, 5, 10),
                "PARCIAL",
                2,
                "PEN",
                BigDecimal.valueOf(1.0),
                BigDecimal.valueOf(1.0),
                BigDecimal.valueOf(0.25),
                BigDecimal.valueOf(0.45),
                BigDecimal.valueOf(0.5)
        );
        return new Bond(user, command);
    }

    @Test
    void generateCashFlowItems() {
        Bond bond = createSampleBond();
        List<CashFlowItem> items = service.generateCashFlowItems(bond);

        for (CashFlowItem item : items) {
            System.out.println("CashFlowItem: " + item.getBondHolderCashFlow());
        }

        assertNotNull(items);
        assertEquals(bond.getDuration() * bond.getFrequency() + 1, items.size()); // +1 por periodo 0
    }

    @Test
    void generateBondMetrics() {
        Bond bond = createSampleBond();
        List<CashFlowItem> items = service.generateCashFlowItems(bond);
        Optional<BondMetrics> metricsOpt = service.generateBondMetrics(bond, items);

        assertTrue(metricsOpt.isPresent());
        BondMetrics metrics = metricsOpt.get();

        assertEquals(metrics.getMaxPrice(), BigDecimal.valueOf(10933.96));
        assertEquals(metrics.getConvexity(), BigDecimal.valueOf(87.62));
        assertEquals(metrics.getTcea(), BigDecimal.valueOf(0.069544421));
        assertEquals(metrics.getTrea(), BigDecimal.valueOf(0.052232911));
    }
}
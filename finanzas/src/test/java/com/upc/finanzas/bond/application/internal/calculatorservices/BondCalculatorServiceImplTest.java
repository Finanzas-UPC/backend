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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BondCalculatorServiceImplTest {

    private final BondCalculatorServiceImpl service = new BondCalculatorServiceImpl();

    private Bond createSampleBond() {
        User user = new User("piero", "12345678");
        CreateBondCommand command = new CreateBondCommand(
                user.getId(),
                "Bono de prueba",
                BigDecimal.valueOf(30000),
                BigDecimal.valueOf(30000),
                5,
                4,
                "NOMINAL",
                BigDecimal.valueOf(10.0),
                30,
                BigDecimal.valueOf(4.5),
                LocalDate.of(2025, 5, 10),
                "TOTAL",
                5
        );
        return new Bond(user, command);
    }

    @Test
    void generateCashFlowItems() {
        Bond bond = createSampleBond();
        List<CashFlowItem> items = service.generateCashFlowItems(bond);

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

        assertNotNull(metrics.getDuration());
        assertNotNull(metrics.getConvexity());
        assertNotNull(metrics.getTcea());
        assertNotNull(metrics.getTrea());

        assertTrue(metrics.getTcea().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(metrics.getTrea().compareTo(BigDecimal.ZERO) > 0);
    }
}
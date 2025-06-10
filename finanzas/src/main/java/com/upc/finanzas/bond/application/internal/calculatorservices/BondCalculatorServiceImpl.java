package com.upc.finanzas.bond.application.internal.calculatorservices;

import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import com.upc.finanzas.bond.domain.model.entities.BondMetrics;
import com.upc.finanzas.bond.domain.model.entities.CashFlowItem;
import com.upc.finanzas.bond.domain.model.valueobjects.GracePeriodType;
import com.upc.finanzas.bond.domain.model.valueobjects.InterestType;
import com.upc.finanzas.bond.domain.services.BondCalculatorService;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BondCalculatorServiceImpl implements BondCalculatorService {
    private static final MathContext mc = new MathContext(10, RoundingMode.HALF_UP);

    @Override
    public List<CashFlowItem> generateCashFlowItems(Bond bond) {
        // Resultados de estructuración del bono
        int frequencyDays = 360 / bond.getFrequency();
        int totalPeriods = bond.getDuration() * bond.getFrequency();
        BigDecimal annualEffectiveRate = getAnnualEffectiveRate(bond);
        BigDecimal periodicRate = calculatePeriodicRate(annualEffectiveRate, frequencyDays);
        BigDecimal discountRate = calculatePeriodicRate(bond.getMarketRate().divide(BigDecimal.valueOf(100), mc), frequencyDays);

        // Inicialización de variables
        List<CashFlowItem> cashFlowItems = new ArrayList<>();
        BigDecimal balance = bond.getAmount();
        LocalDate currentDate = bond.getEmissionDate();

        // Período 0: flujo inicial
        cashFlowItems.add(new CashFlowItem(null, bond, 0, currentDate, false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, bond.getAmount(),
                BigDecimal.ZERO, bond.getAmount(), bond.getAmount().negate(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        ));

        // Período 1 a N: flujos periódicos
        for (int period = 1; period <= totalPeriods; period++) {
            currentDate = currentDate.plusDays(frequencyDays);
            BigDecimal interest = balance.multiply(periodicRate, mc);
            BigDecimal amortization = BigDecimal.ZERO;
            boolean isGrace = period <= bond.getGracePeriodDuration();
            if (!isGrace) {
                BigDecimal amortizableAmount = cashFlowItems.get(bond.getGracePeriodDuration()).getFinalBalance();
                amortization = amortizableAmount.divide(BigDecimal.valueOf(totalPeriods - bond.getGracePeriodDuration()), mc);
            }
            // Declaración de variables para el flujo de caja
            BigDecimal totalPayment;
            BigDecimal finalBalance;
            BigDecimal investorCashFlow;
            BigDecimal issuerCashFlow;

            if (isGrace) {
                if (bond.getGracePeriodType().equals(GracePeriodType.TOTAL)) {
                    totalPayment = BigDecimal.ZERO;
                    finalBalance = balance.add(interest); // Capitalizas el interés
                    investorCashFlow = BigDecimal.ZERO;
                } else { // Parcial
                    totalPayment = interest;
                    finalBalance = balance; // No se amortiza ni capitaliza
                    investorCashFlow = totalPayment;
                }
            } else { // Fuera del periodo de gracia
                totalPayment = interest.add(amortization);
                finalBalance = balance.subtract(amortization);
                investorCashFlow = totalPayment;
            }
            // Ajuste del balance final
            finalBalance = finalBalance.setScale(10, RoundingMode.HALF_UP);
            if (finalBalance.abs().compareTo(new BigDecimal("0.00001")) < 0) {
                finalBalance = BigDecimal.ZERO;
            }

            issuerCashFlow = investorCashFlow.negate();

            BigDecimal discountFactor = calculateDiscountFactor(discountRate, period);
            BigDecimal discountedFlow = calculateDiscountedFlow(investorCashFlow, discountFactor);
            BigDecimal discountedFlowTimesPeriod = calculateDiscountedFlowTimesPeriod(discountedFlow, period, bond.getFrequency());
            BigDecimal convexityFactor = calculateConvexityFactor(discountedFlow, period);

            cashFlowItems.add(new CashFlowItem(
                    null, bond, period, currentDate, isGrace,
                    balance.setScale(2, RoundingMode.HALF_UP),
                    interest.setScale(2, RoundingMode.HALF_UP),
                    amortization.setScale(2, RoundingMode.HALF_UP),
                    finalBalance.setScale(2, RoundingMode.HALF_UP),
                    totalPayment.setScale(2, RoundingMode.HALF_UP),
                    issuerCashFlow.setScale(2, RoundingMode.HALF_UP),
                    investorCashFlow.setScale(2, RoundingMode.HALF_UP),
                    discountedFlow.setScale(2, RoundingMode.HALF_UP),
                    discountedFlowTimesPeriod.setScale(2, RoundingMode.HALF_UP),
                    convexityFactor.setScale(2, RoundingMode.HALF_UP)
            ));
            balance = finalBalance; // Actualizar el balance para el siguiente periodo
        }
        return cashFlowItems;
    }

    @Override
    public Optional<BondMetrics> generateBondMetrics(Bond bond, List<CashFlowItem> cashFlowItems) {
        BigDecimal annualRateDecimal = bond.getMarketRate().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        int frequencyDays = 360 / bond.getFrequency();
        BigDecimal marketRatePerPeriod = calculatePeriodicRate(annualRateDecimal, frequencyDays);
        // Calcular métricas del bono
        BigDecimal duration = calculateDuration(cashFlowItems);
        BigDecimal convexity = calculateConvexity(cashFlowItems, marketRatePerPeriod, frequencyDays);
        BigDecimal modifiedDuration = calculateModifiedDuration(duration, marketRatePerPeriod);
        BigDecimal totalDurationConvexity = calculateTotalDurationConvexity(duration, convexity);
        BigDecimal tcea = calculateTCEA(cashFlowItems, bond.getFrequency());
        BigDecimal trea = calculateTREA(cashFlowItems, bond.getFrequency());

        BondMetrics metrics = new BondMetrics(
                null, bond,
                duration.setScale(2, RoundingMode.HALF_UP),
                convexity.setScale(2, RoundingMode.HALF_UP),
                totalDurationConvexity.setScale(2, RoundingMode.HALF_UP),
                modifiedDuration.setScale(2, RoundingMode.HALF_UP),
                tcea.setScale(4, RoundingMode.HALF_UP),
                trea.setScale(4, RoundingMode.HALF_UP)
        );

        return Optional.of(metrics);
    }

    private BigDecimal getAnnualEffectiveRate(Bond bond) {
        if (bond.getInterestType().equals(InterestType.EFECTIVA)) {
            BigDecimal rate = bond.getInterestRate().divide(BigDecimal.valueOf(100), mc);
            int m = bond.getFrequency(); // frecuencia del cupón
            return BigDecimal.ONE.add(rate).pow(m, mc).subtract(BigDecimal.ONE);
        } else {
            // Convertir tasa nominal a efectiva anual usando capitalización
            BigDecimal r = bond.getInterestRate().divide(BigDecimal.valueOf(100), mc);
            int m = 360 / bond.getCapitalization();
            return BigDecimal.ONE.add(r.divide(BigDecimal.valueOf(m), mc)).pow(m, mc).subtract(BigDecimal.ONE);
        }
    }

    private BigDecimal calculatePeriodicRate(BigDecimal annualRate, int frequencyDays) {
        double exponent = (double) frequencyDays / 360.0;
        double base = BigDecimal.ONE.add(annualRate).doubleValue();
        double result = Math.pow(base, exponent) - 1.0;
        return BigDecimal.valueOf(result).setScale(6, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDiscountFactor(BigDecimal rate, int period) {
        return BigDecimal.ONE.divide(BigDecimal.ONE.add(rate).pow(period, mc), mc);
    }

    private BigDecimal calculateDiscountedFlow(BigDecimal cashFlow, BigDecimal discountFactor) {
        return cashFlow.multiply(discountFactor, mc);
    }

    private BigDecimal calculateDiscountedFlowTimesPeriod(BigDecimal discountedFlow, int period, int frequency) {
        BigDecimal timeInYears = BigDecimal.valueOf(period).divide(BigDecimal.valueOf(frequency), mc);
        return discountedFlow.multiply(timeInYears, mc);
    }

    private BigDecimal calculateConvexityFactor(BigDecimal discountedFlow, int period) {
        BigDecimal periodFactor = BigDecimal.valueOf(period).multiply(BigDecimal.valueOf(period + 1));
        return discountedFlow.multiply(periodFactor, mc);
    }

    private BigDecimal calculateDuration(List<CashFlowItem> cashFlowItems) {
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal totalDiscountedFlow = BigDecimal.ZERO;

        for (CashFlowItem item : cashFlowItems) {
            sum = sum.add(item.getDiscountedFlowTimesPeriod());
            totalDiscountedFlow = totalDiscountedFlow.add(item.getDiscountedFlow());
        }

        if (totalDiscountedFlow.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return sum.divide(totalDiscountedFlow, 10, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateConvexity(List<CashFlowItem> cashFlowItems, BigDecimal ratePerPeriod, int frequencyDays) {
        BigDecimal sumConvexityFactors = BigDecimal.ZERO;
        BigDecimal sumDiscountedFlows = BigDecimal.ZERO;

        for (CashFlowItem item : cashFlowItems) {
            sumConvexityFactors = sumConvexityFactors.add(item.getConvexityFactor());
            sumDiscountedFlows = sumDiscountedFlows.add(item.getDiscountedFlow());
        }

        if (sumDiscountedFlows.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal rateFactor = BigDecimal.ONE.add(ratePerPeriod).pow(2);
        BigDecimal freqFactor = BigDecimal.valueOf(360.0 / frequencyDays).pow(2);

        return sumConvexityFactors
                .divide(rateFactor.multiply(sumDiscountedFlows).multiply(freqFactor), 10, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateModifiedDuration(BigDecimal duration, BigDecimal ratePerPeriod) {
        return duration.divide(BigDecimal.ONE.add(ratePerPeriod), 10, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalDurationConvexity(BigDecimal duration, BigDecimal convexity) {
        return duration.add(convexity);
    }

    private BigDecimal calculateIRR(List<CashFlowItem> cashFlowItems, java.util.function.Function<CashFlowItem, BigDecimal> cashFlowSelector) {
        double[] cashFlows = cashFlowItems.stream()
                .map(cashFlowSelector)
                .mapToDouble(BigDecimal::doubleValue)
                .toArray();

        UnivariateFunction npvFunction = r -> {
            double npv = 0.0;
            for (int t = 0; t < cashFlows.length; t++) {
                npv += cashFlows[t] / Math.pow(1 + r, t);
            }
            return npv;
        };

        BrentSolver solver = new BrentSolver(1e-10, 1e-14);
        double irr = solver.solve(1000, npvFunction, -0.9999, 1.0, 0.1); // intervalo de búsqueda

        return BigDecimal.valueOf(irr).setScale(10, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTREA(List<CashFlowItem> cashFlowItems, int frequency) {
        BigDecimal tir = calculateIRR(cashFlowItems, CashFlowItem::getBondHolderCashFlow);
        double annualRate = Math.pow(1 + tir.doubleValue(), frequency) - 1;
        return BigDecimal.valueOf(annualRate).setScale(10, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTCEA(List<CashFlowItem> cashFlowItems, int frequency) {
        BigDecimal tir = calculateIRR(cashFlowItems, CashFlowItem::getIssuerCashFlow);
        double annualRate = Math.pow(1 + tir.doubleValue(), frequency) - 1;
        return BigDecimal.valueOf(annualRate).setScale(10, RoundingMode.HALF_UP);
    }
}
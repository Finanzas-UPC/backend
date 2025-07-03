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
        int totalPeriods = bond.getDuration() * bond.getFrequency();
        BigDecimal TEA = getTEA(bond);
        BigDecimal periodInterestRate = getPeriodicInterestRate(TEA, bond.getFrequency());
        BigDecimal periodDiscountRate = getPeriodDiscountRate(bond.getDiscountRate().divide(BigDecimal.valueOf(100), mc), bond.getFrequency());
        // Inicialización de variables
        List<CashFlowItem> cashFlowItems = new ArrayList<>();
        BigDecimal balance = bond.getNominalValue();
        LocalDate currentDate = bond.getEmissionDate();
        BigDecimal initialExpensesIssuer = (bond.getStructuringRate().add(bond.getPlacementRate()).add(bond.getFloatRate()).add(bond.getCavaliRate()))
                .divide(BigDecimal.valueOf(100), mc)
                .multiply(bond.getMarketValue()); // Gastos iniciales del emisor
        BigDecimal initialExpensesBondHolder = (bond.getStructuringRate().add(bond.getPlacementRate()).add(bond.getFloatRate()).add(bond.getCavaliRate()))
                .divide(BigDecimal.valueOf(100), mc)
                .multiply(bond.getMarketValue()); // Gastos iniciales del bonista

        BigDecimal issuerCashFlow = bond.getMarketValue().subtract(initialExpensesIssuer); // Flujo de caja del emisor (ingreso para el emisor)
        BigDecimal bondHolderCashFlow = bond.getMarketValue().add(initialExpensesBondHolder); // Flujo de caja del bonista (inversor)

        // Período 0: flujo inicial
        cashFlowItems.add(new CashFlowItem(null, bond, 0, currentDate, false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, bond.getNominalValue(),
                BigDecimal.ZERO, issuerCashFlow, bondHolderCashFlow.negate(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        ));

        // Período 1 a N: flujos periódicos
        for (int period = 1; period <= totalPeriods; period++) {
            currentDate = currentDate.plusDays(360/bond.getFrequency());
            BigDecimal amortization = BigDecimal.ZERO;
            // i = balance * (TEP)
            BigDecimal interest = balance.multiply(periodInterestRate, mc);
            if (period == totalPeriods) {
                // Periodo final considera el gasto de prima
                // i = balance * (TEP + PrimeRate)
                interest = balance.multiply(periodInterestRate.add(bond.getPrimeRate().divide(BigDecimal.valueOf(100), mc)), mc);
            }

            boolean isGrace = period <= bond.getGracePeriodDuration();
            if (!isGrace) {
                BigDecimal amortizableAmount = cashFlowItems.get(bond.getGracePeriodDuration()).getFinalBalance();
                // amortizacion = valor capitalizado / (n - g) donde n es el total de periodos y g es el total de periodos de gracia
                amortization = amortizableAmount.divide(BigDecimal.valueOf(totalPeriods - bond.getGracePeriodDuration()), mc);
            }
            // Declaración de variables para el flujo de caja
            BigDecimal totalPayment;
            BigDecimal finalBalance;

            // Cálculo de los flujos de caja según el periodo de gracia
            if (isGrace) {
                if (bond.getGracePeriodType().equals(GracePeriodType.TOTAL)) {
                    totalPayment = BigDecimal.ZERO;
                    finalBalance = balance.add(interest); // Capitalizas el interés
                    bondHolderCashFlow = BigDecimal.ZERO;
                } else { // Parcial
                    totalPayment = interest;
                    finalBalance = balance; // No se amortiza ni capitaliza
                    bondHolderCashFlow = totalPayment;
                }
            }
            else { // Fuera del periodo de gracia
                totalPayment = interest.add(amortization);
                finalBalance = balance.subtract(amortization);
                bondHolderCashFlow = totalPayment;
            }

            // Ajuste del balance final
            finalBalance = finalBalance.setScale(10, RoundingMode.HALF_UP);
            if (finalBalance.abs().compareTo(new BigDecimal("0.00001")) < 0) {
                finalBalance = BigDecimal.ZERO;
            }

            issuerCashFlow = bondHolderCashFlow.negate();

            // Cálculo de datos intermedios
            // FC/(1+r)^n
            BigDecimal presentFlow = getPresentValue(bondHolderCashFlow, periodDiscountRate, period);
            // FC/(1+r)^n * t
            BigDecimal presentFlowTimesPeriod = presentFlow.multiply(BigDecimal.valueOf(period));
            // FC/(1+r)^n * t * (t + 1)
            BigDecimal convexityFactor = presentFlowTimesPeriod.multiply(BigDecimal.valueOf(period).add(BigDecimal.ONE));

            cashFlowItems.add(new CashFlowItem(
                    null, bond, period, currentDate, isGrace,
                    balance.setScale(2, RoundingMode.HALF_UP),
                    interest.setScale(2, RoundingMode.HALF_UP),
                    amortization.setScale(2, RoundingMode.HALF_UP),
                    finalBalance.setScale(2, RoundingMode.HALF_UP),
                    totalPayment.setScale(2, RoundingMode.HALF_UP),
                    issuerCashFlow.setScale(2, RoundingMode.HALF_UP),
                    bondHolderCashFlow.setScale(2, RoundingMode.HALF_UP),
                    presentFlow.setScale(2, RoundingMode.HALF_UP),
                    presentFlowTimesPeriod.setScale(2, RoundingMode.HALF_UP),
                    convexityFactor.setScale(2, RoundingMode.HALF_UP)
            ));
            balance = finalBalance; // Actualizar el balance para el siguiente periodo
        }
        return cashFlowItems;
    }

    @Override
    public Optional<BondMetrics> generateBondMetrics(Bond bond, List<CashFlowItem> cashFlowItems) {
        BigDecimal periodDiscountRate = getPeriodDiscountRate(bond.getDiscountRate().divide(BigDecimal.valueOf(100), mc), bond.getFrequency());
        // Calcular métricas del bono
        BigDecimal maxBondPrice = getMaxBondPrice(cashFlowItems.stream().map(CashFlowItem::getPresentCashFlow).toList()); // Flujos de caja al presente
        BigDecimal duration = getDuration(cashFlowItems.stream().map(CashFlowItem::getPresentCashFlowTimesPeriod).toList(),  // Flujos de caja al presente por periodo
                maxBondPrice);
        BigDecimal modifiedDuration = getModifiedDuration(duration, periodDiscountRate);
        BigDecimal convexity = getConvexity(
                cashFlowItems.stream().map(CashFlowItem::getConvexityFactor).toList(), // Factores de convexidad
                maxBondPrice, periodDiscountRate);
        BigDecimal tcea = getTCEA(cashFlowItems, bond.getFrequency());
        BigDecimal trea = getTREA(cashFlowItems, bond.getFrequency());

        BondMetrics metrics = new BondMetrics(
                null, bond,
                maxBondPrice.setScale(2, RoundingMode.HALF_UP),
                duration.setScale(2, RoundingMode.HALF_UP),
                convexity.setScale(2, RoundingMode.HALF_UP),
                modifiedDuration.setScale(2, RoundingMode.HALF_UP),
                tcea.setScale(4, RoundingMode.HALF_UP),
                trea.setScale(4, RoundingMode.HALF_UP)
        );

        return Optional.of(metrics);
    }

    private BigDecimal getPresentValue(BigDecimal value, BigDecimal periodDiscountRate, int period) {
        // (1 + r)^n donde r es la tasa de descuento y n es el periodo
        BigDecimal divisor = BigDecimal.ONE.add(periodDiscountRate).pow(period, mc);
        // Se calcula el valor presente del flujo de caja
        return value.divide(divisor, mc);
    }

    private BigDecimal getMaxBondPrice(List<BigDecimal> presentCashFlows) {
        // Se inicializa el precio máximo del bono en 0
        BigDecimal maxPrice = BigDecimal.ZERO;
        // Sumatoria de flujos de caja en tiempo presente (desde el periodo 1)
        for (int i = 1; i < presentCashFlows.size(); i++) {
            maxPrice = maxPrice.add(presentCashFlows.get(i));
        }
        // Se retorna el precio máximo del bono con dos decimales
        return maxPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDuration(List<BigDecimal> presentCashFlowsTimesPeriod, BigDecimal maxBondPrice) {
        // Se inicializa la duración en 0
        BigDecimal sumPresentCashFlows = BigDecimal.ZERO;
        // Se suma el producto de los flujos de caja en tiempo presente por su periodo
        for (int i = 1; i < presentCashFlowsTimesPeriod.size(); i++) {
            sumPresentCashFlows = sumPresentCashFlows.add(presentCashFlowsTimesPeriod.get(i), mc);
        }
        // Se divide la sumatoria por el precio máximo del bono para obtener la duración
        return sumPresentCashFlows.divide(maxBondPrice, mc);
    }

    private BigDecimal getModifiedDuration(BigDecimal duration, BigDecimal periodDiscountRate) {
        // Se calcula la duración modificada como la duración dividida por (1 + COK)
        return duration.divide(BigDecimal.ONE.add(periodDiscountRate), mc);
    }

    private BigDecimal getConvexity(List<BigDecimal> convexityFactor, BigDecimal maxBondPrice, BigDecimal periodDiscountRate) {
        // Se inicializa la suma de factores de convexidad en 0
        BigDecimal sumConvexityFactors = BigDecimal.ZERO;
        // Se obtiene la sumatoria de los factores de convexidad
        for (int i = 1; i < convexityFactor.size(); i++) {
            sumConvexityFactors = sumConvexityFactors.add(convexityFactor.get(i), mc);
        }
        // Se calcula la convexidad sum/P*(1+r)^2
        return sumConvexityFactors.divide(maxBondPrice.multiply((BigDecimal.ONE.add(periodDiscountRate)).pow(2, mc)), mc);
    }

    private BigDecimal getTREA(List<CashFlowItem> cashFlowItems, int frequency) {
        BigDecimal trep = calculateIRR(cashFlowItems.stream().map(CashFlowItem::getBondHolderCashFlow).toList());
        // TREA = (1 + TREP)^(n) - 1 donde n es el número de periodos en un año
        return BigDecimal.ONE.add(trep).pow(frequency, mc).subtract(BigDecimal.ONE);
    }

    private BigDecimal getTCEA(List<CashFlowItem> cashFlowItems, int frequency) {
        // TCEA = (1 + TCEP)^(n) - 1 donde n es el número de periodos en un año
        BigDecimal tcep = calculateIRR(cashFlowItems.stream().map(CashFlowItem::getIssuerCashFlow).toList());
        return BigDecimal.ONE.add(tcep).pow(frequency, mc).subtract(BigDecimal.ONE);
    }

    // Conversiones de tasas de interés

    private BigDecimal getTEA(Bond bond) {
        if (bond.getInterestType().equals(InterestType.EFECTIVA)) {
            // Tasa efectiva anual ya está en formato TEA
            return bond.getInterestRate().divide(BigDecimal.valueOf(100), mc);
        } else {
            // Convertir tasa nominal a efectiva anual usando capitalización
            // TNP% -> TNP
            BigDecimal TNP = bond.getInterestRate().divide(BigDecimal.valueOf(100), mc);
            int m = 360 / bond.getCapitalization();
            // (1 + TNP/m)^m - 1 = TEA
            return BigDecimal.ONE.add(TNP.divide(BigDecimal.valueOf(m), mc)).pow(m, mc).subtract(BigDecimal.ONE);
        }
    }

    private BigDecimal getPeriodicInterestRate(BigDecimal TEA, int frequency) {
        // TEA -> TEP = (1+TEA)^(1/n) - 1 donde n es el número de periodos en un año
        BigDecimal base = BigDecimal.ONE.add(TEA);
        BigDecimal exponent = BigDecimal.ONE.divide(BigDecimal.valueOf(frequency), mc);
        double result = Math.pow(base.doubleValue(), exponent.doubleValue()) - 1;
        return BigDecimal.valueOf(result);
    }

    private BigDecimal getPeriodDiscountRate(BigDecimal discountRate, int frequency) {
        // TEA -> TEP = (1+COK)^(1/n) - 1 donde n es numero de periodos en un año
        BigDecimal base = BigDecimal.ONE.add(discountRate);
        double exponent = BigDecimal.ONE.divide(BigDecimal.valueOf(frequency), mc).doubleValue();
        double result = Math.pow(base.doubleValue(), exponent);
        return BigDecimal.valueOf(result).subtract(BigDecimal.ONE);
    }

    // Cálculo del TIR

    private BigDecimal calculateIRR(List<BigDecimal> cashFlowItems) {
        // Convertir a un array de double en vez de BigDecimals
        double[] cashFlows = cashFlowItems.stream()
                .mapToDouble(BigDecimal::doubleValue)
                .toArray();

        /* Calculo del valor actual neto (NPV) como función de la tasa de descuento
            Donde:
            r es la tasa de descuento (que se busca encontrar = TIR)
            cashFlows es un array de flujos de caja que depende si es del emisor o del bonista
         */

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
}
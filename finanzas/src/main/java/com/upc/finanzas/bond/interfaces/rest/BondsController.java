package com.upc.finanzas.bond.interfaces.rest;

import com.upc.finanzas.bond.application.internal.queryservices.CashFlowItemQueryServiceImpl;
import com.upc.finanzas.bond.domain.exceptions.BondNotFoundException;
import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import com.upc.finanzas.bond.domain.model.commands.DeleteBondCommand;
import com.upc.finanzas.bond.domain.model.entities.CashFlowItem;
import com.upc.finanzas.bond.domain.model.queries.GetAllBondsByUserIdQuery;
import com.upc.finanzas.bond.domain.model.queries.GetBondByIdQuery;
import com.upc.finanzas.bond.domain.model.queries.GetBondMetricsByBondId;
import com.upc.finanzas.bond.domain.model.queries.GetCashFlowByBondId;
import com.upc.finanzas.bond.domain.services.BondCommandService;
import com.upc.finanzas.bond.domain.services.BondMetricsQueryService;
import com.upc.finanzas.bond.domain.services.BondQueryService;
import com.upc.finanzas.bond.domain.services.CashFlowItemQueryService;
import com.upc.finanzas.bond.interfaces.rest.resources.*;
import com.upc.finanzas.bond.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@RestController
@RequestMapping(value="api/v1/bonds", produces = APPLICATION_JSON_VALUE)
@Tag(name="Bonds", description = "Bonds Management Endpoints")
public class BondsController {
    private final BondCommandService bondCommandService;
    private final BondQueryService bondQueryService;
    private final CashFlowItemQueryService cashFlowItemQueryService;
    private final BondMetricsQueryService bondMetricsQueryService;

    public BondsController(BondCommandService bondCommandService,
                           BondQueryService bondQueryService,
                           CashFlowItemQueryService cashFlowItemQueryService,
                           BondMetricsQueryService bondMetricsQueryService) {
        this.bondCommandService = bondCommandService;
        this.bondQueryService = bondQueryService;
        this.cashFlowItemQueryService = cashFlowItemQueryService;
        this.bondMetricsQueryService = bondMetricsQueryService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BondResource>> getBondsByUserId(@PathVariable Long userId) {
        var query = new GetAllBondsByUserIdQuery(userId);
        List<Bond> bonds = bondQueryService.handle(query);
        var bondResources = bonds.stream().map(BondResourceFromEntityAssembler::toResourceFromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(bondResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BondResource> getBondById(@PathVariable Long id) {
        var query = new GetBondByIdQuery(id);
        var bond = bondQueryService.handle(query);
        if (bond.isEmpty()) throw new BondNotFoundException(id);
        var bondResource = BondResourceFromEntityAssembler.toResourceFromEntity(bond.get());
        return ResponseEntity.ok(bondResource);
    }

    @GetMapping("/{id}/cashflow")
    public ResponseEntity<List<CashFlowItemResource>> getBondCashFlowById(@PathVariable Long id) {
        var query = new GetCashFlowByBondId(id);
        List<CashFlowItem> cashFlow = cashFlowItemQueryService.handle(query);
        if (cashFlow.isEmpty()) throw new BondNotFoundException(id);
        var cashFlowResource = cashFlow.stream().map(CashFlowItemResourceFromEntityAssembler::toResourceFromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(cashFlowResource);
    }

    @GetMapping("/{id}/metrics")
    public ResponseEntity<BondMetricsResource> getBondMetricsById(@PathVariable Long id) {
        var query = new GetBondMetricsByBondId(id);
        var metrics = bondMetricsQueryService.handle(query);
        if (metrics.isEmpty()) throw new BondNotFoundException(id);
        var metricsResource = BondMetricsResourceFromEntityAssembler.toResourceFromEntity(metrics.get());
        return ResponseEntity.ok(metricsResource);
    }

    @PostMapping
    public ResponseEntity<BondResource> createBond(@RequestBody CreateBondResource resource) {
        var command = CreateBondCommandFromResourceAssembler.toCommandFromResource(resource);
        Long bondId = bondCommandService.handle(command);
        var bond = bondQueryService.handle(new GetBondByIdQuery(bondId));
        if (bond.isEmpty()) throw new BondNotFoundException(bondId);
        var bondResource = BondResourceFromEntityAssembler.toResourceFromEntity(bond.get());
        return new ResponseEntity<>(bondResource, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BondResource> updateBondById(@PathVariable Long id, @RequestBody UpdateBondResource resource) {
        var command = UpdateBondCommandFromResourceAssembler.toCommandFromResource(id, resource);
        Long bondId = bondCommandService.handle(command);
        var bond = bondQueryService.handle(new GetBondByIdQuery(bondId));
        if (bond.isEmpty()) throw new BondNotFoundException(bondId);
        var bondResource = BondResourceFromEntityAssembler.toResourceFromEntity(bond.get());
        return ResponseEntity.ok(bondResource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBondById(@PathVariable Long id) {
        var command = new DeleteBondCommand(id);
        bondCommandService.handle(command);
        return ResponseEntity.ok().body("Bono con ID " + id + " eliminado correctamente.");
    }
}
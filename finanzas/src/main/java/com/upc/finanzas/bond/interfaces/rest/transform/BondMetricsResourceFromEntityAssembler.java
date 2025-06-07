package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.entities.BondMetrics;
import com.upc.finanzas.bond.interfaces.rest.resources.BondMetricsResource;

public class BondMetricsResourceFromEntityAssembler {
    public static BondMetricsResource toResourceFromEntity(BondMetrics entity) {
        return new BondMetricsResource(
                entity.getId(),
                entity.getBond().getId(),
                entity.getDuration(),
                entity.getConvexity(),
                entity.getTotalDurationConvexity(),
                entity.getModifiedDuration(),
                entity.getTcea(),
                entity.getTrea()
        );
    }
}

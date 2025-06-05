package com.upc.finanzas.bond.domain.services;

import com.upc.finanzas.bond.domain.model.commands.CreateBondCommand;
import com.upc.finanzas.bond.domain.model.commands.DeleteBondCommand;
import com.upc.finanzas.bond.domain.model.commands.UpdateBondCommand;

public interface BondCommandService {
    Long handle(CreateBondCommand command);
    Long handle(UpdateBondCommand command);
    void handle(DeleteBondCommand command);
}

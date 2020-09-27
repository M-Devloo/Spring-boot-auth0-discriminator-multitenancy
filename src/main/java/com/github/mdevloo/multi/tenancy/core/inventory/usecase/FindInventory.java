package com.github.mdevloo.multi.tenancy.core.inventory.usecase;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.InventoryRepository;
import com.github.mdevloo.multi.tenancy.fwk.ObjectNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class FindInventory {

  private final InventoryRepository inventoryRepository;

  public Inventory execute(final UUID id) {
    return this.inventoryRepository
        .findById(id)
        .orElseThrow(() -> new ObjectNotFoundException(Inventory.class, id));
  }
}

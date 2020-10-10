package com.github.mdevloo.multi.tenancy.core.inventory.usecase.inventory;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.InventoryRepository;
import com.github.mdevloo.multi.tenancy.fwk.ObjectNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@AllArgsConstructor
public class FindInventory {

  private final InventoryRepository inventoryRepository;

  @Transactional
  public Inventory execute(final UUID id) {
    return this.inventoryRepository
        .findById(id)
        .orElseThrow(() -> new ObjectNotFoundException(Inventory.class, id));
  }
}

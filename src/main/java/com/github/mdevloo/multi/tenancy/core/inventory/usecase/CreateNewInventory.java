package com.github.mdevloo.multi.tenancy.core.inventory.usecase;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.InventoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class CreateNewInventory {

  private final InventoryRepository inventoryRepository;

  @Transactional
  public Inventory execute(final InventoryCreationRequest request) {
    final Inventory inventory = new Inventory();
    inventory.setName(request.getName());
    inventory.setAmount(request.getInitialAmount());
    return this.inventoryRepository.saveAndFlush(inventory);
  }
}

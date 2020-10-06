package com.github.mdevloo.multi.tenancy.core.inventory.usecase.inventory;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.InventoryRepository;
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

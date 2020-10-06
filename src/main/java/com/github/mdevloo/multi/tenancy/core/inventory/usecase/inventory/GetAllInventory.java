package com.github.mdevloo.multi.tenancy.core.inventory.usecase.inventory;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.InventoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@AllArgsConstructor
public class GetAllInventory {

  private final InventoryRepository inventoryRepository;

  @Transactional
  public List<Inventory> execute() {
    return this.inventoryRepository.findAll();
  }
}

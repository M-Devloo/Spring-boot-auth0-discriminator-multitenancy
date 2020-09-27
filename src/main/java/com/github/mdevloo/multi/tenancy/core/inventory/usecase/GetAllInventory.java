package com.github.mdevloo.multi.tenancy.core.inventory.usecase;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.InventoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class GetAllInventory {

  private final InventoryRepository inventoryRepository;

  public List<Inventory> execute() {
    return this.inventoryRepository.findAll();
  }
}

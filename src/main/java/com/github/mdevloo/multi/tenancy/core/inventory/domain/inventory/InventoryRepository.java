package com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface InventoryRepository
    extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Inventory> {

    void deleteByName(final String name);
}

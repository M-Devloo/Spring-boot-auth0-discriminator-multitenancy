package com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {}

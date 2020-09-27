package com.github.mdevloo.multi.tenancy.core.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

  @Query("select i from Inventory i where i.id = :id")
  @Override
  Optional<Inventory> findById(@Param("id") final UUID id);
}

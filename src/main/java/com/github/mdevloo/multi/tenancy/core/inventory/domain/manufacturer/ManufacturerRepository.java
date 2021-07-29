package com.github.mdevloo.multi.tenancy.core.inventory.domain.manufacturer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, UUID> {}

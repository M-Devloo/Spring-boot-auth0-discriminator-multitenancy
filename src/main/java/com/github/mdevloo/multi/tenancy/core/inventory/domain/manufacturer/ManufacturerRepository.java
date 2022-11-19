package com.github.mdevloo.multi.tenancy.core.inventory.domain.manufacturer;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, UUID> {}

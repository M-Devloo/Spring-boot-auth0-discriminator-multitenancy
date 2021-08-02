package com.github.mdevloo.multi.tenancy.core.inventory.domain.manufacturer;

import com.github.mdevloo.multi.tenancy.fwk.multitenancy.NoMultiTenancyRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@NoMultiTenancyRepository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, UUID> {}

package com.github.mdevloo.multi.tenancy.core.inventory.domain.manufacturer;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface ManufacturerRepository extends CrudRepository<Manufacturer, UUID> {}

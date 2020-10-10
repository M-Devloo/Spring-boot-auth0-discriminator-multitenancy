package com.github.mdevloo.multi.tenancy.core.inventory.domain.vendor;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface VendorRepository extends CrudRepository<Vendor, UUID> {}

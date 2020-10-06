package com.github.mdevloo.multi.tenancy.core.inventory.domain.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {}

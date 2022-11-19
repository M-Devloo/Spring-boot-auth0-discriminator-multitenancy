package com.github.mdevloo.multi.tenancy.core.inventory.domain.product;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidProductRepository extends JpaRepository<InvalidProduct, UUID> {}

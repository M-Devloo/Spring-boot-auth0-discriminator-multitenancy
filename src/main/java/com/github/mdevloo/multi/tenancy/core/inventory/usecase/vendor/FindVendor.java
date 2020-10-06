package com.github.mdevloo.multi.tenancy.core.inventory.usecase.vendor;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.vendor.Vendor;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.vendor.VendorRepository;
import com.github.mdevloo.multi.tenancy.fwk.ObjectNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@AllArgsConstructor
public class FindVendor {

  private final VendorRepository vendorRepository;

  @Transactional
  public Vendor execute(final UUID id) {
    return this.vendorRepository
        .findById(id)
        .orElseThrow(() -> new ObjectNotFoundException(Vendor.class, id));
  }
}

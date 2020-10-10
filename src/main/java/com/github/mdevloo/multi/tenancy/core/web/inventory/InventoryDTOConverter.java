package com.github.mdevloo.multi.tenancy.core.web.inventory;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
class InventoryDTOConverter implements Converter<Inventory, InventoryDTO> {

  @Override
  public InventoryDTO convert(final Inventory source) {
    return InventoryDTO.of(source.getId(), source.getName(), source.getAmount());
  }
}

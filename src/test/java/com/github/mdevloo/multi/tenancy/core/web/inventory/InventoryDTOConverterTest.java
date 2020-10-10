package com.github.mdevloo.multi.tenancy.core.web.inventory;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class InventoryDTOConverterTest {

  @Test
  void convertInventoryToDTO() {
    final Inventory inventory = new Inventory();
    inventory.setName("PS4");
    inventory.setAmount(5);
    inventory.setId(UUID.randomUUID());

    final InventoryDTOConverter dtoConverter = new InventoryDTOConverter();
    final InventoryDTO convert = dtoConverter.convert(inventory);
    Assertions.assertThat(convert).isNotNull();
    Assertions.assertThat(convert.getId()).isEqualTo(inventory.getId());
    Assertions.assertThat(convert.getAmount()).isEqualTo(inventory.getAmount());
    Assertions.assertThat(convert.getName()).isEqualTo(inventory.getName());
  }
}

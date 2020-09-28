package com.github.mdevloo.multi.tenancy.core.web.inventory;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor(staticName = "of")
class InventoryDTO {

  UUID id;
  String name;
  int amount;
}

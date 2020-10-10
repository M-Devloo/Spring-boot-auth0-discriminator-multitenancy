package com.github.mdevloo.multi.tenancy.core.inventory.usecase.inventory;

import lombok.Getter;
import lombok.Value;

@Value
@Getter
public class InventoryCreationRequest {

  String name;
  int initialAmount;
}

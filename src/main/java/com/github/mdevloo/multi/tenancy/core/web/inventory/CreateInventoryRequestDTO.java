package com.github.mdevloo.multi.tenancy.core.web.inventory;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
class CreateInventoryRequestDTO {

  @NotBlank private final String name;

  @Min(0)
  private final int amount;
}

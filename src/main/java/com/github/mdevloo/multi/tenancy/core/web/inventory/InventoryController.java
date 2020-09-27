package com.github.mdevloo.multi.tenancy.core.web.inventory;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.usecase.CreateNewInventory;
import com.github.mdevloo.multi.tenancy.core.inventory.usecase.InventoryCreationRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import static com.github.mdevloo.multi.tenancy.core.AppConstants.INVENTORY_URL;

@Slf4j
@RestController
@Validated
@RequestMapping(INVENTORY_URL)
@AllArgsConstructor
public class InventoryController {

  private final CreateNewInventory createNewInventory;
  private final Converter<Inventory, InventoryDTO> inventoryConverter;

  @GetMapping
  public InventoryDTO createInventory(
      @RequestParam @NotBlank final String name, @RequestParam @Min(0) final int amount) {
    final InventoryCreationRequest request = new InventoryCreationRequest(name, amount);
    return this.inventoryConverter.convert(this.createNewInventory.execute(request));
  }
}

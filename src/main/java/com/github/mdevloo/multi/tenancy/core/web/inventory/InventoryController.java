package com.github.mdevloo.multi.tenancy.core.web.inventory;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.usecase.CreateNewInventory;
import com.github.mdevloo.multi.tenancy.core.inventory.usecase.FindInventory;
import com.github.mdevloo.multi.tenancy.core.inventory.usecase.GetAllInventory;
import com.github.mdevloo.multi.tenancy.core.inventory.usecase.InventoryCreationRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.mdevloo.multi.tenancy.core.AppConstants.INVENTORY_URL;

@Slf4j
@RestController
@Validated
@RequestMapping(INVENTORY_URL)
@AllArgsConstructor
public class InventoryController {

  private final CreateNewInventory createNewInventory;
  private final FindInventory findInventory;
  private final GetAllInventory getAllInventory;
  private final Converter<Inventory, InventoryDTO> inventoryConverter;

  @GetMapping
  public InventoryDTO getInventory(@NotNull @RequestParam final UUID id) {
    return this.inventoryConverter.convert(this.findInventory.execute(id));
  }

  @GetMapping
  public List<InventoryDTO> getAllInventory() {
    return this.getAllInventory.execute().stream()
        .map(this.inventoryConverter::convert)
        .collect(Collectors.toUnmodifiableList());
  }

  @PostMapping
  public InventoryDTO createInventory(
      @RequestBody @NotNull final CreateInventoryRequestDTO requestDTO) {
    final InventoryCreationRequest request =
        new InventoryCreationRequest(requestDTO.getName(), requestDTO.getAmount());
    return this.inventoryConverter.convert(this.createNewInventory.execute(request));
  }
}

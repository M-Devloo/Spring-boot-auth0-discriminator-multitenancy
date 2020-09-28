package com.github.mdevloo.multi.tenancy.core.web.inventory;

import static com.github.mdevloo.multi.tenancy.core.AppConstants.INVENTORY_URL;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.usecase.CreateNewInventory;
import com.github.mdevloo.multi.tenancy.core.inventory.usecase.FindInventory;
import com.github.mdevloo.multi.tenancy.core.inventory.usecase.GetAllInventory;
import com.github.mdevloo.multi.tenancy.core.inventory.usecase.InventoryCreationRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
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

  @GetMapping(path = "/all")
  public List<InventoryDTO> getAllInventory() {
    return this.getAllInventory.execute().stream()
        .map(this.inventoryConverter::convert)
        .collect(Collectors.toUnmodifiableList());
  }

  @PostMapping(path = "/create")
  public InventoryDTO createInventory(
      @RequestBody @NotNull @Valid final CreateInventoryRequestDTO requestDTO) {
    final InventoryCreationRequest request =
        new InventoryCreationRequest(requestDTO.getName(), requestDTO.getAmount());
    return this.inventoryConverter.convert(this.createNewInventory.execute(request));
  }
}

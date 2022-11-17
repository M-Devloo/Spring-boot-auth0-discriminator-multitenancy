package com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

@SqlGroup({
  @Sql(
      scripts = "classpath:sql/inventory.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
  @Sql(
      scripts = "classpath:sql/cleanup.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
class InventoryRepositoryTest extends AbstractIntegrationTest {

  public static final String CURRENT_TENANT_INVENTORY_ID = "aa05d535-a53a-4a30-a8e6-9ede533d25c5";
  private final InventoryRepository inventoryRepository;

  @Autowired
  InventoryRepositoryTest(final InventoryRepository inventoryRepository) {
    this.inventoryRepository = inventoryRepository;
  }

  public static Specification<Inventory> inventoryId(final UUID id) {
    return (inventory, cq, cb) -> inventory.get(Inventory_.ID).in(id);
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  @Transactional
  @Test
  void deleteById() {
    final UUID otherTenantId = UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6");
    Assertions.assertThatExceptionOfType(EmptyResultDataAccessException.class)
        .isThrownBy(() -> this.inventoryRepository.deleteById(otherTenantId));
  }

  @Transactional
  @Test
  void deleteAllById() {
    Assertions.assertThat(this.inventoryRepository.count()).isEqualTo(2L);
    this.inventoryRepository.deleteAllById(
        List.of(UUID.fromString("aa05d535-a53a-4a30-a8e6-9ede533d25c5")));
    Assertions.assertThat(this.inventoryRepository.count()).isEqualTo(1L);

    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(this.inventoryRepository.count()).isEqualTo(2L);
    Assertions.assertThatExceptionOfType(EmptyResultDataAccessException.class)
        .isThrownBy(
            () ->
                this.inventoryRepository.deleteAllById(
                    List.of(UUID.fromString("ba05d535-a53a-4a30-a8e6-9ede533d25c5"))));

    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.existsById(
                UUID.fromString("ba05d535-a53a-4a30-a8e6-9ede533d25c5")))
        .isTrue();
  }

  @Transactional
  @Test
  void getReferenceById() {
    final UUID idOfOtherTenant = UUID.fromString("ca05d535-a53a-4a30-a8e6-9ede533d25c6");
    Assertions.assertThatExceptionOfType(JpaObjectRetrievalFailureException.class)
        .isThrownBy(() -> this.inventoryRepository.getReferenceById(idOfOtherTenant))
        .withMessageContaining("Lazy fetching is not supported as it breaks multi tenancy");

    final UUID idOfOwnTenant = UUID.fromString(CURRENT_TENANT_INVENTORY_ID);
    Assertions.assertThatExceptionOfType(JpaObjectRetrievalFailureException.class)
        .isThrownBy(() -> this.inventoryRepository.getReferenceById(idOfOwnTenant))
        .withMessageContaining("Lazy fetching is not supported as it breaks multi tenancy");
  }

  @Transactional
  @Test
  void deleteAllByIdInBatch() {
    Assertions.assertThat(this.inventoryRepository.count()).isEqualTo(2L);
    this.inventoryRepository.deleteAllByIdInBatch(
        List.of(UUID.fromString("aa05d535-a53a-4a30-a8e6-9ede533d25c5")));
    Assertions.assertThat(this.inventoryRepository.count()).isEqualTo(1L);

    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(this.inventoryRepository.count()).isEqualTo(2L);
    this.inventoryRepository.deleteAllByIdInBatch(
        List.of(UUID.fromString("ba05d535-a53a-4a30-a8e6-9ede533d25c5")));
    Assertions.assertThat(this.inventoryRepository.count()).isEqualTo(2L);

    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.existsById(
                UUID.fromString("ba05d535-a53a-4a30-a8e6-9ede533d25c5")))
        .isTrue();
  }

  @Transactional
  @Test
  void deleteNonManagedObject() {
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findById(
                UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")))
        .isPresent();

    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
    final Inventory inventory = new Inventory();
    inventory.setId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"));
    this.inventoryRepository.delete(inventory);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findById(
                UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")))
        .isPresent();
    this.inventoryRepository.delete(inventory);
    Assertions.assertThat(
            this.inventoryRepository.findById(
                UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")))
        .isNotPresent();
  }

  @Transactional
  @Test
  void deleteManagedObject() {
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final Inventory inventoryOfOtherTenant =
        this.inventoryRepository
            .findById(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"))
            .orElseThrow();

    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
    this.inventoryRepository.delete(inventoryOfOtherTenant);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findById(
                UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")))
        .isPresent();
    this.inventoryRepository.delete(inventoryOfOtherTenant);
    Assertions.assertThat(
            this.inventoryRepository.findById(
                UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")))
        .isNotPresent();
  }

  @Transactional
  @Test
  void deleteManagedObjectByName() {
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findById(
                UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")))
        .isPresent();

    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
    this.inventoryRepository.deleteByName("Sega Master System");

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findById(
                UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")))
        .isPresent();
    this.inventoryRepository.deleteByName("Sega Master System");
    Assertions.assertThat(
            this.inventoryRepository.findById(
                UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")))
        .isNotPresent();
  }

  @Transactional
  @Test
  void deleteAllWithObjectList() {
    this.delete(
        () -> {
          this.inventoryRepository.deleteAll(this.otherTenantsInventory());
          Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(2);
        });
  }

  @Transactional
  @Test
  void deleteInBatch() {
    this.delete(
        () -> {
          this.inventoryRepository.deleteInBatch(this.otherTenantsInventory());
          Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(2);
        });
  }

  @Transactional
  @Test
  void deleteAll() {
    this.delete(
        () -> {
          this.inventoryRepository.deleteAll();
          Assertions.assertThat(this.inventoryRepository.findAll()).isEmpty();
        });
  }

  @Transactional
  @Test
  void deleteAllInBatch() {
    this.delete(
        () -> {
          this.inventoryRepository.deleteAllInBatch();
          Assertions.assertThat(this.inventoryRepository.findAll()).isEmpty();
        });
  }

  @Transactional
  @Test
  void findByExampleAndQueryFunction() {
    final Optional<Inventory> currentTenant =
        this.inventoryRepository.findById(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
    Assertions.assertThat(currentTenant).isPresent();
    Assertions.assertThat(this.inventoryRepository.<Inventory, Long>findBy(Example.of(currentTenant.get()),
        FetchableFluentQuery::count)).isEqualTo(1L);
    Assertions.assertThat(this.inventoryRepository.<Inventory, Boolean>findBy(Example.of(currentTenant.get()),
        FetchableFluentQuery::exists)).isEqualTo(true);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(this.inventoryRepository.<Inventory, Long>findBy(Example.of(currentTenant.get()),
        FetchableFluentQuery::count)).isEqualTo(0L);
    Assertions.assertThat(this.inventoryRepository.<Inventory, Boolean>findBy(Example.of(currentTenant.get()),
        FetchableFluentQuery::exists)).isEqualTo(false);
  }

  @Transactional
  @Test
  void findById() {
    final Optional<Inventory> otherTenantInventory =
        this.inventoryRepository.findById(UUID.fromString("ca05d535-a53a-4a30-a8e6-9ede533d25c6"));
    Assertions.assertThat(otherTenantInventory).isEmpty();

    final Optional<Inventory> currentTenant =
        this.inventoryRepository.findById(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
    Assertions.assertThat(currentTenant).isPresent();
    Assertions.assertThat(currentTenant.get().getId())
        .isEqualTo(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
  }

  @Transactional
  @Test
  void getOne() {
    final UUID idOfOtherTenant = UUID.fromString("ca05d535-a53a-4a30-a8e6-9ede533d25c6");
    Assertions.assertThatExceptionOfType(JpaObjectRetrievalFailureException.class)
        .isThrownBy(() -> this.inventoryRepository.getOne(idOfOtherTenant));

    final UUID idOfOwnTenant = UUID.fromString(CURRENT_TENANT_INVENTORY_ID);
    Assertions.assertThatExceptionOfType(JpaObjectRetrievalFailureException.class)
        .isThrownBy(() -> this.inventoryRepository.getOne(idOfOwnTenant));
  }

  @Transactional
  @Test
  void existsById() {
    Assertions.assertThat(
            this.inventoryRepository.existsById(
                UUID.fromString("ca05d535-a53a-4a30-a8e6-9ede533d25c6")))
        .isFalse();
    Assertions.assertThat(
            this.inventoryRepository.existsById(UUID.fromString(CURRENT_TENANT_INVENTORY_ID)))
        .isTrue();
  }

  @Transactional
  @Test
  void findAll() {
    Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(2);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final List<Inventory> otherTenantSwitchEvents = this.inventoryRepository.findAll();
    Assertions.assertThat(otherTenantSwitchEvents).hasSize(1);
    Assertions.assertThat(otherTenantSwitchEvents.get(0).getName()).isEqualTo("Sega Master System");
  }

  @Transactional
  @Test
  void findAllById() {
    Assertions.assertThat(
            this.inventoryRepository.findAllById(
                List.of(UUID.fromString("ca05d535-a53a-4a30-a8e6-9ede533d25c6"))))
        .isEmpty();

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final List<Inventory> otherTenantSwitchEvents =
        this.inventoryRepository.findAllById(
            List.of(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")));
    Assertions.assertThat(otherTenantSwitchEvents).hasSize(1);
    Assertions.assertThat(otherTenantSwitchEvents.get(0).getName()).isEqualTo("Sega Master System");
  }

  @Transactional
  @Test
  void findAllWithSort() {
    Assertions.assertThat(this.inventoryRepository.findAll(Sort.unsorted())).hasSize(2);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final List<Inventory> otherTenantSwitchEvents =
        this.inventoryRepository.findAll(Sort.unsorted());
    Assertions.assertThat(otherTenantSwitchEvents).hasSize(1);
    Assertions.assertThat(otherTenantSwitchEvents.get(0).getName()).isEqualTo("Sega Master System");
  }

  @Transactional
  @Test
  void findAllWithPageable() {
    Assertions.assertThat(this.inventoryRepository.findAll(Pageable.unpaged()).getTotalElements())
        .isEqualTo(2);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final Page<Inventory> otherTenantSwitchEvents =
        this.inventoryRepository.findAll(Pageable.unpaged());

    Assertions.assertThat(otherTenantSwitchEvents.getTotalElements()).isEqualTo(1);
    Assertions.assertThat(otherTenantSwitchEvents.getContent().get(0).getName())
        .isEqualTo("Sega Master System");
  }

  @Transactional
  @Test
  void findOneWithExample() {
    Assertions.assertThat(
            this.inventoryRepository.findOne(Example.of(this.otherTenantsInventory().get(0))))
        .isNotPresent();
    Assertions.assertThat(this.inventoryRepository.findOne(Example.of(this.ownInventory().get(0))))
        .isPresent();
  }

  @Transactional
  @Test
  void count() {
    Assertions.assertThat(this.inventoryRepository.count()).isEqualTo(2);
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(this.inventoryRepository.count()).isOne();
  }

  @Transactional
  @Test
  void countWithExample() {
    Assertions.assertThat(
            this.inventoryRepository.count(Example.of(this.otherTenantsInventory().get(0))))
        .isZero();
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.count((Example.of(this.otherTenantsInventory().get(0)))))
        .isOne();
  }

  @Transactional
  @Test
  void existWithExample() {
    Assertions.assertThat(
            this.inventoryRepository.exists(Example.of(this.otherTenantsInventory().get(0))))
        .isFalse();
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.exists((Example.of(this.otherTenantsInventory().get(0)))))
        .isTrue();
  }

  @Transactional
  @Test
  void findAllWithExample() {
    Assertions.assertThat(
            this.inventoryRepository.findAll(Example.of(this.otherTenantsInventory().get(0))))
        .isEmpty();
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findAll(Example.of(this.otherTenantsInventory().get(0))))
        .hasSize(1);
  }

  @Transactional
  @Test
  void findAllWithExampleAndSort() {
    Assertions.assertThat(
            this.inventoryRepository.findAll(
                Example.of(this.otherTenantsInventory().get(0)), Sort.unsorted()))
        .isEmpty();
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findAll(
                Example.of(this.otherTenantsInventory().get(0)), Sort.unsorted()))
        .hasSize(1);
  }

  @Transactional
  @Test
  void findAllWithExampleAndPageable() {
    Assertions.assertThat(
            this.inventoryRepository.findAll(
                Example.of(this.otherTenantsInventory().get(0)), Pageable.unpaged()))
        .isEmpty();
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findAll(
                Example.of(this.otherTenantsInventory().get(0)), Pageable.unpaged()))
        .hasSize(1);
  }

  @Transactional
  @Test
  void save() {
    this.verifySingleInventory(this.inventoryRepository::save);
  }

  @Transactional
  @Test
  void saveAndFlush() {
    this.verifySingleInventory(this.inventoryRepository::saveAndFlush);
  }

  @Transactional
  @Test
  void saveAll() {
    this.verifyInventorys(this.inventoryRepository::saveAll);
  }

  @Transactional
  @Test
  void findOneWithSpecification() {
    Assertions.assertThat(
            this.inventoryRepository.findOne(
                inventoryId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"))))
        .isNotPresent();

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findOne(
                inventoryId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"))))
        .isPresent();
  }

  @Transactional
  @Test
  void findAllWithSpecification() {
    Assertions.assertThat(
            this.inventoryRepository.findAll(
                inventoryId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"))))
        .isEmpty();

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findAll(
                inventoryId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"))))
        .hasSize(1);
  }

  @Transactional
  @Test
  void findAllWithSpecificationAndSort() {
    Assertions.assertThat(
            this.inventoryRepository.findAll(
                inventoryId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")),
                Sort.unsorted()))
        .isEmpty();

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findAll(
                inventoryId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")),
                Sort.unsorted()))
        .hasSize(1);
  }

  @Transactional
  @Test
  void findAllWithSpecificationAndPageable() {
    Assertions.assertThat(
            this.inventoryRepository.findAll(
                inventoryId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")),
                Pageable.unpaged()))
        .isEmpty();

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.findAll(
                inventoryId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")),
                Pageable.unpaged()))
        .hasSize(1);
  }

  @Transactional
  @Test
  void countWithSpecification() {
    Assertions.assertThat(
            this.inventoryRepository.count(
                inventoryId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"))))
        .isZero();

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
            this.inventoryRepository.count(
                inventoryId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"))))
        .isOne();
  }

  private void verifySingleInventory(final Function<Inventory, Inventory> testFunction) {
    final Inventory inventory = new Inventory();
    inventory.setAmount(50);
    inventory.setVendor(null);
    inventory.setName("Diablo V Mint");
    inventory.setTenantId("random tenantId");

    final Inventory savedInventory = testFunction.apply(inventory);
    Assertions.assertThat(savedInventory.getAmount()).isEqualTo(50);
    Assertions.assertThat(savedInventory.getVendor()).isNull();
    Assertions.assertThat(savedInventory.getId()).isNotNull();
    Assertions.assertThat(savedInventory.getName()).isEqualTo("Diablo V Mint");
    Assertions.assertThat(savedInventory.getTenantId())
        .isEqualTo("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  private void verifyInventorys(final Function<List<Inventory>, List<Inventory>> testFunction) {
    final Inventory inventory = new Inventory();
    inventory.setAmount(50);
    inventory.setVendor(null);
    inventory.setName("Diablo IV Mint");
    inventory.setTenantId("random tenantId2");

    final List<Inventory> savedInventorys = testFunction.apply(List.of(inventory));
    final Inventory firstInventory = savedInventorys.get(0);
    Assertions.assertThat(firstInventory.getAmount()).isEqualTo(50);
    Assertions.assertThat(firstInventory.getVendor()).isNull();
    Assertions.assertThat(firstInventory.getId()).isNotNull();
    Assertions.assertThat(firstInventory.getName()).isEqualTo("Diablo IV Mint");
    Assertions.assertThat(firstInventory.getTenantId())
        .isEqualTo("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  /** Object of tenant: auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e */
  private List<Inventory> otherTenantsInventory() {
    final Inventory otherTenantInventory = new Inventory();
    otherTenantInventory.setId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"));
    return List.of(otherTenantInventory);
  }

  /** Object of tenant: auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e */
  private List<Inventory> ownInventory() {
    final Inventory ownInventory = new Inventory();
    ownInventory.setId(UUID.fromString("aa05d535-a53a-4a30-a8e6-9ede533d25c5"));
    return List.of(ownInventory);
  }

  private void delete(final Runnable runnable) {
    Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(2);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(1);

    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
    runnable.run();

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final List<Inventory> inventoryOfOtherTenant = this.inventoryRepository.findAll();
    Assertions.assertThat(inventoryOfOtherTenant).hasSize(1);
    Assertions.assertThat(inventoryOfOtherTenant.get(0).getId())
        .isEqualTo(this.otherTenantsInventory().get(0).getId());
  }
}

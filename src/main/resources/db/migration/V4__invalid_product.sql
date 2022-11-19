CREATE TABLE core.invalid_product
(
    id          uuid              not null
        constraint pk_invalid_product primary key,
    description character varying not null,
    tenant_id character varying not null
);

ALTER TABLE core.inventory
    ADD COLUMN manufacturer_id uuid,
    ADD CONSTRAINT fk_inventory_manufacturer FOREIGN KEY (manufacturer_id) REFERENCES core.manufacturer (id);

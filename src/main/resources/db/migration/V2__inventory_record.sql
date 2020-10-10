CREATE TABLE core.vendor
(
    vendor_id uuid              not null
        constraint pk_vendor primary key,
    tenant_id character varying not null
);

ALTER TABLE core.inventory
    ADD COLUMN vendor_id uuid,
    ADD CONSTRAINT fk_inventory_vendor FOREIGN KEY (vendor_id) REFERENCES core.vendor (vendor_id);

CREATE TABLE core.customer
(
    id   uuid              not null
        constraint pk_customer primary key,
    name character varying not null
);

ALTER TABLE core.inventory
    ADD COLUMN customer_id uuid,
    ADD CONSTRAINT fk_inventory_customer FOREIGN KEY (customer_id) REFERENCES core.customer (id);

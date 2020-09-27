CREATE SCHEMA core;

CREATE TABLE core.inventory
(
    id uuid not null constraint pk_inventory primary key,
    name character varying not null,
    amount int not null,
    tenant_id character varying not null
);

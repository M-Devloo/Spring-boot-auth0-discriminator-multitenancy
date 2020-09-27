CREATE SCHEMA core;

CREATE TABLE core.inventory
(
    id uuid not null constraint pk_inventory primary key,
    name character varying not null,
    amount numeric(19,2) not null,
    tenant_id character varying not null
);

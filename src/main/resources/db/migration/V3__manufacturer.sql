CREATE TABLE core.manufacturer
(
    id   uuid              not null
        constraint pk_manufacturer primary key,
    name character varying not null
);

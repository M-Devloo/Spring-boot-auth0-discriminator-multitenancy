SET session_replication_role = 'replica';

DELETE FROM core.vendor;
DELETE FROM core.inventory;
DELETE FROM core.manufacturer;
DELETE FROM core.invalid_product;
DELETE FROM core.customer;

SET session_replication_role = 'origin'

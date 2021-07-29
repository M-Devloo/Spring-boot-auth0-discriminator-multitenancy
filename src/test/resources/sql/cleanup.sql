SET session_replication_role = 'replica';

DELETE FROM core.vendor;
DELETE FROM core.inventory;
DELETE FROM core.manufacturer;

SET session_replication_role = 'origin'

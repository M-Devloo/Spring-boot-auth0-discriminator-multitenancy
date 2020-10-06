SET session_replication_role = 'replica';

DELETE FROM core.vendor;
DELETE FROM core.inventory;

SET session_replication_role = 'origin'

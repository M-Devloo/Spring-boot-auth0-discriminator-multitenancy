INSERT INTO core.vendor (vendor_id, tenant_id)
VALUES ('850e192b-91ab-4439-8e12-9420a060bbc6', 'auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e'),
       ('850e192b-91ab-4439-8e12-9420a060bbc7', 'auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e'),
       ('850e192b-91ab-4439-8e12-9420a060bbc8', 'auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e');

INSERT INTO core.inventory (id, name, amount, vendor_id, tenant_id)
VALUES ('aaaad535-a53a-4a30-a8e6-9ede533d25c6', 'Atari 5200', 15,
        '850e192b-91ab-4439-8e12-9420a060bbc8','auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e');
INSERT INTO core.inventory (id, name, amount, vendor_id, tenant_id)
VALUES ('bbbbd535-a53a-4a30-a8e6-9ede533d25c6', 'Sega Master System X', 39,
        '850e192b-91ab-4439-8e12-9420a060bbc8','auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e');

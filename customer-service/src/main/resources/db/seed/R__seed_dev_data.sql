-- Datos de prueba, solo con el perfil dev. Flyway vuelve a ejecutar este archivo cuando cambia: borra y recarga.
-- Los ids de cliente coinciden con app_user.customer_id (auth-service) y account.customer_id (account-service).
DELETE FROM favorite;
DELETE FROM customer;

INSERT INTO customer (id, document_type, document_number, first_names, last_names, email, phone, status, registered_at) VALUES
(1, 'DNI', '10000001', 'Ana', 'Torres', 'ana.torres@mail.com', '999111222', 'ACTIVE', '2026-01-10 09:00:00'),
(2, 'DNI', '10000002', 'Luis', 'Ramirez', 'luis.ramirez@mail.com', '999333444', 'ACTIVE', '2026-01-15 10:30:00'),
(3, 'DNI', '10000003', 'Maria', 'Lopez', 'maria.lopez@mail.com', '999555666', 'ACTIVE', '2026-02-01 11:00:00'),
(4, 'DNI', '10000004', 'Pedro', 'Diaz', 'pedro.diaz@mail.com', NULL, 'BLOCKED', '2026-02-20 16:45:00');

INSERT INTO favorite (id, alias, account_number, bank, holder, customer_id) VALUES
(1, 'Luis', '00219100000000000003', 'BCP', 'Luis Ramirez', 1),
(2, 'Maria', '00219100000000000004', 'BCP', 'Maria Lopez', 1),
(3, 'Ana', '00219100000000000001', 'BCP', 'Ana Torres', 2);

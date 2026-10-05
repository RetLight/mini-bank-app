-- Datos de prueba para minibank. Borra los datos actuales y vuelve a cargarlos.
-- Usuarios: demo/demo123, admin/admin123, maria/maria123, pedro/pedro123 (bloqueado)

DELETE FROM movement;
DELETE FROM transfer;
DELETE FROM favorite;
DELETE FROM account;
DELETE FROM app_user;
DELETE FROM customer;

ALTER TABLE movement AUTO_INCREMENT = 1;
ALTER TABLE transfer AUTO_INCREMENT = 1;
ALTER TABLE favorite AUTO_INCREMENT = 1;
ALTER TABLE account AUTO_INCREMENT = 1;
ALTER TABLE app_user AUTO_INCREMENT = 1;
ALTER TABLE customer AUTO_INCREMENT = 1;

INSERT INTO customer (id, document_type, document_number, first_names, last_names, email, phone, status, registered_at) VALUES
(1, 'DNI', '10000001', 'Ana', 'Torres', 'ana.torres@mail.com', '999111222', 'ACTIVE', '2026-01-10 09:00:00'),
(2, 'DNI', '10000002', 'Luis', 'Ramirez', 'luis.ramirez@mail.com', '999333444', 'ACTIVE', '2026-01-15 10:30:00'),
(3, 'DNI', '10000003', 'Maria', 'Lopez', 'maria.lopez@mail.com', '999555666', 'ACTIVE', '2026-02-01 11:00:00'),
(4, 'DNI', '10000004', 'Pedro', 'Diaz', 'pedro.diaz@mail.com', NULL, 'BLOCKED', '2026-02-20 16:45:00');

INSERT INTO app_user (id, username, password_hash, role, failed_attempts, blocked, last_login, customer_id) VALUES
(1, 'demo', '$2a$10$52YSTemOeiHF/xQ.fx5b4ufhpEdu9HGvHhXUVopUVgRiNOvtql/n.', 'CUSTOMER', 0, FALSE, NULL, 1),
(2, 'admin', '$2a$10$35czU6jtmAHtlUMTiE03N.Aij2/paCl/3iYuGh9xzX1Gj.esgUn8C', 'ADMIN', 0, FALSE, NULL, 2),
(3, 'maria', '$2a$10$NWAlcsheLdx2YumyjJRFs.b0gCBJ1vrx/oOPV8axzTOLg6mV/yoiO', 'CUSTOMER', 0, FALSE, NULL, 3),
(4, 'pedro', '$2a$10$rsm5T3azMHIpDbO7Qbp2F.YMu1nCiDXb/cHtSAJcEoK2TRZMJX2YW', 'CUSTOMER', 3, TRUE, NULL, 4);

INSERT INTO account (id, account_number, cci, type, currency, balance, status, opened_at, customer_id) VALUES
(1, '1910000000001', '00219100000000000001', 'SAVINGS', 'PEN', 900.00, 'ACTIVE', '2026-01-10 09:05:00', 1),
(2, '1910000000002', '00219100000000000002', 'SAVINGS', 'USD', 500.00, 'ACTIVE', '2026-01-10 09:10:00', 1),
(3, '1910000000003', '00219100000000000003', 'CHECKING', 'PEN', 2100.00, 'ACTIVE', '2026-01-15 10:35:00', 2),
(4, '1910000000004', '00219100000000000004', 'SAVINGS', 'PEN', 300.00, 'ACTIVE', '2026-02-01 11:05:00', 3),
(5, '1910000000005', '00219100000000000005', 'SAVINGS', 'PEN', 100.00, 'BLOCKED', '2026-02-20 16:50:00', 4);

INSERT INTO favorite (id, alias, account_number, bank, holder, customer_id) VALUES
(1, 'Luis', '00219100000000000003', 'BCP', 'Luis Ramirez', 1),
(2, 'Maria', '00219100000000000004', 'BCP', 'Maria Lopez', 1),
(3, 'Ana', '00219100000000000001', 'BCP', 'Ana Torres', 2);

INSERT INTO transfer (id, amount, currency, status, rejection_reason, requested_at, processed_at, type, destination_cci, destination_bank, destination_holder, source_account_id, destination_account_id) VALUES
(1, 100.00, 'PEN', 'COMPLETED', NULL, '2026-03-01 12:00:00', '2026-03-01 12:00:00', 'INTERNAL', '00219100000000000003', 'BCP', 'Luis Ramirez', 1, 3);

INSERT INTO movement (id, type, amount, resulting_balance, occurred_at, account_id, transfer_id) VALUES
(1, 'DECREASE', 100.00, 900.00, '2026-03-01 12:00:00', 1, 1),
(2, 'INCREASE', 100.00, 2100.00, '2026-03-01 12:00:00', 3, 1);

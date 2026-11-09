-- Datos de prueba, solo con el perfil dev. Flyway vuelve a ejecutar este archivo cuando cambia: borra y recarga.
-- account.customer_id coincide con los clientes que carga customer-service.
DELETE FROM movement;
DELETE FROM transfer;
DELETE FROM account;

INSERT INTO account (id, account_number, cci, type, currency, balance, status, opened_at, customer_id) VALUES
(1, '1910000000001', '00219100000000000001', 'SAVINGS', 'PEN', 900.00, 'ACTIVE', '2026-01-10 09:05:00', 1),
(2, '1910000000002', '00219100000000000002', 'SAVINGS', 'USD', 500.00, 'ACTIVE', '2026-01-10 09:10:00', 1),
(3, '1910000000003', '00219100000000000003', 'CHECKING', 'PEN', 2100.00, 'ACTIVE', '2026-01-15 10:35:00', 2),
(4, '1910000000004', '00219100000000000004', 'SAVINGS', 'PEN', 300.00, 'ACTIVE', '2026-02-01 11:05:00', 3),
(5, '1910000000005', '00219100000000000005', 'SAVINGS', 'PEN', 100.00, 'BLOCKED', '2026-02-20 16:50:00', 4);

INSERT INTO transfer (id, amount, currency, status, rejection_reason, requested_at, processed_at, type, destination_cci, destination_bank, destination_holder, source_account_id, destination_account_id) VALUES
(1, 100.00, 'PEN', 'COMPLETED', NULL, '2026-03-01 12:00:00', '2026-03-01 12:00:00', 'INTERNAL', '00219100000000000003', 'BCP', 'Luis Ramirez', 1, 3);

INSERT INTO movement (id, type, amount, resulting_balance, occurred_at, account_id, transfer_id) VALUES
(1, 'DECREASE', 100.00, 900.00, '2026-03-01 12:00:00', 1, 1),
(2, 'INCREASE', 100.00, 2100.00, '2026-03-01 12:00:00', 3, 1);

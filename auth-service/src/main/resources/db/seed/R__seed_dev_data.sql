-- Datos de prueba, solo con el perfil dev. Flyway vuelve a ejecutar este archivo cuando cambia: borra y recarga.
-- Usuarios: demo/demo123, admin/admin123, maria/maria123, pedro/pedro123 (bloqueado).
-- customer_id coincide con los clientes que carga customer-service.
DELETE FROM app_user;

INSERT INTO app_user (id, username, password_hash, role, failed_attempts, blocked, last_login, customer_id) VALUES
(1, 'demo', '$2a$10$52YSTemOeiHF/xQ.fx5b4ufhpEdu9HGvHhXUVopUVgRiNOvtql/n.', 'CUSTOMER', 0, FALSE, NULL, 1),
(2, 'admin', '$2a$10$35czU6jtmAHtlUMTiE03N.Aij2/paCl/3iYuGh9xzX1Gj.esgUn8C', 'ADMIN', 0, FALSE, NULL, 2),
(3, 'maria', '$2a$10$NWAlcsheLdx2YumyjJRFs.b0gCBJ1vrx/oOPV8axzTOLg6mV/yoiO', 'CUSTOMER', 0, FALSE, NULL, 3),
(4, 'pedro', '$2a$10$rsm5T3azMHIpDbO7Qbp2F.YMu1nCiDXb/cHtSAJcEoK2TRZMJX2YW', 'CUSTOMER', 3, TRUE, NULL, 4);

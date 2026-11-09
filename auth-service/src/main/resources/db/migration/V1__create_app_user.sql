-- customer_id es el id del cliente en customer-service, que vive en otra base de datos:
-- por eso no tiene clave foránea. Lo garantiza el alta del cliente, no una restricción.
CREATE TABLE app_user
(
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  failed_attempts INT NOT NULL,
  blocked BOOLEAN NOT NULL,
  last_login TIMESTAMP,
  customer_id INT NOT NULL,
  UNIQUE (customer_id),
  UNIQUE (username)
);

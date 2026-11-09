-- Cuentas, transferencias y movimientos. account.customer_id es el id del cliente en
-- customer-service, que vive en otra base de datos: por eso no tiene clave foránea.
-- La propiedad de cada cuenta se comprueba con el claim customerId del token.
CREATE TABLE account
(
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_number VARCHAR(20) NOT NULL,
  cci VARCHAR(20) NOT NULL,
  type VARCHAR(20) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  balance DECIMAL(15,2) NOT NULL,
  status VARCHAR(20) NOT NULL,
  opened_at TIMESTAMP NOT NULL,
  customer_id INT NOT NULL,
  UNIQUE (account_number),
  UNIQUE (cci)
);

CREATE INDEX idx_account_customer_id ON account (customer_id);

CREATE TABLE transfer
(
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  amount DECIMAL(15,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  status VARCHAR(20) NOT NULL,
  rejection_reason VARCHAR(255),
  requested_at TIMESTAMP NOT NULL,
  processed_at TIMESTAMP,
  type VARCHAR(20) NOT NULL,
  destination_cci VARCHAR(20) NOT NULL,
  destination_bank VARCHAR(50) NOT NULL,
  destination_holder VARCHAR(100) NOT NULL,
  source_account_id INT NOT NULL,
  destination_account_id INT,
  FOREIGN KEY (source_account_id) REFERENCES account(id),
  FOREIGN KEY (destination_account_id) REFERENCES account(id)
);

CREATE TABLE movement
(
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  type VARCHAR(20) NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  resulting_balance DECIMAL(15,2) NOT NULL,
  occurred_at TIMESTAMP NOT NULL,
  account_id INT NOT NULL,
  transfer_id INT,
  FOREIGN KEY (account_id) REFERENCES account(id),
  FOREIGN KEY (transfer_id) REFERENCES transfer(id)
);

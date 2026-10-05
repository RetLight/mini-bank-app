CREATE TABLE customer
(
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  document_type VARCHAR(10) NOT NULL,
  document_number VARCHAR(20) NOT NULL,
  first_names VARCHAR(100) NOT NULL,
  last_names VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  phone VARCHAR(20),
  status VARCHAR(20) NOT NULL,
  registered_at TIMESTAMP NOT NULL,
  UNIQUE (document_number),
  UNIQUE (email)
);

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
  FOREIGN KEY (customer_id) REFERENCES customer(id),
  UNIQUE (customer_id),
  UNIQUE (username)
);

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
  FOREIGN KEY (customer_id) REFERENCES customer(id),
  UNIQUE (account_number),
  UNIQUE (cci)
);

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

CREATE TABLE favorite
(
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  alias VARCHAR(50) NOT NULL,
  account_number VARCHAR(20) NOT NULL,
  bank VARCHAR(50) NOT NULL,
  holder VARCHAR(100) NOT NULL,
  customer_id INT NOT NULL,
  FOREIGN KEY (customer_id) REFERENCES customer(id),
  UNIQUE (customer_id, account_number, bank),
  UNIQUE (customer_id, alias)
);

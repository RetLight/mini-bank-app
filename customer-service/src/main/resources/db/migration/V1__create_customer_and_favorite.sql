-- Clientes y sus beneficiarios favoritos. El usuario con el que cada cliente entra a la app
-- vive en auth-service (app_user.customer_id), en otra base de datos.
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

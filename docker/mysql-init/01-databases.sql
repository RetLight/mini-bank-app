-- Se ejecuta una sola vez, cuando el volumen de MySQL está vacío. Un servidor, una base por servicio:
-- ningún servicio lee las tablas de otro. Las tablas las crea Flyway al arrancar cada servicio.
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS customer_db;
CREATE DATABASE IF NOT EXISTS account_db;

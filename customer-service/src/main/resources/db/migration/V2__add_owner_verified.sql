-- false = el favorito se guardó mientras auth-service no podía confirmar que el usuario seguía activo.
-- Los existentes se dan por verificados.
ALTER TABLE favorite ADD COLUMN owner_verified BOOLEAN NOT NULL DEFAULT TRUE;

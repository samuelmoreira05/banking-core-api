ALTER TABLE clientes
ADD COLUMN usuario_id BIGINT;

ALTER TABLE clientes
ADD CONSTRAINT fk_clientes_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
ADD CONSTRAINT uk_clientes_usuario_id UNIQUE (usuario_id);
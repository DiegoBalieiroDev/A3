ALTER TABLE usuarios
    ADD COLUMN cliente_id BIGINT NULL,
ADD CONSTRAINT fk_usuario_cliente
    FOREIGN KEY (cliente_id) REFERENCES clientes(id);

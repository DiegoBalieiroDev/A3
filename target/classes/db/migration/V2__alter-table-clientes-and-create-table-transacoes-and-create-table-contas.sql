-- Adiciona coluna chave_pix em clientes
ALTER TABLE clientes
    ADD COLUMN chave_pix VARCHAR(100);

-- Cria tabela de contas
CREATE TABLE contas (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        cliente_id BIGINT NOT NULL UNIQUE,
                        numero_conta VARCHAR(20) NOT NULL UNIQUE,
                        agencia VARCHAR(10) NOT NULL,
                        saldo DECIMAL(19,2) NOT NULL DEFAULT 0.00,
                        PRIMARY KEY (id),
                        CONSTRAINT fk_cliente_conta FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- Cria tabela de transações
CREATE TABLE transacoes (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            valor DECIMAL(19,2) NOT NULL,
                            chave_pix VARCHAR(100),
                            data_transacao DATETIME NOT NULL,
                            status VARCHAR(20) NOT NULL,
                            suspeita_golpe BOOLEAN NOT NULL,
                            cliente_origem_id BIGINT NOT NULL,
                            nome_destinatario VARCHAR(100) NOT NULL,
                            chave_pix_destino VARCHAR(100) NOT NULL,
                            cpf_destinatario VARCHAR(11) NOT NULL,
                            PRIMARY KEY (id),
                            CONSTRAINT fk_cliente_transacao FOREIGN KEY (cliente_origem_id) REFERENCES clientes(id)
);
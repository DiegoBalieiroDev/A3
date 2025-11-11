ALTER TABLE transacoes
    ADD COLUMN fraud_score INT NULL,
    ADD COLUMN fraud_reasons TEXT NULL;

ALTER TABLE contas
    ADD COLUMN criado_em TIMESTAMP NULL;
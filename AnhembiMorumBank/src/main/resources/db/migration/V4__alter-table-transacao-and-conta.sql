-- V4 corrigida: adiciona colunas de fraude e criado_em de forma idempotente
ALTER TABLE transacoes
    ADD COLUMN IF NOT EXISTS fraud_score INT NULL,
    ADD COLUMN IF NOT EXISTS fraud_reasons TEXT NULL;

ALTER TABLE contas
    ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP NULL;
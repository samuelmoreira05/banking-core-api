CREATE TABLE faturas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    valor_total DECIMAL(19, 2) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_fechamento DATE,
    status VARCHAR(50) NOT NULL,
    cartao_id BIGINT NOT NULL,

    CONSTRAINT fk_faturas_cartao FOREIGN KEY (cartao_id) REFERENCES cartoes(id)
);

ALTER TABLE transacoes ADD COLUMN fatura_id BIGINT;

ALTER TABLE transacoes ADD CONSTRAINT fk_transacoes_fatura FOREIGN KEY (fatura_id) REFERENCES faturas(id);
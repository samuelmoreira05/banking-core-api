CREATE TABLE transacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_transacao ENUM('SAQUE', 'DEPOSITO') NOT NULL,
    data_transacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    conta_id BIGINT NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    saldo_anterior DECIMAL(15,2),
    descricao VARCHAR(500),
    CONSTRAINT fk_transacao_conta FOREIGN KEY (conta_id) REFERENCES contas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_transacoes_conta_id ON transacoes(conta_id);
CREATE INDEX idx_transacoes_data ON transacoes(data_transacao);
CREATE INDEX idx_transacoes_tipo ON transacoes(tipo_transacao);
CREATE TABLE cartoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cartao VARCHAR(16) NOT NULL UNIQUE,
    cvv VARCHAR(4) NOT NULL,
    data_vencimento DATE NOT NULL,
    tipo_cartao VARCHAR(100) NOT NULL,
    status VARCHAR(100) NOT NULL,
    limite_credito DECIMAL(10, 2),
    dia_vencimento_fatura INT,
    conta_id BIGINT NOT NULL,

    FOREIGN KEY (conta_id) REFERENCES contas(id)
) ENGINE=InnoDB;
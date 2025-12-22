package com.banco.api.banco.repository.jdbc;

import com.banco.api.banco.model.entity.Transacao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class TransacaoJdbcRepository {

    private final DataSource dataSource;

    public TransacaoJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveAll(List<Transacao> transacaos){
        String sql = """
                INSERT INTO transacoes (
                       tipo_transacao,
                       data_transacao,
                       conta_id,
                       valor,
                       saldo_anterior,
                       descricao,
                       fatura_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            for (Transacao transacao : transacaos){
                ps.setString(1, transacao.getTipo().toString());
                ps.setTimestamp(2, Timestamp.valueOf(transacao.getDataTransacao()));
                ps.setLong(3, transacao.getConta().getId());
                ps.setBigDecimal(4, transacao.getValor());
                ps.setBigDecimal(5, transacao.getSaldoAnterior());
                ps.setString(6, transacao.getDescricao());

                if (transacao.getFatura() != null){
                    ps.setLong(7, transacao.getFatura().getId());
                }else {
                    ps.setNull(7, Types.BIGINT);
                }

                ps.addBatch();

                connection.commit();
            }
        }catch (SQLException e){
            throw new RuntimeException("Erro ao salvar transacao via JDBC", e);
        }

    }
}

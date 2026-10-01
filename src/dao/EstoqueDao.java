package dao;

import java.sql.Connection;
import java.sql.SQLException;

/** Movimentação de estoque compartilhada por Venda (saída) e Compra (entrada). Roda na transação do chamador. */
final class EstoqueDao {

    private EstoqueDao() {
    }

    /** Soma (delta positivo) ou subtrai (delta negativo) do estoque e impede saldo negativo. */
    static void movimentar(Connection c, int proCodigo, double delta) throws SQLException {
        Sql.executar(c, "UPDATE PRODUTO SET PRO_ESTOQUE = COALESCE(PRO_ESTOQUE, 0) + ? WHERE PRO_CODIGO = ?", delta, proCodigo);
        String negativo = Sql.primeiro(c, "SELECT PRO_NOME FROM PRODUTO WHERE PRO_CODIGO = ? AND PRO_ESTOQUE < -0.0001",
                rs -> rs.getString(1), proCodigo);
        if (negativo != null) {
            throw new SQLException("Estoque insuficiente para o produto \"" + negativo + "\" (a operação deixaria o saldo negativo).");
        }
    }
}

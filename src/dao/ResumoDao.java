package dao;

import conexao.Conexao;
import java.sql.SQLException;
import model.ResumoModel;

/** Consultas agregadas da tela inicial. */
public class ResumoDao {

    public ResumoModel carregar() throws SQLException {
        return Sql.primeiro(Conexao.getConexao(), "SELECT"
                + " (SELECT COUNT(*) FROM CLIENTE) AS CLIENTES,"
                + " (SELECT COUNT(*) FROM FORNECEDOR) AS FORNECEDORES,"
                + " (SELECT COUNT(*) FROM PRODUTO) AS PRODUTOS,"
                + " (SELECT COUNT(*) FROM VENDA) AS VENDAS,"
                + " (SELECT COUNT(*) FROM PRODUTO WHERE PRO_ATIVO = 'S' AND PRO_MIN > 0 AND PRO_ESTOQUE < PRO_MIN) AS BAIXO,"
                + " (SELECT COALESCE(SUM(VDA_TOTAL), 0) FROM VENDA) AS VENDIDO,"
                + " (SELECT COALESCE(SUM(CPR_TOTAL), 0) FROM COMPRA) AS COMPRADO", rs -> {
                    ResumoModel r = new ResumoModel();
                    r.setClientes(rs.getInt("CLIENTES"));
                    r.setFornecedores(rs.getInt("FORNECEDORES"));
                    r.setProdutos(rs.getInt("PRODUTOS"));
                    r.setVendas(rs.getInt("VENDAS"));
                    r.setEstoqueBaixo(rs.getInt("BAIXO"));
                    r.setTotalVendido(rs.getDouble("VENDIDO"));
                    r.setTotalComprado(rs.getDouble("COMPRADO"));
                    return r;
                });
    }
}

package dao;

import conexao.Conexao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.CompraModel;
import model.CompraProdutoModel;

/** Compra + itens (COMPRA_PRODUTO), na mesma transação. A compra aumenta o estoque. */
public class CompraDao implements Dao<CompraModel> {

    private static final String SELECT = "SELECT C.*, P.PES_NOME AS FOR_NOME, U.USU_NOME AS USU_NOME FROM COMPRA C"
            + " JOIN FORNECEDOR F ON F.FOR_CODIGO = C.FOR_CODIGO JOIN PESSOA P ON P.PES_CODIGO = F.PES_CODIGO"
            + " JOIN USUARIO U ON U.USU_CODIGO = C.USU_CODIGO";

    private static final Sql.Mapeador<CompraModel> MAPA = rs -> {
        CompraModel m = new CompraModel();
        m.setCodigo(rs.getInt("CPR_CODIGO"));
        m.setUsuCodigo(rs.getInt("USU_CODIGO"));
        m.setUsuarioNome(rs.getString("USU_NOME"));
        m.setForCodigo(rs.getInt("FOR_CODIGO"));
        m.setFornecedorNome(rs.getString("FOR_NOME"));
        m.setEmissao(Sql.data(rs, "CPR_EMISSAO"));
        m.setValor(rs.getDouble("CPR_VALOR"));
        m.setDesconto(rs.getDouble("CPR_DESCONTO"));
        m.setTotal(rs.getDouble("CPR_TOTAL"));
        m.setDtEntrada(Sql.data(rs, "CPR_DTENTRADA"));
        m.setObs(rs.getString("CPR_OBS"));
        return m;
    };

    private static final Sql.Mapeador<CompraProdutoModel> MAPA_ITEM = rs -> {
        CompraProdutoModel i = new CompraProdutoModel();
        i.setCodigo(rs.getInt("CPP_CODIGO"));
        i.setCprCodigo(rs.getInt("CPR_CODIGO"));
        i.setProCodigo(rs.getInt("PRO_CODIGO"));
        i.setProNome(rs.getString("PRO_NOME"));
        i.setQtde(rs.getDouble("CPR_QTDE"));
        i.setPreco(rs.getDouble("CPR_PRECO"));
        i.setDesconto(rs.getDouble("CPR_DESCONTO"));
        i.setTotal(rs.getDouble("CPR_TOTAL"));
        return i;
    };

    @Override
    public void adicionar(CompraModel m) throws SQLException {
        try {
            Conexao.transacao(c -> {
                m.setCodigo(Sql.inserir(c, "INSERT INTO COMPRA (USU_CODIGO, FOR_CODIGO, CPR_EMISSAO, CPR_VALOR, CPR_DESCONTO, CPR_TOTAL,"
                        + " CPR_DTENTRADA, CPR_OBS) VALUES (?,?,?,?,?,?,?,?)", m.getUsuCodigo(), m.getForCodigo(), m.getEmissao(),
                        m.getValor(), m.getDesconto(), m.getTotal(), m.getDtEntrada(), m.getObs()));
                gravarItens(c, m);
            });
        } catch (SQLException e) {
            m.setCodigo(0);
            throw e;
        }
    }

    @Override
    public void alterar(CompraModel m) throws SQLException {
        Conexao.transacao(c -> {
            desfazerItens(c, m.getCodigo());
            Sql.executar(c, "UPDATE COMPRA SET FOR_CODIGO=?, CPR_EMISSAO=?, CPR_VALOR=?, CPR_DESCONTO=?, CPR_TOTAL=?,"
                    + " CPR_DTENTRADA=?, CPR_OBS=? WHERE CPR_CODIGO=?", m.getForCodigo(), m.getEmissao(), m.getValor(),
                    m.getDesconto(), m.getTotal(), m.getDtEntrada(), m.getObs(), m.getCodigo());
            gravarItens(c, m);
        });
    }

    @Override
    public void excluir(int codigo) throws SQLException {
        Conexao.transacao(c -> {
            desfazerItens(c, codigo);
            Sql.executar(c, "DELETE FROM COMPRA WHERE CPR_CODIGO = ?", codigo);
        });
    }

    @Override
    public CompraModel buscar(int codigo) throws SQLException {
        Connection c = Conexao.getConexao();
        CompraModel m = Sql.primeiro(c, SELECT + " WHERE C.CPR_CODIGO = ?", MAPA, codigo);
        if (m != null) {
            m.setItens(Sql.consultar(c, "SELECT I.*, P.PRO_NOME FROM COMPRA_PRODUTO I JOIN PRODUTO P ON P.PRO_CODIGO = I.PRO_CODIGO"
                    + " WHERE I.CPR_CODIGO = ? ORDER BY I.CPP_CODIGO", MAPA_ITEM, codigo));
        }
        return m;
    }

    @Override
    public List<CompraModel> consultar(String filtro) throws SQLException {
        int numero = filtro.matches("\\d{1,9}") ? Integer.parseInt(filtro) : -1;
        return Sql.consultar(Conexao.getConexao(), SELECT + " WHERE P.PES_NOME LIKE ? OR C.CPR_CODIGO = ? ORDER BY C.CPR_CODIGO DESC",
                MAPA, Sql.like(filtro), numero);
    }

    private void gravarItens(Connection c, CompraModel m) throws SQLException {
        for (CompraProdutoModel i : m.getItens()) {
            i.setCprCodigo(m.getCodigo());
            i.setCodigo(Sql.inserir(c, "INSERT INTO COMPRA_PRODUTO (CPR_CODIGO, PRO_CODIGO, CPR_QTDE, CPR_PRECO, CPR_DESCONTO, CPR_TOTAL)"
                    + " VALUES (?,?,?,?,?,?)", m.getCodigo(), i.getProCodigo(), i.getQtde(), i.getPreco(), i.getDesconto(), i.getTotal()));
            EstoqueDao.movimentar(c, i.getProCodigo(), i.getQtde());
        }
    }

    private void desfazerItens(Connection c, int cprCodigo) throws SQLException {
        List<CompraProdutoModel> antigos = Sql.consultar(c, "SELECT I.*, '' AS PRO_NOME FROM COMPRA_PRODUTO I WHERE I.CPR_CODIGO = ?",
                MAPA_ITEM, cprCodigo);
        for (CompraProdutoModel i : antigos) {
            EstoqueDao.movimentar(c, i.getProCodigo(), -i.getQtde());
        }
        Sql.executar(c, "DELETE FROM COMPRA_PRODUTO WHERE CPR_CODIGO = ?", cprCodigo);
    }
}

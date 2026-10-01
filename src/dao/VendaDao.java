package dao;

import conexao.Conexao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.VendaModel;
import model.VendaPagtoModel;
import model.VendaProdutoModel;

/** Venda + itens (VENDA_PRODUTO) + pagamentos (VENDA_PAGTO), tudo na mesma transação. */
public class VendaDao implements Dao<VendaModel> {

    private static final String SELECT = "SELECT V.*, P.PES_NOME AS CLI_NOME, U.USU_NOME AS USU_NOME FROM VENDA V"
            + " JOIN CLIENTE C ON C.CLI_CODIGO = V.CLI_CODIGO JOIN PESSOA P ON P.PES_CODIGO = C.PES_CODIGO"
            + " JOIN USUARIO U ON U.USU_CODIGO = V.USU_CODIGO";

    private static final Sql.Mapeador<VendaModel> MAPA = rs -> {
        VendaModel v = new VendaModel();
        v.setCodigo(rs.getInt("VDA_CODIGO"));
        v.setCliCodigo(rs.getInt("CLI_CODIGO"));
        v.setClienteNome(rs.getString("CLI_NOME"));
        v.setUsuCodigo(rs.getInt("USU_CODIGO"));
        v.setUsuarioNome(rs.getString("USU_NOME"));
        v.setData(Sql.data(rs, "VDA_DATA"));
        v.setValor(rs.getDouble("VDA_VALOR"));
        v.setDesconto(rs.getDouble("VDA_DESCONTO"));
        v.setTotal(rs.getDouble("VDA_TOTAL"));
        v.setObs(rs.getString("VDA_OBS"));
        return v;
    };

    private static final Sql.Mapeador<VendaProdutoModel> MAPA_ITEM = rs -> {
        VendaProdutoModel i = new VendaProdutoModel();
        i.setCodigo(rs.getInt("VEP_CODIGO"));
        i.setVdaCodigo(rs.getInt("VDA_CODIGO"));
        i.setProCodigo(rs.getInt("PRO_CODIGO"));
        i.setProNome(rs.getString("PRO_NOME"));
        i.setQtde(rs.getDouble("VEP_QTDE"));
        i.setPreco(rs.getDouble("VEP_PRECO"));
        i.setDesconto(rs.getDouble("VEP_DESCONTO"));
        i.setTotal(rs.getDouble("VEP_TOTAL"));
        return i;
    };

    private static final Sql.Mapeador<VendaPagtoModel> MAPA_PAGTO = rs -> {
        VendaPagtoModel p = new VendaPagtoModel();
        p.setCodigo(rs.getInt("VDP_CODIGO"));
        p.setVdaCodigo(rs.getInt("VDA_CODIGO"));
        p.setFpgCodigo(rs.getInt("FPG_CODIGO"));
        p.setFpgNome(rs.getString("FPG_NOME"));
        p.setValor(rs.getDouble("VDP_VALOR"));
        return p;
    };

    @Override
    public void adicionar(VendaModel v) throws SQLException {
        try {
            Conexao.transacao(c -> {
                v.setCodigo(Sql.inserir(c, "INSERT INTO VENDA (USU_CODIGO, CLI_CODIGO, VDA_DATA, VDA_VALOR, VDA_DESCONTO, VDA_TOTAL, VDA_OBS)"
                        + " VALUES (?,?,?,?,?,?,?)", v.getUsuCodigo(), v.getCliCodigo(), v.getData(), v.getValor(),
                        v.getDesconto(), v.getTotal(), v.getObs()));
                gravarFilhos(c, v);
            });
        } catch (SQLException e) {
            v.setCodigo(0); // houve rollback: o código gerado não existe mais
            throw e;
        }
    }

    @Override
    public void alterar(VendaModel v) throws SQLException {
        Conexao.transacao(c -> {
            desfazerFilhos(c, v.getCodigo());
            Sql.executar(c, "UPDATE VENDA SET CLI_CODIGO=?, VDA_DATA=?, VDA_VALOR=?, VDA_DESCONTO=?, VDA_TOTAL=?, VDA_OBS=?"
                    + " WHERE VDA_CODIGO=?", v.getCliCodigo(), v.getData(), v.getValor(), v.getDesconto(), v.getTotal(),
                    v.getObs(), v.getCodigo());
            gravarFilhos(c, v);
        });
    }

    @Override
    public void excluir(int codigo) throws SQLException {
        Conexao.transacao(c -> {
            desfazerFilhos(c, codigo);
            Sql.executar(c, "DELETE FROM VENDA WHERE VDA_CODIGO = ?", codigo);
        });
    }

    @Override
    public VendaModel buscar(int codigo) throws SQLException {
        Connection c = Conexao.getConexao();
        VendaModel v = Sql.primeiro(c, SELECT + " WHERE V.VDA_CODIGO = ?", MAPA, codigo);
        if (v != null) {
            v.setItens(Sql.consultar(c, "SELECT I.*, P.PRO_NOME FROM VENDA_PRODUTO I JOIN PRODUTO P ON P.PRO_CODIGO = I.PRO_CODIGO"
                    + " WHERE I.VDA_CODIGO = ? ORDER BY I.VEP_CODIGO", MAPA_ITEM, codigo));
            v.setPagamentos(Sql.consultar(c, "SELECT G.*, F.FPG_NOME FROM VENDA_PAGTO G JOIN FORMAPAGTO F ON F.FPG_CODIGO = G.FPG_CODIGO"
                    + " WHERE G.VDA_CODIGO = ? ORDER BY G.VDP_CODIGO", MAPA_PAGTO, codigo));
        }
        return v;
    }

    /** Lista para a grade (sem itens/pagamentos). O filtro busca pelo nome do cliente ou pelo número da venda. */
    @Override
    public List<VendaModel> consultar(String filtro) throws SQLException {
        int numero = filtro.matches("\\d{1,9}") ? Integer.parseInt(filtro) : -1;
        return Sql.consultar(Conexao.getConexao(), SELECT + " WHERE P.PES_NOME LIKE ? OR V.VDA_CODIGO = ? ORDER BY V.VDA_CODIGO DESC",
                MAPA, Sql.like(filtro), numero);
    }

    // ---- auxiliares (rodam dentro da transação) ----
    private void gravarFilhos(Connection c, VendaModel v) throws SQLException {
        for (VendaProdutoModel i : v.getItens()) {
            i.setVdaCodigo(v.getCodigo());
            i.setCodigo(Sql.inserir(c, "INSERT INTO VENDA_PRODUTO (VDA_CODIGO, PRO_CODIGO, VEP_QTDE, VEP_PRECO, VEP_DESCONTO, VEP_TOTAL)"
                    + " VALUES (?,?,?,?,?,?)", v.getCodigo(), i.getProCodigo(), i.getQtde(), i.getPreco(), i.getDesconto(), i.getTotal()));
            EstoqueDao.movimentar(c, i.getProCodigo(), -i.getQtde());
        }
        for (VendaPagtoModel p : v.getPagamentos()) {
            p.setVdaCodigo(v.getCodigo());
            p.setCodigo(Sql.inserir(c, "INSERT INTO VENDA_PAGTO (VDA_CODIGO, FPG_CODIGO, VDP_VALOR) VALUES (?,?,?)",
                    v.getCodigo(), p.getFpgCodigo(), p.getValor()));
        }
    }

    /** Devolve ao estoque o que a venda tinha tirado e apaga itens e pagamentos antigos. */
    private void desfazerFilhos(Connection c, int vdaCodigo) throws SQLException {
        List<VendaProdutoModel> antigos = Sql.consultar(c, "SELECT I.*, '' AS PRO_NOME FROM VENDA_PRODUTO I WHERE I.VDA_CODIGO = ?",
                MAPA_ITEM, vdaCodigo);
        for (VendaProdutoModel i : antigos) {
            EstoqueDao.movimentar(c, i.getProCodigo(), i.getQtde());
        }
        Sql.executar(c, "DELETE FROM VENDA_PRODUTO WHERE VDA_CODIGO = ?", vdaCodigo);
        Sql.executar(c, "DELETE FROM VENDA_PAGTO WHERE VDA_CODIGO = ?", vdaCodigo);
    }
}

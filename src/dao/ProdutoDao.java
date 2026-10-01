package dao;

import conexao.Conexao;
import java.sql.SQLException;
import java.util.List;
import model.ProdutoModel;

public class ProdutoDao implements Dao<ProdutoModel> {

    private static final Sql.Mapeador<ProdutoModel> MAPA = rs -> {
        ProdutoModel p = new ProdutoModel();
        p.setCodigo(rs.getInt("PRO_CODIGO"));
        p.setNome(rs.getString("PRO_NOME"));
        p.setEstoque(rs.getDouble("PRO_ESTOQUE"));
        p.setUnidade(rs.getString("PRO_UNIDADE"));
        p.setPreco(rs.getDouble("PRO_PRECO"));
        p.setCusto(rs.getDouble("PRO_CUSTO"));
        p.setAtacado(rs.getDouble("PRO_ATACADO"));
        p.setMinimo(rs.getDouble("PRO_MIN"));
        p.setMaximo(rs.getDouble("PRO_MAX"));
        p.setEmbalagem(rs.getInt("PRO_EMBALAGEM"));
        p.setPeso(rs.getDouble("PRO_PESO"));
        p.setCadastro(Sql.data(rs, "PRO_CADASTRO"));
        p.setObs(rs.getString("PRO_OBS"));
        p.setAtivo(Sql.sim(rs, "PRO_ATIVO"));
        return p;
    };

    @Override
    public void adicionar(ProdutoModel p) throws SQLException {
        p.setCodigo(Sql.inserir(Conexao.getConexao(),
                "INSERT INTO PRODUTO (PRO_NOME, PRO_ESTOQUE, PRO_UNIDADE, PRO_PRECO, PRO_CUSTO, PRO_ATACADO, PRO_MIN, PRO_MAX,"
                + " PRO_EMBALAGEM, PRO_PESO, PRO_CADASTRO, PRO_OBS, PRO_ATIVO) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                p.getNome(), p.getEstoque(), p.getUnidade(), p.getPreco(), p.getCusto(), p.getAtacado(), p.getMinimo(),
                p.getMaximo(), p.getEmbalagem(), p.getPeso(), p.getCadastro(), p.getObs(), p.isAtivo()));
    }

    @Override
    public void alterar(ProdutoModel p) throws SQLException {
        Sql.executar(Conexao.getConexao(),
                "UPDATE PRODUTO SET PRO_NOME=?, PRO_ESTOQUE=?, PRO_UNIDADE=?, PRO_PRECO=?, PRO_CUSTO=?, PRO_ATACADO=?, PRO_MIN=?,"
                + " PRO_MAX=?, PRO_EMBALAGEM=?, PRO_PESO=?, PRO_OBS=?, PRO_ATIVO=? WHERE PRO_CODIGO=?",
                p.getNome(), p.getEstoque(), p.getUnidade(), p.getPreco(), p.getCusto(), p.getAtacado(), p.getMinimo(),
                p.getMaximo(), p.getEmbalagem(), p.getPeso(), p.getObs(), p.isAtivo(), p.getCodigo());
    }

    @Override
    public void excluir(int codigo) throws SQLException {
        Sql.executar(Conexao.getConexao(), "DELETE FROM PRODUTO WHERE PRO_CODIGO = ?", codigo);
    }

    @Override
    public ProdutoModel buscar(int codigo) throws SQLException {
        return Sql.primeiro(Conexao.getConexao(), "SELECT * FROM PRODUTO WHERE PRO_CODIGO = ?", MAPA, codigo);
    }

    @Override
    public List<ProdutoModel> consultar(String filtro) throws SQLException {
        return Sql.consultar(Conexao.getConexao(), "SELECT * FROM PRODUTO WHERE PRO_NOME LIKE ? ORDER BY PRO_NOME",
                MAPA, Sql.like(filtro));
    }
}

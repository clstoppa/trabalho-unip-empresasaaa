package dao;

import conexao.Conexao;
import java.sql.SQLException;
import java.util.List;
import model.FormaPagtoModel;

public class FormaPagtoDao implements Dao<FormaPagtoModel> {

    private static final Sql.Mapeador<FormaPagtoModel> MAPA = rs -> {
        FormaPagtoModel f = new FormaPagtoModel();
        f.setCodigo(rs.getInt("FPG_CODIGO"));
        f.setNome(rs.getString("FPG_NOME"));
        f.setAtivo(Sql.sim(rs, "FPG_ATIVO"));
        return f;
    };

    @Override
    public void adicionar(FormaPagtoModel f) throws SQLException {
        f.setCodigo(Sql.inserir(Conexao.getConexao(), "INSERT INTO FORMAPAGTO (FPG_NOME, FPG_ATIVO) VALUES (?, ?)",
                f.getNome(), f.isAtivo()));
    }

    @Override
    public void alterar(FormaPagtoModel f) throws SQLException {
        Sql.executar(Conexao.getConexao(), "UPDATE FORMAPAGTO SET FPG_NOME = ?, FPG_ATIVO = ? WHERE FPG_CODIGO = ?",
                f.getNome(), f.isAtivo(), f.getCodigo());
    }

    @Override
    public void excluir(int codigo) throws SQLException {
        Sql.executar(Conexao.getConexao(), "DELETE FROM FORMAPAGTO WHERE FPG_CODIGO = ?", codigo);
    }

    @Override
    public FormaPagtoModel buscar(int codigo) throws SQLException {
        return Sql.primeiro(Conexao.getConexao(), "SELECT * FROM FORMAPAGTO WHERE FPG_CODIGO = ?", MAPA, codigo);
    }

    @Override
    public List<FormaPagtoModel> consultar(String filtro) throws SQLException {
        return Sql.consultar(Conexao.getConexao(), "SELECT * FROM FORMAPAGTO WHERE FPG_NOME LIKE ? ORDER BY FPG_NOME",
                MAPA, Sql.like(filtro));
    }
}

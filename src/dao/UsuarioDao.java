package dao;

import conexao.Conexao;
import java.sql.SQLException;
import java.util.List;
import model.UsuarioModel;

public class UsuarioDao implements Dao<UsuarioModel> {

    private static final Sql.Mapeador<UsuarioModel> MAPA = rs -> {
        UsuarioModel u = new UsuarioModel();
        u.setCodigo(rs.getInt("USU_CODIGO"));
        u.setNome(rs.getString("USU_NOME"));
        u.setLogin(rs.getString("USU_LOGIN"));
        u.setSenha(rs.getString("USU_SENHA"));
        u.setCadastro(Sql.data(rs, "USU_CADASTRO"));
        u.setAtivo(Sql.sim(rs, "USU_ATIVO"));
        return u;
    };

    @Override
    public void adicionar(UsuarioModel u) throws SQLException {
        u.setCodigo(Sql.inserir(Conexao.getConexao(),
                "INSERT INTO USUARIO (USU_NOME, USU_LOGIN, USU_SENHA, USU_CADASTRO, USU_ATIVO) VALUES (?, ?, ?, ?, ?)",
                u.getNome(), u.getLogin(), u.getSenha(), u.getCadastro(), u.isAtivo()));
    }

    @Override
    public void alterar(UsuarioModel u) throws SQLException {
        Sql.executar(Conexao.getConexao(),
                "UPDATE USUARIO SET USU_NOME = ?, USU_LOGIN = ?, USU_SENHA = ?, USU_ATIVO = ? WHERE USU_CODIGO = ?",
                u.getNome(), u.getLogin(), u.getSenha(), u.isAtivo(), u.getCodigo());
    }

    @Override
    public void excluir(int codigo) throws SQLException {
        Sql.executar(Conexao.getConexao(), "DELETE FROM USUARIO WHERE USU_CODIGO = ?", codigo);
    }

    @Override
    public UsuarioModel buscar(int codigo) throws SQLException {
        return Sql.primeiro(Conexao.getConexao(), "SELECT * FROM USUARIO WHERE USU_CODIGO = ?", MAPA, codigo);
    }

    @Override
    public List<UsuarioModel> consultar(String filtro) throws SQLException {
        String f = Sql.like(filtro);
        return Sql.consultar(Conexao.getConexao(),
                "SELECT * FROM USUARIO WHERE USU_NOME LIKE ? OR USU_LOGIN LIKE ? ORDER BY USU_NOME", MAPA, f, f);
    }

    public UsuarioModel buscarPorLogin(String login) throws SQLException {
        return Sql.primeiro(Conexao.getConexao(), "SELECT * FROM USUARIO WHERE LOWER(USU_LOGIN) = LOWER(?)", MAPA, login);
    }
}

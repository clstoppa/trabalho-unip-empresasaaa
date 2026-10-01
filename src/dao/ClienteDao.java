package dao;

import conexao.Conexao;
import java.sql.SQLException;
import java.util.List;
import model.ClienteModel;

public class ClienteDao implements Dao<ClienteModel> {

    private static final String SELECT = "SELECT C.CLI_CODIGO, C.CLI_LIMITECRED, P.* FROM CLIENTE C"
            + " JOIN PESSOA P ON P.PES_CODIGO = C.PES_CODIGO";

    private static final Sql.Mapeador<ClienteModel> MAPA = rs -> {
        ClienteModel c = new ClienteModel();
        PessoaDao.preencher(c, rs);
        c.setCliCodigo(rs.getInt("CLI_CODIGO"));
        c.setLimiteCredito(rs.getDouble("CLI_LIMITECRED"));
        return c;
    };

    @Override
    public void adicionar(ClienteModel c) throws SQLException {
        try {
            Conexao.transacao(cn -> {
                int pes = PessoaDao.inserir(cn, c);
                c.setCliCodigo(Sql.inserir(cn, "INSERT INTO CLIENTE (PES_CODIGO, CLI_LIMITECRED) VALUES (?, ?)", pes, c.getLimiteCredito()));
            });
        } catch (SQLException e) {
            c.setCodigo(0);
            c.setCliCodigo(0);
            throw e;
        }
    }

    @Override
    public void alterar(ClienteModel c) throws SQLException {
        Conexao.transacao(cn -> {
            PessoaDao.atualizar(cn, c);
            Sql.executar(cn, "UPDATE CLIENTE SET CLI_LIMITECRED = ? WHERE CLI_CODIGO = ?", c.getLimiteCredito(), c.getCliCodigo());
        });
    }

    @Override
    public void excluir(int cliCodigo) throws SQLException {
        Conexao.transacao(cn -> {
            ClienteModel c = Sql.primeiro(cn, SELECT + " WHERE C.CLI_CODIGO = ?", MAPA, cliCodigo);
            if (c != null) {
                Sql.executar(cn, "DELETE FROM CLIENTE WHERE CLI_CODIGO = ?", cliCodigo);
                PessoaDao.excluir(cn, c.getCodigo());
            }
        });
    }

    @Override
    public ClienteModel buscar(int cliCodigo) throws SQLException {
        return Sql.primeiro(Conexao.getConexao(), SELECT + " WHERE C.CLI_CODIGO = ?", MAPA, cliCodigo);
    }

    @Override
    public List<ClienteModel> consultar(String filtro) throws SQLException {
        String f = Sql.like(filtro);
        return Sql.consultar(Conexao.getConexao(), SELECT
                + " WHERE P.PES_NOME LIKE ? OR P.PES_FANTASIA LIKE ? OR P.PES_CPFCNPJ LIKE ? ORDER BY P.PES_NOME", MAPA, f, f, f);
    }
}

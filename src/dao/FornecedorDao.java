package dao;

import conexao.Conexao;
import java.sql.SQLException;
import java.util.List;
import model.FornecedorModel;

public class FornecedorDao implements Dao<FornecedorModel> {

    private static final String SELECT = "SELECT F.FOR_CODIGO, F.FOR_CONTATO, P.* FROM FORNECEDOR F"
            + " JOIN PESSOA P ON P.PES_CODIGO = F.PES_CODIGO";

    private static final Sql.Mapeador<FornecedorModel> MAPA = rs -> {
        FornecedorModel f = new FornecedorModel();
        PessoaDao.preencher(f, rs);
        f.setForCodigo(rs.getInt("FOR_CODIGO"));
        f.setContato(rs.getString("FOR_CONTATO"));
        return f;
    };

    @Override
    public void adicionar(FornecedorModel f) throws SQLException {
        try {
            Conexao.transacao(cn -> {
                int pes = PessoaDao.inserir(cn, f);
                f.setForCodigo(Sql.inserir(cn, "INSERT INTO FORNECEDOR (PES_CODIGO, FOR_CONTATO) VALUES (?, ?)", pes, f.getContato()));
            });
        } catch (SQLException e) {
            f.setCodigo(0);
            f.setForCodigo(0);
            throw e;
        }
    }

    @Override
    public void alterar(FornecedorModel f) throws SQLException {
        Conexao.transacao(cn -> {
            PessoaDao.atualizar(cn, f);
            Sql.executar(cn, "UPDATE FORNECEDOR SET FOR_CONTATO = ? WHERE FOR_CODIGO = ?", f.getContato(), f.getForCodigo());
        });
    }

    @Override
    public void excluir(int forCodigo) throws SQLException {
        Conexao.transacao(cn -> {
            FornecedorModel f = Sql.primeiro(cn, SELECT + " WHERE F.FOR_CODIGO = ?", MAPA, forCodigo);
            if (f != null) {
                Sql.executar(cn, "DELETE FROM FORNECEDOR WHERE FOR_CODIGO = ?", forCodigo);
                PessoaDao.excluir(cn, f.getCodigo());
            }
        });
    }

    @Override
    public FornecedorModel buscar(int forCodigo) throws SQLException {
        return Sql.primeiro(Conexao.getConexao(), SELECT + " WHERE F.FOR_CODIGO = ?", MAPA, forCodigo);
    }

    @Override
    public List<FornecedorModel> consultar(String filtro) throws SQLException {
        String f = Sql.like(filtro);
        return Sql.consultar(Conexao.getConexao(), SELECT
                + " WHERE P.PES_NOME LIKE ? OR P.PES_FANTASIA LIKE ? OR P.PES_CPFCNPJ LIKE ? ORDER BY P.PES_NOME", MAPA, f, f, f);
    }
}

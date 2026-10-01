package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.PessoaModel;

/** Parte compartilhada por Cliente e Fornecedor (tabela PESSOA). Usa a transação do chamador. */
public final class PessoaDao {

    private PessoaDao() {
    }

    public static void preencher(PessoaModel p, ResultSet rs) throws SQLException {
        p.setCodigo(rs.getInt("PES_CODIGO"));
        p.setNome(rs.getString("PES_NOME"));
        p.setFantasia(rs.getString("PES_FANTASIA"));
        p.setFisica(Sql.sim(rs, "PES_FISICA"));
        p.setCpfCnpj(rs.getString("PES_CPFCNPJ"));
        p.setRgIe(rs.getString("PES_RGIE"));
        p.setCadastro(Sql.data(rs, "PES_CADASTRO"));
        p.setEndereco(rs.getString("PES_ENDERECO"));
        p.setNumero(rs.getString("PES_NUMERO"));
        p.setComplemento(rs.getString("PES_COMPLEMENTO"));
        p.setBairro(rs.getString("PES_BAIRRO"));
        p.setCidade(rs.getString("PES_CIDADE"));
        p.setUf(rs.getString("PES_UF"));
        p.setCep(rs.getString("PES_CEP"));
        p.setFone1(rs.getString("PES_FONE1"));
        p.setFone2(rs.getString("PES_FONE2"));
        p.setCelular(rs.getString("PES_CELULAR"));
        p.setSite(rs.getString("PES_SITE"));
        p.setEmail(rs.getString("PES_EMAIL"));
        p.setAtivo(Sql.sim(rs, "PES_ATIVO"));
    }

    public static int inserir(Connection c, PessoaModel p) throws SQLException {
        int id = Sql.inserir(c, "INSERT INTO PESSOA (PES_NOME, PES_FANTASIA, PES_FISICA, PES_CPFCNPJ, PES_RGIE, PES_CADASTRO,"
                + " PES_ENDERECO, PES_NUMERO, PES_COMPLEMENTO, PES_BAIRRO, PES_CIDADE, PES_UF, PES_CEP, PES_FONE1, PES_FONE2,"
                + " PES_CELULAR, PES_SITE, PES_EMAIL, PES_ATIVO) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                p.getNome(), p.getFantasia(), p.isFisica(), p.getCpfCnpj(), p.getRgIe(), p.getCadastro(),
                p.getEndereco(), p.getNumero(), p.getComplemento(), p.getBairro(), p.getCidade(), p.getUf(), p.getCep(),
                p.getFone1(), p.getFone2(), p.getCelular(), p.getSite(), p.getEmail(), p.isAtivo());
        p.setCodigo(id);
        return id;
    }

    public static void atualizar(Connection c, PessoaModel p) throws SQLException {
        Sql.executar(c, "UPDATE PESSOA SET PES_NOME=?, PES_FANTASIA=?, PES_FISICA=?, PES_CPFCNPJ=?, PES_RGIE=?, PES_CADASTRO=?,"
                + " PES_ENDERECO=?, PES_NUMERO=?, PES_COMPLEMENTO=?, PES_BAIRRO=?, PES_CIDADE=?, PES_UF=?, PES_CEP=?,"
                + " PES_FONE1=?, PES_FONE2=?, PES_CELULAR=?, PES_SITE=?, PES_EMAIL=?, PES_ATIVO=? WHERE PES_CODIGO=?",
                p.getNome(), p.getFantasia(), p.isFisica(), p.getCpfCnpj(), p.getRgIe(), p.getCadastro(),
                p.getEndereco(), p.getNumero(), p.getComplemento(), p.getBairro(), p.getCidade(), p.getUf(), p.getCep(),
                p.getFone1(), p.getFone2(), p.getCelular(), p.getSite(), p.getEmail(), p.isAtivo(), p.getCodigo());
    }

    public static void excluir(Connection c, int pesCodigo) throws SQLException {
        Sql.executar(c, "DELETE FROM PESSOA WHERE PES_CODIGO = ?", pesCodigo);
    }
}

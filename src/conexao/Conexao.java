package conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import util.Seguranca;

/**
 * Conexão JDBC única (SQLite) + criação automática das tabelas do DER.
 * Para usar outro banco troque BANCO/DRIVER (veja README).
 */
public class Conexao {

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String BANCO = "jdbc:sqlite:erp.db";
    // private static final String DRIVER = "org.postgresql.Driver";
    // private static final String BANCO = "jdbc:postgresql://localhost:5432/faculdade?user=postgres&password=SENHA";

    private static Connection conexao;

    /** Bloco de código executado dentro de uma transação. */
    public interface Acao {
        void executar(Connection c) throws SQLException;
    }

    public static synchronized Connection getConexao() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            try {
                Class.forName(DRIVER);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver JDBC não encontrado. Adicione o .jar da pasta driverJDBC ao classpath.");
            }
            conexao = DriverManager.getConnection(BANCO);
            try (Statement st = conexao.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            criarBancoDeDados(conexao);
        }
        return conexao;
    }

    /** Executa a ação com commit no fim e rollback se algo falhar. */
    public static synchronized void transacao(Acao acao) throws SQLException {
        Connection c = getConexao();
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            acao.executar(c);
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
    }

    private static void criarBancoDeDados(Connection c) throws SQLException {
        String[] ddl = {
            "CREATE TABLE IF NOT EXISTS PESSOA ("
            + " PES_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, PES_NOME VARCHAR(80) NOT NULL, PES_FANTASIA VARCHAR(80),"
            + " PES_FISICA CHAR(1) NOT NULL, PES_CPFCNPJ VARCHAR(20) NOT NULL, PES_RGIE VARCHAR(20), PES_CADASTRO DATE,"
            + " PES_ENDERECO VARCHAR(120), PES_NUMERO VARCHAR(10), PES_COMPLEMENTO VARCHAR(30), PES_BAIRRO VARCHAR(50),"
            + " PES_CIDADE VARCHAR(80), PES_UF CHAR(2), PES_CEP VARCHAR(9), PES_FONE1 VARCHAR(16), PES_FONE2 VARCHAR(16),"
            + " PES_CELULAR VARCHAR(16), PES_SITE VARCHAR(200), PES_EMAIL VARCHAR(200), PES_ATIVO CHAR(1))",
            "CREATE TABLE IF NOT EXISTS CLIENTE ("
            + " CLI_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " PES_CODIGO INTEGER NOT NULL REFERENCES PESSOA (PES_CODIGO), CLI_LIMITECRED NUMERIC(18,2))",
            "CREATE TABLE IF NOT EXISTS FORNECEDOR ("
            + " FOR_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " PES_CODIGO INTEGER NOT NULL REFERENCES PESSOA (PES_CODIGO), FOR_CONTATO VARCHAR(80))",
            "CREATE TABLE IF NOT EXISTS USUARIO ("
            + " USU_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, USU_NOME VARCHAR(80), USU_LOGIN VARCHAR(20) NOT NULL UNIQUE,"
            + " USU_SENHA VARCHAR(64), USU_CADASTRO DATE, USU_ATIVO CHAR(1))",
            "CREATE TABLE IF NOT EXISTS FORMAPAGTO ("
            + " FPG_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, FPG_NOME VARCHAR(80) NOT NULL, FPG_ATIVO CHAR(1))",
            "CREATE TABLE IF NOT EXISTS PRODUTO ("
            + " PRO_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, PRO_NOME VARCHAR(80) NOT NULL, PRO_ESTOQUE NUMERIC(14,4),"
            + " PRO_UNIDADE VARCHAR(5), PRO_PRECO NUMERIC(18,2), PRO_CUSTO NUMERIC(18,2), PRO_ATACADO NUMERIC(18,2),"
            + " PRO_MIN NUMERIC(14,4), PRO_MAX NUMERIC(14,4), PRO_EMBALAGEM DECIMAL(9,0), PRO_PESO NUMERIC(14,4),"
            + " PRO_CADASTRO DATE, PRO_OBS CLOB, PRO_ATIVO CHAR(1))",
            "CREATE TABLE IF NOT EXISTS VENDA ("
            + " VDA_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, USU_CODIGO INTEGER NOT NULL REFERENCES USUARIO (USU_CODIGO),"
            + " CLI_CODIGO INTEGER NOT NULL REFERENCES CLIENTE (CLI_CODIGO), VDA_DATA DATE NOT NULL,"
            + " VDA_VALOR NUMERIC(18,2), VDA_DESCONTO NUMERIC(18,2), VDA_TOTAL NUMERIC(18,2), VDA_OBS CLOB)",
            "CREATE TABLE IF NOT EXISTS VENDA_PRODUTO ("
            + " VEP_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, VDA_CODIGO INTEGER NOT NULL REFERENCES VENDA (VDA_CODIGO),"
            + " PRO_CODIGO INTEGER NOT NULL REFERENCES PRODUTO (PRO_CODIGO), VEP_QTDE NUMERIC(14,4), VEP_PRECO NUMERIC(18,2),"
            + " VEP_DESCONTO NUMERIC(18,2), VEP_TOTAL NUMERIC(18,2))",
            "CREATE TABLE IF NOT EXISTS VENDA_PAGTO ("
            + " VDP_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, VDA_CODIGO INTEGER NOT NULL REFERENCES VENDA (VDA_CODIGO),"
            + " FPG_CODIGO INTEGER NOT NULL REFERENCES FORMAPAGTO (FPG_CODIGO), VDP_VALOR NUMERIC(18,2))",
            "CREATE TABLE IF NOT EXISTS COMPRA ("
            + " CPR_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, USU_CODIGO INTEGER NOT NULL REFERENCES USUARIO (USU_CODIGO),"
            + " FOR_CODIGO INTEGER NOT NULL REFERENCES FORNECEDOR (FOR_CODIGO), CPR_EMISSAO DATE, CPR_VALOR NUMERIC(18,2),"
            + " CPR_DESCONTO NUMERIC(18,2), CPR_TOTAL NUMERIC(18,2), CPR_DTENTRADA DATE, CPR_OBS CLOB)",
            "CREATE TABLE IF NOT EXISTS COMPRA_PRODUTO ("
            + " CPP_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT, CPR_CODIGO INTEGER NOT NULL REFERENCES COMPRA (CPR_CODIGO),"
            + " PRO_CODIGO INTEGER NOT NULL REFERENCES PRODUTO (PRO_CODIGO), CPR_QTDE NUMERIC(14,4) NOT NULL,"
            + " CPR_PRECO NUMERIC(18,2) NOT NULL, CPR_DESCONTO NUMERIC(18,2), CPR_TOTAL NUMERIC(18,2))"
        };
        try (Statement st = c.createStatement()) {
            for (String sql : ddl) {
                st.executeUpdate(sql);
            }
            // Dados iniciais: usuário admin/admin e formas de pagamento básicas (só se as tabelas estiverem vazias).
            if (vazia(st, "USUARIO")) {
                st.executeUpdate("INSERT INTO USUARIO (USU_NOME, USU_LOGIN, USU_SENHA, USU_CADASTRO, USU_ATIVO) VALUES "
                        + "('Administrador', 'admin', '" + Seguranca.hash("admin") + "', '" + LocalDate.now() + "', 'S')");
            }
            if (vazia(st, "FORMAPAGTO")) {
                for (String nome : new String[]{"Dinheiro", "Pix", "Cartão de Crédito", "Cartão de Débito", "Boleto"}) {
                    st.executeUpdate("INSERT INTO FORMAPAGTO (FPG_NOME, FPG_ATIVO) VALUES ('" + nome + "', 'S')");
                }
            }
        }
    }

    private static boolean vazia(Statement st, String tabela) throws SQLException {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabela)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }
}

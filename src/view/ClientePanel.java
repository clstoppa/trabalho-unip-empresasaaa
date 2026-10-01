package view;

import controller.ClienteController;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import model.ClienteModel;
import util.Formato;
import util.NegocioException;
import view.ui.Campo;
import view.ui.CampoTexto;
import view.ui.Tabelas;

public class ClientePanel extends CadastroBase {

    private final ClienteController controller = new ClienteController();
    private final PessoaForm form = new PessoaForm();
    private final CampoTexto txtLimite = Campo.texto("0,00");
    private final JCheckBox chkAtivo = Campo.check("Cliente ativo");

    public ClientePanel() {
        txtLimite.setHorizontalAlignment(CampoTexto.RIGHT);
        form.campo("Limite de crédito (R$)", txtLimite, 4);
        form.campo(" ", chkAtivo, 4);
        montar();
    }

    @Override
    protected String titulo() {
        return "Clientes";
    }

    @Override
    protected String singular() {
        return "cliente";
    }

    @Override
    protected String descricao() {
        return "Pessoas físicas e jurídicas que compram de você.";
    }

    @Override
    protected String placeholderBusca() {
        return "Buscar por nome, fantasia ou CPF/CNPJ...";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Cód.", "Nome / Razão social", "CPF / CNPJ", "Cidade", "Celular", "Limite de crédito", "Situação"};
    }

    @Override
    protected void configurarTabela(JTable t) {
        Tabelas.moeda(t, 5);
        Tabelas.largura(t, 1, 280);
    }

    @Override
    protected List<Object[]> listar(String filtro) throws NegocioException {
        List<Object[]> linhas = new ArrayList<>();
        for (ClienteModel c : controller.consultar(filtro)) {
            linhas.add(new Object[]{c.getCliCodigo(), c.getNome(), c.getCpfCnpj(), c.getCidade(), c.getCelular(),
                c.getLimiteCredito(), c.isAtivo() ? "Ativo" : "Inativo"});
        }
        return linhas;
    }

    @Override
    protected JComponent criarFormulario() {
        return form;
    }

    @Override
    protected void novoRegistro() {
        form.limpar();
        txtLimite.setText("");
        chkAtivo.setSelected(true);
        form.focarNome();
    }

    @Override
    protected void editarRegistro(int codigo) throws NegocioException {
        ClienteModel c = controller.buscar(codigo);
        if (c == null) {
            throw new NegocioException("Cliente não encontrado.");
        }
        form.preencher(c);
        txtLimite.setText(Formato.decimal(c.getLimiteCredito()));
        chkAtivo.setSelected(c.isAtivo());
    }

    @Override
    protected void gravarRegistro() throws NegocioException {
        ClienteModel c = idEdicao() == 0 ? new ClienteModel() : controller.buscar(idEdicao());
        if (idEdicao() == 0) {
            c.setCadastro(LocalDate.now());
        }
        form.ler(c);
        c.setLimiteCredito(Formato.numero(txtLimite.getText(), "Limite de crédito"));
        c.setAtivo(chkAtivo.isSelected());
        controller.gravar(c);
    }

    @Override
    protected void excluirRegistro(int codigo) throws NegocioException {
        controller.excluir(codigo);
    }
}

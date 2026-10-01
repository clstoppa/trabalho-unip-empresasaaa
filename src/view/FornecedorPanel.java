package view;

import controller.FornecedorController;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import model.FornecedorModel;
import util.NegocioException;
import view.ui.Campo;
import view.ui.CampoTexto;
import view.ui.Tabelas;

public class FornecedorPanel extends CadastroBase {

    private final FornecedorController controller = new FornecedorController();
    private final PessoaForm form = new PessoaForm();
    private final CampoTexto txtContato = Campo.texto("Nome da pessoa de contato");
    private final JCheckBox chkAtivo = Campo.check("Fornecedor ativo");

    public FornecedorPanel() {
        form.campo("Contato", txtContato, 8);
        form.campo(" ", chkAtivo, 4);
        montar();
    }

    @Override
    protected String titulo() {
        return "Fornecedores";
    }

    @Override
    protected String singular() {
        return "fornecedor";
    }

    @Override
    protected String descricao() {
        return "Empresas e pessoas das quais você compra mercadorias.";
    }

    @Override
    protected String placeholderBusca() {
        return "Buscar por nome, fantasia ou CPF/CNPJ...";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Cód.", "Nome / Razão social", "CPF / CNPJ", "Cidade", "Contato", "Situação"};
    }

    @Override
    protected void configurarTabela(JTable t) {
        Tabelas.largura(t, 1, 280);
    }

    @Override
    protected List<Object[]> listar(String filtro) throws NegocioException {
        List<Object[]> linhas = new ArrayList<>();
        for (FornecedorModel f : controller.consultar(filtro)) {
            linhas.add(new Object[]{f.getForCodigo(), f.getNome(), f.getCpfCnpj(), f.getCidade(), f.getContato(),
                f.isAtivo() ? "Ativo" : "Inativo"});
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
        txtContato.setText("");
        chkAtivo.setSelected(true);
        form.focarNome();
    }

    @Override
    protected void editarRegistro(int codigo) throws NegocioException {
        FornecedorModel f = controller.buscar(codigo);
        if (f == null) {
            throw new NegocioException("Fornecedor não encontrado.");
        }
        form.preencher(f);
        txtContato.setText(f.getContato());
        chkAtivo.setSelected(f.isAtivo());
    }

    @Override
    protected void gravarRegistro() throws NegocioException {
        FornecedorModel f = idEdicao() == 0 ? new FornecedorModel() : controller.buscar(idEdicao());
        if (idEdicao() == 0) {
            f.setCadastro(LocalDate.now());
        }
        form.ler(f);
        f.setContato(txtContato.getText().trim());
        f.setAtivo(chkAtivo.isSelected());
        controller.gravar(f);
    }

    @Override
    protected void excluirRegistro(int codigo) throws NegocioException {
        controller.excluir(codigo);
    }
}

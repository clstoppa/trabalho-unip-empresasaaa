package view;

import controller.FormaPagtoController;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import model.FormaPagtoModel;
import util.NegocioException;
import view.ui.Campo;
import view.ui.CampoTexto;
import view.ui.FormGrid;

public class FormaPagtoPanel extends CadastroBase {

    private final FormaPagtoController controller = new FormaPagtoController();
    private final FormGrid form = new FormGrid();
    private final CampoTexto txtNome = Campo.texto("Ex.: Pix, Dinheiro, Cartão...");
    private final JCheckBox chkAtivo = Campo.check("Forma de pagamento ativa");

    public FormaPagtoPanel() {
        form.secao("Forma de pagamento");
        form.campo("Nome *", txtNome, 6);
        form.campo(" ", chkAtivo, 4);
        montar();
    }

    @Override
    protected String titulo() {
        return "Formas de pagamento";
    }

    @Override
    protected String singular() {
        return "forma de pagamento";
    }

    @Override
    protected String descricao() {
        return "Meios aceitos no recebimento das vendas.";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Cód.", "Nome", "Situação"};
    }

    @Override
    protected List<Object[]> listar(String filtro) throws NegocioException {
        List<Object[]> linhas = new ArrayList<>();
        for (FormaPagtoModel f : controller.consultar(filtro)) {
            linhas.add(new Object[]{f.getCodigo(), f.getNome(), f.isAtivo() ? "Ativa" : "Inativa"});
        }
        return linhas;
    }

    @Override
    protected JComponent criarFormulario() {
        return form;
    }

    @Override
    protected void novoRegistro() {
        txtNome.setText("");
        chkAtivo.setSelected(true);
        focar(txtNome);
    }

    @Override
    protected void editarRegistro(int codigo) throws NegocioException {
        FormaPagtoModel f = controller.buscar(codigo);
        if (f == null) {
            throw new NegocioException("Forma de pagamento não encontrada.");
        }
        txtNome.setText(f.getNome());
        chkAtivo.setSelected(f.isAtivo());
    }

    @Override
    protected void gravarRegistro() throws NegocioException {
        FormaPagtoModel f = idEdicao() == 0 ? new FormaPagtoModel() : controller.buscar(idEdicao());
        f.setNome(txtNome.getText().trim());
        f.setAtivo(chkAtivo.isSelected());
        controller.gravar(f);
    }

    @Override
    protected void excluirRegistro(int codigo) throws NegocioException {
        controller.excluir(codigo);
    }
}

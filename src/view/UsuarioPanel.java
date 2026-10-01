package view;

import controller.UsuarioController;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import model.UsuarioModel;
import util.Formato;
import util.NegocioException;
import view.ui.Campo;
import view.ui.CampoSenha;
import view.ui.CampoTexto;
import view.ui.FormGrid;

public class UsuarioPanel extends CadastroBase {

    private final UsuarioController controller = new UsuarioController();
    private final FormGrid form = new FormGrid();
    private final CampoTexto txtNome = Campo.texto();
    private final CampoTexto txtLogin = Campo.texto("e-mail ou nome de acesso");
    private final CampoSenha txtSenha = Campo.senha();
    private final CampoTexto txtCadastro = Campo.somenteLeitura();
    private final JCheckBox chkAtivo = Campo.check("Usuário ativo");

    public UsuarioPanel() {
        form.secao("Dados de acesso");
        form.campo("Nome *", txtNome, 6);
        form.campo("Login *", txtLogin, 3);
        form.campo("Senha", txtSenha, 3);
        form.novaLinha();
        form.campo("Cadastro", txtCadastro, 3);
        form.campo(" ", chkAtivo, 4);
        montar();
    }

    @Override
    protected String titulo() {
        return "Usuários";
    }

    @Override
    protected String singular() {
        return "usuário";
    }

    @Override
    protected String descricao() {
        return "Pessoas com acesso ao sistema.";
    }

    @Override
    protected String placeholderBusca() {
        return "Buscar por nome ou login...";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Cód.", "Nome", "Login", "Cadastro", "Situação"};
    }

    @Override
    protected List<Object[]> listar(String filtro) throws NegocioException {
        List<Object[]> linhas = new ArrayList<>();
        for (UsuarioModel u : controller.consultar(filtro)) {
            linhas.add(new Object[]{u.getCodigo(), u.getNome(), u.getLogin(), Formato.data(u.getCadastro()),
                u.isAtivo() ? "Ativo" : "Inativo"});
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
        txtLogin.setText("");
        txtSenha.setText("");
        txtCadastro.setText(Formato.data(LocalDate.now()));
        chkAtivo.setSelected(true);
        txtSenha.setToolTipText(null);
        focar(txtNome);
    }

    @Override
    protected void editarRegistro(int codigo) throws NegocioException {
        UsuarioModel u = controller.buscar(codigo);
        if (u == null) {
            throw new NegocioException("Usuário não encontrado.");
        }
        txtNome.setText(u.getNome());
        txtLogin.setText(u.getLogin());
        txtSenha.setText("");
        txtSenha.setToolTipText("Deixe em branco para manter a senha atual");
        txtCadastro.setText(Formato.data(u.getCadastro()));
        chkAtivo.setSelected(u.isAtivo());
    }

    @Override
    protected void gravarRegistro() throws NegocioException {
        UsuarioModel u = idEdicao() == 0 ? new UsuarioModel() : controller.buscar(idEdicao());
        if (idEdicao() == 0) {
            u.setCadastro(LocalDate.now());
        }
        u.setNome(txtNome.getText().trim());
        u.setLogin(txtLogin.getText().trim());
        u.setSenha(new String(txtSenha.getPassword()));
        u.setAtivo(chkAtivo.isSelected());
        controller.gravar(u);
    }

    @Override
    protected void excluirRegistro(int codigo) throws NegocioException {
        controller.excluir(codigo);
    }
}

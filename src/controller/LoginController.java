package controller;

import dao.UsuarioDao;
import java.sql.SQLException;
import java.time.LocalDate;
import model.UsuarioModel;
import util.NegocioException;
import util.Regras;
import util.Seguranca;
import util.Sessao;

/** Regras da tela de login: entrar, criar conta e redefinir senha. */
public class LoginController {

    private final UsuarioDao dao = new UsuarioDao();

    public UsuarioModel autenticar(String login, String senha) throws NegocioException {
        Regras.exigir(!Regras.vazio(login) && !Regras.vazio(senha), "Informe o e-mail/login e a senha.");
        try {
            UsuarioModel u = dao.buscarPorLogin(login.trim());
            Regras.exigir(u != null && u.getSenha().equals(Seguranca.hash(senha)), "Login ou senha incorretos.");
            Regras.exigir(u.isAtivo(), "Este usuário está desativado.");
            Sessao.setUsuario(u);
            return u;
        } catch (SQLException e) {
            throw new NegocioException("Erro ao acessar o banco de dados: " + e.getMessage(), e);
        }
    }

    public void criarConta(String nome, String login, String senha, String confirmacao) throws NegocioException {
        UsuarioModel u = new UsuarioModel();
        u.setNome(nome == null ? "" : nome.trim());
        u.setLogin(login == null ? "" : login.trim());
        u.setSenha(senha);
        u.setCadastro(LocalDate.now());
        u.setAtivo(true);
        Regras.exigir(senha != null && senha.equals(confirmacao), "A confirmação da senha não confere.");
        new UsuarioController().gravar(u);
    }

    public void redefinirSenha(String login, String novaSenha, String confirmacao) throws NegocioException {
        Regras.exigir(!Regras.vazio(login), "Informe o e-mail/login da conta.");
        Regras.exigir(novaSenha != null && novaSenha.length() >= 4, "A nova senha deve ter ao menos 4 caracteres.");
        Regras.exigir(novaSenha.equals(confirmacao), "A confirmação da senha não confere.");
        try {
            UsuarioModel u = dao.buscarPorLogin(login.trim());
            Regras.exigir(u != null, "Nenhuma conta encontrada com esse login.");
            u.setSenha(Seguranca.hash(novaSenha));
            dao.alterar(u);
        } catch (SQLException e) {
            throw new NegocioException("Erro ao acessar o banco de dados: " + e.getMessage(), e);
        }
    }
}

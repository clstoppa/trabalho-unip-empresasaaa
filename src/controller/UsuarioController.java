package controller;

import dao.Dao;
import dao.UsuarioDao;
import java.sql.SQLException;
import model.UsuarioModel;
import util.NegocioException;
import util.Regras;
import util.Seguranca;
import util.Sessao;

/**
 * Cadastro de usuários. Na View a senha chega em texto puro dentro do model;
 * aqui ela é transformada em hash antes de ir para o DAO.
 * Na edição, senha em branco = manter a senha atual.
 */
public class UsuarioController extends CrudController<UsuarioModel> {

    private final UsuarioDao dao = new UsuarioDao();

    @Override
    protected Dao<UsuarioModel> dao() {
        return dao;
    }

    @Override
    protected int codigoDe(UsuarioModel u) {
        return u.getCodigo();
    }

    @Override
    protected void validar(UsuarioModel u) throws NegocioException {
        Regras.exigir(!Regras.vazio(u.getNome()), "Informe o nome do usuário.");
        Regras.exigir(!Regras.vazio(u.getLogin()) && u.getLogin().trim().length() >= 3, "O login deve ter ao menos 3 caracteres.");
        Regras.exigir(u.getLogin().trim().length() <= 20, "O login pode ter no máximo 20 caracteres.");
        Regras.exigir(u.getCodigo() != 0 || !Regras.vazio(u.getSenha()), "Informe uma senha.");
        Regras.exigir(Regras.vazio(u.getSenha()) || u.getSenha().length() >= 4, "A senha deve ter ao menos 4 caracteres.");
        boolean eleMesmo = Sessao.getUsuario() != null && Sessao.getUsuario().getCodigo() == u.getCodigo();
        Regras.exigir(!eleMesmo || u.isAtivo(), "Você não pode desativar o usuário com o qual está logado.");
    }

    @Override
    public void gravar(UsuarioModel u) throws NegocioException {
        validar(u);
        u.setLogin(u.getLogin().trim());
        try {
            UsuarioModel mesmoLogin = dao.buscarPorLogin(u.getLogin());
            Regras.exigir(mesmoLogin == null || mesmoLogin.getCodigo() == u.getCodigo(), "Já existe um usuário com esse login.");
            if (Regras.vazio(u.getSenha())) {
                u.setSenha(dao.buscar(u.getCodigo()).getSenha()); // edição sem trocar senha
            } else {
                u.setSenha(Seguranca.hash(u.getSenha()));
            }
            if (u.getCodigo() == 0) {
                dao.adicionar(u);
            } else {
                dao.alterar(u);
            }
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    @Override
    public void excluir(int codigo) throws NegocioException {
        Regras.exigir(Sessao.getUsuario() == null || Sessao.getUsuario().getCodigo() != codigo,
                "Você não pode excluir o usuário com o qual está logado.");
        super.excluir(codigo);
    }
}

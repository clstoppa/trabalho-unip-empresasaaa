package controller;

import dao.Dao;
import java.sql.SQLException;
import java.util.List;
import util.NegocioException;

/**
 * Controller genérico: a View só conversa com ele, e ele com o DAO.
 * Cada cadastro define apenas o DAO, a validação e como obter o código do objeto.
 */
public abstract class CrudController<T> {

    protected abstract Dao<T> dao();

    protected abstract void validar(T obj) throws NegocioException;

    protected abstract int codigoDe(T obj);

    public List<T> consultar(String filtro) throws NegocioException {
        try {
            return dao().consultar(filtro == null ? "" : filtro.trim());
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    public T buscar(int codigo) throws NegocioException {
        try {
            return dao().buscar(codigo);
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    public void gravar(T obj) throws NegocioException {
        validar(obj);
        try {
            if (codigoDe(obj) == 0) {
                dao().adicionar(obj);
            } else {
                dao().alterar(obj);
            }
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    public void excluir(int codigo) throws NegocioException {
        try {
            dao().excluir(codigo);
        } catch (SQLException e) {
            throw traduzir(e);
        }
    }

    /** Transforma erros técnicos do banco em mensagens que o usuário entende. */
    protected NegocioException traduzir(SQLException e) {
        String m = e.getMessage() == null ? "" : e.getMessage();
        if (m.startsWith("Estoque insuficiente")) {
            return new NegocioException(m, e);
        }
        if (m.contains("FOREIGN KEY")) {
            return new NegocioException("Operação não permitida: este registro está relacionado a outros dados do sistema.", e);
        }
        if (m.contains("UNIQUE")) {
            return new NegocioException("Já existe um registro com esse valor.", e);
        }
        return new NegocioException("Erro no banco de dados: " + m, e);
    }
}

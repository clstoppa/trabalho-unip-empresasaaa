package dao;

import java.sql.SQLException;
import java.util.List;

/** Contrato padrão do padrão DAO: todo cadastro implementa as mesmas cinco operações. */
public interface Dao<T> {

    void adicionar(T obj) throws SQLException;

    void alterar(T obj) throws SQLException;

    void excluir(int codigo) throws SQLException;

    T buscar(int codigo) throws SQLException;

    List<T> consultar(String filtro) throws SQLException;
}

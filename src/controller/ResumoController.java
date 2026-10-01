package controller;

import dao.ResumoDao;
import java.sql.SQLException;
import model.ResumoModel;
import util.NegocioException;

public class ResumoController {

    public ResumoModel carregar() throws NegocioException {
        try {
            return new ResumoDao().carregar();
        } catch (SQLException e) {
            throw new NegocioException("Erro ao carregar o resumo: " + e.getMessage(), e);
        }
    }
}

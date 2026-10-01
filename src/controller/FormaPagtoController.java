package controller;

import dao.Dao;
import dao.FormaPagtoDao;
import java.util.ArrayList;
import java.util.List;
import model.FormaPagtoModel;
import util.NegocioException;
import util.Regras;

public class FormaPagtoController extends CrudController<FormaPagtoModel> {

    private final FormaPagtoDao dao = new FormaPagtoDao();

    @Override
    protected Dao<FormaPagtoModel> dao() {
        return dao;
    }

    @Override
    protected int codigoDe(FormaPagtoModel f) {
        return f.getCodigo();
    }

    @Override
    protected void validar(FormaPagtoModel f) throws NegocioException {
        Regras.exigir(!Regras.vazio(f.getNome()), "Informe o nome da forma de pagamento.");
    }

    public List<FormaPagtoModel> ativas() throws NegocioException {
        List<FormaPagtoModel> lista = new ArrayList<>();
        for (FormaPagtoModel f : consultar("")) {
            if (f.isAtivo()) {
                lista.add(f);
            }
        }
        return lista;
    }
}

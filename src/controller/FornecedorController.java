package controller;

import dao.Dao;
import dao.FornecedorDao;
import java.util.ArrayList;
import java.util.List;
import model.FornecedorModel;
import util.NegocioException;

public class FornecedorController extends CrudController<FornecedorModel> {

    private final FornecedorDao dao = new FornecedorDao();

    @Override
    protected Dao<FornecedorModel> dao() {
        return dao;
    }

    @Override
    protected int codigoDe(FornecedorModel f) {
        return f.getForCodigo();
    }

    @Override
    protected void validar(FornecedorModel f) throws NegocioException {
        PessoaValidador.validar(f, "fornecedor");
    }

    public List<FornecedorModel> ativos() throws NegocioException {
        List<FornecedorModel> lista = new ArrayList<>();
        for (FornecedorModel f : consultar("")) {
            if (f.isAtivo()) {
                lista.add(f);
            }
        }
        return lista;
    }
}

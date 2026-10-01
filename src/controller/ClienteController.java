package controller;

import dao.ClienteDao;
import dao.Dao;
import java.util.ArrayList;
import java.util.List;
import model.ClienteModel;
import util.NegocioException;
import util.Regras;

public class ClienteController extends CrudController<ClienteModel> {

    private final ClienteDao dao = new ClienteDao();

    @Override
    protected Dao<ClienteModel> dao() {
        return dao;
    }

    @Override
    protected int codigoDe(ClienteModel c) {
        return c.getCliCodigo();
    }

    @Override
    protected void validar(ClienteModel c) throws NegocioException {
        PessoaValidador.validar(c, "cliente");
        Regras.exigir(c.getLimiteCredito() >= 0, "O limite de crédito não pode ser negativo.");
    }

    /** Somente clientes ativos (usado na tela de venda). */
    public List<ClienteModel> ativos() throws NegocioException {
        List<ClienteModel> lista = new ArrayList<>();
        for (ClienteModel c : consultar("")) {
            if (c.isAtivo()) {
                lista.add(c);
            }
        }
        return lista;
    }
}

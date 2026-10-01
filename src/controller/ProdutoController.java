package controller;

import dao.Dao;
import dao.ProdutoDao;
import java.util.ArrayList;
import java.util.List;
import model.ProdutoModel;
import util.NegocioException;
import util.Regras;

public class ProdutoController extends CrudController<ProdutoModel> {

    private final ProdutoDao dao = new ProdutoDao();

    @Override
    protected Dao<ProdutoModel> dao() {
        return dao;
    }

    @Override
    protected int codigoDe(ProdutoModel p) {
        return p.getCodigo();
    }

    @Override
    protected void validar(ProdutoModel p) throws NegocioException {
        Regras.exigir(!Regras.vazio(p.getNome()), "Informe o nome do produto.");
        Regras.exigir(p.getPreco() >= 0 && p.getCusto() >= 0 && p.getAtacado() >= 0, "Preços e custo não podem ser negativos.");
        Regras.exigir(p.getEstoque() >= 0, "O estoque não pode ser negativo.");
        Regras.exigir(p.getMinimo() >= 0 && p.getMaximo() >= 0, "Estoque mínimo/máximo não podem ser negativos.");
        Regras.exigir(p.getMaximo() == 0 || p.getMaximo() >= p.getMinimo(), "O estoque máximo deve ser maior ou igual ao mínimo.");
        Regras.exigir(p.getPeso() >= 0, "O peso não pode ser negativo.");
        Regras.exigir(p.getEmbalagem() >= 0, "A embalagem não pode ser negativa.");
    }

    public List<ProdutoModel> ativos() throws NegocioException {
        List<ProdutoModel> lista = new ArrayList<>();
        for (ProdutoModel p : consultar("")) {
            if (p.isAtivo()) {
                lista.add(p);
            }
        }
        return lista;
    }
}

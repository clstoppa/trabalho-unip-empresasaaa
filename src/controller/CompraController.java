package controller;

import dao.CompraDao;
import dao.Dao;
import model.CompraModel;
import model.CompraProdutoModel;
import util.Formato;
import util.NegocioException;
import util.Regras;

public class CompraController extends CrudController<CompraModel> {

    private final CompraDao dao = new CompraDao();

    @Override
    protected Dao<CompraModel> dao() {
        return dao;
    }

    @Override
    protected int codigoDe(CompraModel c) {
        return c.getCodigo();
    }

    public static double totalItem(double qtde, double preco, double desconto) {
        return Formato.arredondar(qtde * preco - desconto);
    }

    /** Recalcula CPR_VALOR (soma dos itens) e CPR_TOTAL (valor - desconto geral). */
    public static void calcular(CompraModel c) {
        double soma = 0;
        for (CompraProdutoModel i : c.getItens()) {
            i.setTotal(totalItem(i.getQtde(), i.getPreco(), i.getDesconto()));
            soma += i.getTotal();
        }
        c.setValor(Formato.arredondar(soma));
        c.setTotal(Formato.arredondar(c.getValor() - c.getDesconto()));
    }

    @Override
    protected void validar(CompraModel c) throws NegocioException {
        Regras.exigir(c.getForCodigo() > 0, "Selecione o fornecedor.");
        Regras.exigir(c.getEmissao() != null, "Informe a data de emissão.");
        Regras.exigir(c.getDtEntrada() == null || !c.getDtEntrada().isBefore(c.getEmissao()),
                "A data de entrada não pode ser anterior à emissão.");
        Regras.exigir(!c.getItens().isEmpty(), "Adicione ao menos um produto à compra.");
        for (CompraProdutoModel i : c.getItens()) {
            Regras.exigir(i.getQtde() > 0, "A quantidade de \"" + i.getProNome() + "\" deve ser maior que zero.");
            Regras.exigir(i.getPreco() >= 0 && i.getDesconto() >= 0, "Preço e desconto de \"" + i.getProNome() + "\" não podem ser negativos.");
            Regras.exigir(i.getDesconto() <= i.getQtde() * i.getPreco() + 0.005, "O desconto de \"" + i.getProNome() + "\" excede o valor do item.");
        }
        calcular(c);
        Regras.exigir(c.getDesconto() >= 0 && c.getDesconto() <= c.getValor() + 0.005, "O desconto geral deve estar entre zero e o valor dos produtos.");
    }
}

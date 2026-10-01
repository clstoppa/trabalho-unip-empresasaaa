package controller;

import dao.Dao;
import dao.VendaDao;
import model.VendaModel;
import model.VendaPagtoModel;
import model.VendaProdutoModel;
import util.Formato;
import util.NegocioException;
import util.Regras;

public class VendaController extends CrudController<VendaModel> {

    private final VendaDao dao = new VendaDao();

    @Override
    protected Dao<VendaModel> dao() {
        return dao;
    }

    @Override
    protected int codigoDe(VendaModel v) {
        return v.getCodigo();
    }

    /** Total de um item: qtde x preço - desconto. */
    public static double totalItem(double qtde, double preco, double desconto) {
        return Formato.arredondar(qtde * preco - desconto);
    }

    /** Recalcula VDA_VALOR (soma dos itens) e VDA_TOTAL (valor - desconto geral). */
    public static void calcular(VendaModel v) {
        double soma = 0;
        for (VendaProdutoModel i : v.getItens()) {
            i.setTotal(totalItem(i.getQtde(), i.getPreco(), i.getDesconto()));
            soma += i.getTotal();
        }
        v.setValor(Formato.arredondar(soma));
        v.setTotal(Formato.arredondar(v.getValor() - v.getDesconto()));
    }

    public static double totalPago(VendaModel v) {
        double pago = 0;
        for (VendaPagtoModel p : v.getPagamentos()) {
            pago += p.getValor();
        }
        return Formato.arredondar(pago);
    }

    @Override
    protected void validar(VendaModel v) throws NegocioException {
        Regras.exigir(v.getCliCodigo() > 0, "Selecione o cliente.");
        Regras.exigir(v.getData() != null, "Informe a data da venda.");
        Regras.exigir(!v.getItens().isEmpty(), "Adicione ao menos um produto à venda.");
        for (VendaProdutoModel i : v.getItens()) {
            Regras.exigir(i.getQtde() > 0, "A quantidade de \"" + i.getProNome() + "\" deve ser maior que zero.");
            Regras.exigir(i.getPreco() >= 0 && i.getDesconto() >= 0, "Preço e desconto de \"" + i.getProNome() + "\" não podem ser negativos.");
            Regras.exigir(i.getDesconto() <= i.getQtde() * i.getPreco() + 0.005, "O desconto de \"" + i.getProNome() + "\" excede o valor do item.");
        }
        calcular(v);
        Regras.exigir(v.getDesconto() >= 0 && v.getDesconto() <= v.getValor() + 0.005, "O desconto geral deve estar entre zero e o valor dos produtos.");
        Regras.exigir(!v.getPagamentos().isEmpty(), "Informe ao menos uma forma de pagamento.");
        double pago = totalPago(v);
        Regras.exigir(Math.abs(pago - v.getTotal()) < 0.005,
                "A soma dos pagamentos (" + Formato.moeda(pago) + ") deve ser igual ao total da venda (" + Formato.moeda(v.getTotal()) + ").");
    }
}

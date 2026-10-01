package model;

/** Tabela COMPRA_PRODUTO. */
public class CompraProdutoModel {

    private int codigo;
    private int cprCodigo;
    private int proCodigo;
    private String proNome;
    private double qtde;
    private double preco;
    private double desconto;
    private double total;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getCprCodigo() {
        return cprCodigo;
    }

    public void setCprCodigo(int cprCodigo) {
        this.cprCodigo = cprCodigo;
    }

    public int getProCodigo() {
        return proCodigo;
    }

    public void setProCodigo(int proCodigo) {
        this.proCodigo = proCodigo;
    }

    public String getProNome() {
        return proNome;
    }

    public void setProNome(String proNome) {
        this.proNome = proNome;
    }

    public double getQtde() {
        return qtde;
    }

    public void setQtde(double qtde) {
        this.qtde = qtde;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}

package model;

/** Indicadores exibidos na tela inicial. */
public class ResumoModel {

    private int clientes;
    private int fornecedores;
    private int produtos;
    private int vendas;
    private int estoqueBaixo;
    private double totalVendido;
    private double totalComprado;

    public int getClientes() {
        return clientes;
    }

    public void setClientes(int clientes) {
        this.clientes = clientes;
    }

    public int getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(int fornecedores) {
        this.fornecedores = fornecedores;
    }

    public int getProdutos() {
        return produtos;
    }

    public void setProdutos(int produtos) {
        this.produtos = produtos;
    }

    public int getVendas() {
        return vendas;
    }

    public void setVendas(int vendas) {
        this.vendas = vendas;
    }

    public int getEstoqueBaixo() {
        return estoqueBaixo;
    }

    public void setEstoqueBaixo(int estoqueBaixo) {
        this.estoqueBaixo = estoqueBaixo;
    }

    public double getTotalVendido() {
        return totalVendido;
    }

    public void setTotalVendido(double totalVendido) {
        this.totalVendido = totalVendido;
    }

    public double getTotalComprado() {
        return totalComprado;
    }

    public void setTotalComprado(double totalComprado) {
        this.totalComprado = totalComprado;
    }
}

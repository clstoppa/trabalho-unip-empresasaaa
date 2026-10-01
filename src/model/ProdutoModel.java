package model;

import java.time.LocalDate;

/** Tabela PRODUTO. */
public class ProdutoModel {

    private int codigo;
    private String nome;
    private double estoque;
    private String unidade;
    private double preco;
    private double custo;
    private double atacado;
    private double minimo;
    private double maximo;
    private int embalagem;
    private double peso;
    private LocalDate cadastro;
    private String obs;
    private boolean ativo = true;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getEstoque() {
        return estoque;
    }

    public void setEstoque(double estoque) {
        this.estoque = estoque;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getCusto() {
        return custo;
    }

    public void setCusto(double custo) {
        this.custo = custo;
    }

    public double getAtacado() {
        return atacado;
    }

    public void setAtacado(double atacado) {
        this.atacado = atacado;
    }

    public double getMinimo() {
        return minimo;
    }

    public void setMinimo(double minimo) {
        this.minimo = minimo;
    }

    public double getMaximo() {
        return maximo;
    }

    public void setMaximo(double maximo) {
        this.maximo = maximo;
    }

    public int getEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(int embalagem) {
        this.embalagem = embalagem;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public LocalDate getCadastro() {
        return cadastro;
    }

    public void setCadastro(LocalDate cadastro) {
        this.cadastro = cadastro;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}

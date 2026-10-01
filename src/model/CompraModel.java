package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Tabela COMPRA com seus itens (COMPRA_PRODUTO). */
public class CompraModel {

    private int codigo;
    private int usuCodigo;
    private String usuarioNome;
    private int forCodigo;
    private String fornecedorNome;
    private LocalDate emissao;
    private double valor;
    private double desconto;
    private double total;
    private LocalDate dtEntrada;
    private String obs;
    private List<CompraProdutoModel> itens = new ArrayList<>();

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getUsuCodigo() {
        return usuCodigo;
    }

    public void setUsuCodigo(int usuCodigo) {
        this.usuCodigo = usuCodigo;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public int getForCodigo() {
        return forCodigo;
    }

    public void setForCodigo(int forCodigo) {
        this.forCodigo = forCodigo;
    }

    public String getFornecedorNome() {
        return fornecedorNome;
    }

    public void setFornecedorNome(String fornecedorNome) {
        this.fornecedorNome = fornecedorNome;
    }

    public LocalDate getEmissao() {
        return emissao;
    }

    public void setEmissao(LocalDate emissao) {
        this.emissao = emissao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
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

    public LocalDate getDtEntrada() {
        return dtEntrada;
    }

    public void setDtEntrada(LocalDate dtEntrada) {
        this.dtEntrada = dtEntrada;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public List<CompraProdutoModel> getItens() {
        return itens;
    }

    public void setItens(List<CompraProdutoModel> itens) {
        this.itens = itens;
    }
}

package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Tabela VENDA com seus itens (VENDA_PRODUTO) e pagamentos (VENDA_PAGTO). */
public class VendaModel {

    private int codigo;
    private int cliCodigo;
    private String clienteNome;
    private int usuCodigo;
    private String usuarioNome;
    private LocalDate data;
    private double valor;
    private double desconto;
    private double total;
    private String obs;
    private List<VendaProdutoModel> itens = new ArrayList<>();
    private List<VendaPagtoModel> pagamentos = new ArrayList<>();

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getCliCodigo() {
        return cliCodigo;
    }

    public void setCliCodigo(int cliCodigo) {
        this.cliCodigo = cliCodigo;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
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

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public List<VendaProdutoModel> getItens() {
        return itens;
    }

    public void setItens(List<VendaProdutoModel> itens) {
        this.itens = itens;
    }

    public List<VendaPagtoModel> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<VendaPagtoModel> pagamentos) {
        this.pagamentos = pagamentos;
    }
}

package model;

/** Tabela VENDA_PAGTO. */
public class VendaPagtoModel {

    private int codigo;
    private int vdaCodigo;
    private int fpgCodigo;
    private String fpgNome;
    private double valor;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getVdaCodigo() {
        return vdaCodigo;
    }

    public void setVdaCodigo(int vdaCodigo) {
        this.vdaCodigo = vdaCodigo;
    }

    public int getFpgCodigo() {
        return fpgCodigo;
    }

    public void setFpgCodigo(int fpgCodigo) {
        this.fpgCodigo = fpgCodigo;
    }

    public String getFpgNome() {
        return fpgNome;
    }

    public void setFpgNome(String fpgNome) {
        this.fpgNome = fpgNome;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}

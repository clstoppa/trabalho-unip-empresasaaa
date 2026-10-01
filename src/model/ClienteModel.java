package model;

/** Tabela CLIENTE (CLI_CODIGO = cliCodigo) + dados de PESSOA. */
public class ClienteModel extends PessoaModel {

    private int cliCodigo;
    private double limiteCredito;

    public int getCliCodigo() {
        return cliCodigo;
    }

    public void setCliCodigo(int cliCodigo) {
        this.cliCodigo = cliCodigo;
    }

    public double getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(double limiteCredito) {
        this.limiteCredito = limiteCredito;
    }
}

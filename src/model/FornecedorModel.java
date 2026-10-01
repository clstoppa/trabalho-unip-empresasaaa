package model;

/** Tabela FORNECEDOR (FOR_CODIGO = forCodigo) + dados de PESSOA. */
public class FornecedorModel extends PessoaModel {

    private int forCodigo;
    private String contato;

    public int getForCodigo() {
        return forCodigo;
    }

    public void setForCodigo(int forCodigo) {
        this.forCodigo = forCodigo;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }
}

package model;

import java.time.LocalDate;

/** Tabela USUARIO. */
public class UsuarioModel {

    private int codigo;
    private String nome;
    private String login;
    private String senha;
    private LocalDate cadastro;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDate getCadastro() {
        return cadastro;
    }

    public void setCadastro(LocalDate cadastro) {
        this.cadastro = cadastro;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}

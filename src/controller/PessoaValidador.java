package controller;

import model.PessoaModel;
import util.Formato;
import util.NegocioException;
import util.Regras;

/** Regras de validação comuns a Cliente e Fornecedor. */
final class PessoaValidador {

    private PessoaValidador() {
    }

    static void validar(PessoaModel p, String papel) throws NegocioException {
        Regras.exigir(!Regras.vazio(p.getNome()), "Informe o nome / razão social do " + papel + ".");
        int esperado = p.isFisica() ? 11 : 14;
        Regras.exigir(Formato.digitos(p.getCpfCnpj()).length() == esperado,
                "Informe um " + (p.isFisica() ? "CPF com 11" : "CNPJ com 14") + " dígitos.");
        Regras.exigir(Regras.vazio(p.getEmail()) || p.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"), "E-mail inválido.");
        Regras.exigir(Regras.vazio(p.getUf()) || p.getUf().length() == 2, "UF inválida.");
        Regras.exigir(Regras.vazio(p.getCep()) || Formato.digitos(p.getCep()).length() == 8, "CEP deve ter 8 dígitos.");
    }
}

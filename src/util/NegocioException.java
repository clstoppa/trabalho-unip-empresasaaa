package util;

/** Erro de regra de negócio/validação. A mensagem é exibida diretamente ao usuário. */
public class NegocioException extends Exception {

    public NegocioException(String mensagem) {
        super(mensagem);
    }

    public NegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

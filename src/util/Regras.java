package util;

/** Pequenos atalhos de validação usados pelos controllers. */
public final class Regras {

    private Regras() {
    }

    public static void exigir(boolean condicao, String mensagem) throws NegocioException {
        if (!condicao) {
            throw new NegocioException(mensagem);
        }
    }

    public static boolean vazio(String s) {
        return s == null || s.trim().isEmpty();
    }
}

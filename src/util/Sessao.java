package util;

import model.UsuarioModel;

/** Guarda o usuário logado enquanto o sistema está aberto. */
public final class Sessao {

    private static UsuarioModel usuario;

    private Sessao() {
    }

    public static UsuarioModel getUsuario() {
        return usuario;
    }

    public static void setUsuario(UsuarioModel u) {
        usuario = u;
    }
}

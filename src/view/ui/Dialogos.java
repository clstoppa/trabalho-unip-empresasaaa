package view.ui;

import java.awt.Component;
import javax.swing.JOptionPane;

/** Mensagens padronizadas do sistema. */
public final class Dialogos {

    private Dialogos() {
    }

    public static void info(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component pai, String mensagem) {
        Object[] opcoes = {"Sim", "Não"};
        return JOptionPane.showOptionDialog(pai, mensagem, "Confirmação", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[1]) == 0;
    }
}

package view;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** DocumentListener que dispara a mesma ação para qualquer mudança no texto. */
public class SimpleDocListener implements DocumentListener {

    private final Runnable acao;

    public SimpleDocListener(Runnable acao) {
        this.acao = acao;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        acao.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        acao.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        acao.run();
    }
}

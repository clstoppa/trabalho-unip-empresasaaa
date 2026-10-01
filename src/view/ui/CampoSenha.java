package view.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JPasswordField;

/** Campo de senha com o mesmo visual do CampoTexto. */
public class CampoSenha extends JPasswordField {

    private final boolean escuro;

    public CampoSenha(boolean escuro) {
        this.escuro = escuro;
        setOpaque(false);
        setFont(Tema.fonte(java.awt.Font.PLAIN, 14f));
        setForeground(escuro ? Color.WHITE : Tema.TEXTO);
        setCaretColor(escuro ? Color.WHITE : Tema.PRIMARIA);
        setBorder(new BordaCampo(escuro, null));
        setPreferredSize(new Dimension(40, 40));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(escuro ? Tema.CAMPO_ESCURO : Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BordaCampo.RAIO, BordaCampo.RAIO);
        g2.dispose();
        super.paintComponent(g);
    }
}

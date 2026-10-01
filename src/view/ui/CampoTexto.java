package view.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;

/** Campo de texto moderno: cantos arredondados, placeholder e variante escura (tela de login). */
public class CampoTexto extends JTextField {

    private String placeholder = "";
    private final boolean escuro;

    public CampoTexto(boolean escuro) {
        this.escuro = escuro;
        setOpaque(false);
        setFont(Tema.fonte(java.awt.Font.PLAIN, 14f));
        setForeground(escuro ? Color.WHITE : Tema.TEXTO);
        setCaretColor(escuro ? Color.WHITE : Tema.PRIMARIA);
        setDisabledTextColor(Tema.MUTED);
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

    public void setPlaceholder(String texto) {
        this.placeholder = texto == null ? "" : texto;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(escuro ? Tema.CAMPO_ESCURO : (isEditable() && isEnabled() ? Color.WHITE : Tema.CAMPO_DESABILITADO));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BordaCampo.RAIO, BordaCampo.RAIO);
        g2.dispose();
        super.paintComponent(g);
        if (getText().isEmpty() && !placeholder.isEmpty()) {
            Graphics2D p = (Graphics2D) g.create();
            p.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            p.setColor(escuro ? new Color(0x7C7A92) : new Color(0xA19EB5));
            p.setFont(getFont());
            FontMetrics fm = p.getFontMetrics();
            p.drawString(placeholder, getInsets().left, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            p.dispose();
        }
    }
}

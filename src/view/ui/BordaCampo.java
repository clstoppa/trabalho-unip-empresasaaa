package view.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import javax.swing.border.Border;

/** Borda arredondada dos campos; fica roxa (e mais grossa) quando o campo tem o foco. */
class BordaCampo implements Border {

    static final int RAIO = 12;
    private final boolean escuro;
    private final Component foco;

    BordaCampo(boolean escuro, Component foco) {
        this.escuro = escuro;
        this.foco = foco;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Component alvo = foco != null ? foco : c;
        boolean focado = alvo.hasFocus();
        if (alvo instanceof javax.swing.JComboBox) {
            focado = focado || ((javax.swing.JComboBox<?>) alvo).isPopupVisible();
        }
        if (focado) {
            g2.setColor(Tema.PRIMARIA);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, RAIO, RAIO);
        } else {
            g2.setColor(escuro ? new Color(0x3A3950) : Tema.BORDA);
            g2.drawRoundRect(x, y, w - 1, h - 1, RAIO, RAIO);
        }
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(8, 12, 8, 12);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}

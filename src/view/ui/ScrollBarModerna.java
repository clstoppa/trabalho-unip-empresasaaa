package view.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

/** Barra de rolagem fina, sem setas. Registrada em Tema.instalar(). */
public class ScrollBarModerna extends BasicScrollBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new ScrollBarModerna();
    }

    private JButton semBotao() {
        JButton b = new JButton();
        Dimension zero = new Dimension(0, 0);
        b.setPreferredSize(zero);
        b.setMinimumSize(zero);
        b.setMaximumSize(zero);
        return b;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return semBotao();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return semBotao();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.setColor(Color.WHITE);
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isDragging || isThumbRollover() ? Tema.PRIMARIA : new Color(0xC9C3DE));
        int w = 8;
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            g2.fillRoundRect(r.x + (r.width - w) / 2, r.y + 2, w, r.height - 4, w, w);
        } else {
            g2.fillRoundRect(r.x + 2, r.y + (r.height - w) / 2, r.width - 4, w, w, w);
        }
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(12, 12);
    }
}

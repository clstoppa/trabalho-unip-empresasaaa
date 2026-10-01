package view.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/** Painel branco de cantos arredondados com sombra suave. */
public class Card extends JPanel {

    public Card(LayoutManager layout, int margem) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(margem, margem + 2, margem + 3, margem + 2));
    }

    public Card(LayoutManager layout) {
        this(layout, 20);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setColor(new Color(40, 25, 90, 14));
        g2.fillRoundRect(1, 3, w - 2, h - 3, 18, 18);
        g2.setColor(new Color(40, 25, 90, 10));
        g2.fillRoundRect(0, 2, w, h - 2, 20, 20);
        g2.setColor(Tema.CARD);
        g2.fillRoundRect(0, 0, w, h - 3, 16, 16);
        g2.setColor(Tema.BORDA);
        g2.drawRoundRect(0, 0, w - 1, h - 4, 16, 16);
        g2.dispose();
        super.paintComponent(g);
    }
}

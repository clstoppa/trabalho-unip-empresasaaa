package view.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/** Botão arredondado com quatro estilos. */
public class Botao extends JButton {

    public enum Tipo { PRIMARIO, DEGRADE, SECUNDARIO, PERIGO, LINK }

    private final Tipo tipo;
    private boolean sobre;
    private boolean pressionado;

    public Botao(String texto, Tipo tipo) {
        super(texto);
        this.tipo = tipo;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setFont(Tema.fonte(java.awt.Font.BOLD, 13f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(tipo == Tipo.LINK ? 4 : 10, tipo == Tipo.LINK ? 4 : 20, tipo == Tipo.LINK ? 4 : 10, tipo == Tipo.LINK ? 4 : 20));
        Color fg = Color.WHITE;
        if (tipo == Tipo.SECUNDARIO) {
            fg = Tema.TEXTO;
        } else if (tipo == Tipo.LINK) {
            fg = Tema.PRIMARIA;
        }
        setForeground(fg);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sobre = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sobre = false;
                pressionado = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressionado = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressionado = false;
                repaint();
            }
        });
    }

    public Botao(String texto) {
        this(texto, Tipo.PRIMARIO);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(d.height, tipo == Tipo.LINK ? 24 : 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight(), arco = 12;
        if (!isEnabled()) {
            g2.setComposite(java.awt.AlphaComposite.SrcOver.derive(0.45f));
        }
        Color a, b;
        switch (tipo) {
            case PRIMARIO:
                a = sobre ? Tema.PRIMARIA_ESCURA : Tema.PRIMARIA;
                b = sobre ? new Color(0x8E3FE0) : new Color(0xA04BF0);
                g2.setPaint(new GradientPaint(0, 0, a, w, 0, b));
                g2.fillRoundRect(0, pressionado ? 1 : 0, w, h - 1, arco, arco);
                break;
            case DEGRADE:
                a = sobre ? new Color(0x5F3BF0) : new Color(0x7B5CFF);
                b = sobre ? new Color(0xFF7A45) : Tema.ACENTO;
                g2.setPaint(new GradientPaint(0, 0, a, w, 0, b));
                g2.fillRoundRect(0, pressionado ? 1 : 0, w, h - 1, arco, arco);
                break;
            case PERIGO:
                g2.setColor(sobre ? new Color(0xCC3A3F) : Tema.PERIGO);
                g2.fillRoundRect(0, pressionado ? 1 : 0, w, h - 1, arco, arco);
                break;
            case SECUNDARIO:
                g2.setColor(sobre ? Tema.PRIMARIA_CLARA : Color.WHITE);
                g2.fillRoundRect(0, 0, w - 1, h - 1, arco, arco);
                g2.setColor(sobre ? Tema.PRIMARIA : Tema.BORDA);
                g2.drawRoundRect(0, 0, w - 1, h - 1, arco, arco);
                break;
            default:
                break;
        }
        g2.dispose();
        super.paintComponent(g);
    }
}

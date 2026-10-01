package view.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/** Caixa de seleção com o visual do tema (seta fina, lista com destaque roxo). */
public class CampoCombo<E> extends JComboBox<E> {

    public CampoCombo() {
        setUI(new Visual());
        setOpaque(false);
        setFont(Tema.fonte(java.awt.Font.PLAIN, 14f));
        setBorder(new BordaCampo(false, this));
        setPreferredSize(new Dimension(40, 40));
        setMaximumRowCount(10);
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selecionado, boolean foco) {
                super.getListCellRendererComponent(list, value, index, selecionado, foco);
                setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
                setBackground(selecionado ? Tema.PRIMARIA_CLARA : Color.WHITE);
                setForeground(selecionado ? Tema.PRIMARIA : Tema.TEXTO);
                return this;
            }
        });
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
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                repaint();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                repaint();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isEnabled() ? Color.WHITE : Tema.CAMPO_DESABILITADO);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BordaCampo.RAIO, BordaCampo.RAIO);
        g2.dispose();
        super.paintComponent(g);
    }

    /** UI própria: sem fundo azul de seleção e com seta desenhada à mão. */
    private static class Visual extends BasicComboBoxUI {

        @Override
        protected JButton createArrowButton() {
            JButton b = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Tema.MUTED);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int cx = getWidth() / 2, cy = getHeight() / 2;
                    g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
                    g2.drawLine(cx, cy + 2, cx + 4, cy - 2);
                    g2.dispose();
                }
            };
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            return b;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // o fundo arredondado já é desenhado pelo próprio CampoCombo
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle r, boolean hasFocus) {
            Object v = comboBox.getSelectedItem();
            if (v == null) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.clipRect(r.x, r.y, r.width, r.height);
            g2.setFont(comboBox.getFont());
            g2.setColor(comboBox.isEnabled() ? Tema.TEXTO : Tema.MUTED);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(v.toString(), r.x, r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup p = new BasicComboPopup(comboBox);
            p.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
            return p;
        }
    }
}

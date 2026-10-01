package view.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Área de texto com o visual do tema, já dentro de um JScrollPane (adicione este componente ao layout). */
public class CampoArea extends JScrollPane {

    private final JTextArea area = new JTextArea();

    public CampoArea(int altura) {
        super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        area.setFont(Tema.fonte(java.awt.Font.PLAIN, 14f));
        area.setForeground(Tema.TEXTO);
        area.setCaretColor(Tema.PRIMARIA);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        setOpaque(false);
        setViewportView(area);
        getViewport().setBackground(Color.WHITE);
        setBorder(new BordaCampo(false, area));
        setPreferredSize(new Dimension(40, altura));
        area.addFocusListener(new FocusAdapter() {
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

    public JTextArea getArea() {
        return area;
    }

    public String getText() {
        return area.getText();
    }

    public void setText(String t) {
        area.setText(t == null ? "" : t);
        area.setCaretPosition(0);
    }
}

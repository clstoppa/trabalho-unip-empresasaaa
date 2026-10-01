package view.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

/** Fábrica dos componentes de formulário (mantém todos com o mesmo visual). */
public final class Campo {

    private Campo() {
    }

    public static CampoTexto texto() {
        return new CampoTexto(false);
    }

    public static CampoTexto texto(String placeholder) {
        CampoTexto c = new CampoTexto(false);
        c.setPlaceholder(placeholder);
        return c;
    }

    public static CampoTexto somenteLeitura() {
        CampoTexto c = new CampoTexto(false);
        c.setEditable(false);
        c.setFocusable(false);
        return c;
    }

    public static CampoSenha senha() {
        return new CampoSenha(false);
    }

    public static <E> CampoCombo<E> combo() {
        return new CampoCombo<>();
    }

    public static CampoArea area(int altura) {
        return new CampoArea(altura);
    }

    public static JCheckBox check(String texto) {
        JCheckBox c = new JCheckBox(texto);
        c.setOpaque(false);
        c.setFont(Tema.fonte(Font.PLAIN, 14f));
        c.setForeground(Tema.TEXTO);
        c.setFocusPainted(false);
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return c;
    }

    public static JRadioButton radio(String texto) {
        JRadioButton r = new JRadioButton(texto);
        r.setOpaque(false);
        r.setFont(Tema.fonte(Font.PLAIN, 14f));
        r.setForeground(Tema.TEXTO);
        r.setFocusPainted(false);
        r.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return r;
    }

    public static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.fonte(Font.BOLD, 12f));
        l.setForeground(Tema.MUTED);
        return l;
    }

    public static JLabel titulo(String texto, float tamanho) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.fonte(Font.BOLD, tamanho));
        l.setForeground(Tema.TEXTO);
        return l;
    }

    public static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.fonte(Font.PLAIN, 13f));
        l.setForeground(Tema.MUTED);
        return l;
    }

    public static JLabel rotuloCor(String texto, Color cor, float tamanho, int estilo) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.fonte(estilo, tamanho));
        l.setForeground(cor);
        return l;
    }
}

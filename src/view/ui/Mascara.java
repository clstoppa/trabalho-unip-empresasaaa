package view.ui;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

/**
 * Máscara de digitação. Cada padrão usa '#' para um dígito (ex.: "###.###.###-##").
 * Com vários padrões, é usado o primeiro que comporta a quantidade de dígitos digitada
 * (assim CPF e CNPJ, ou telefone fixo e celular, funcionam no mesmo campo).
 */
public final class Mascara {

    public static final String[] CPF_CNPJ = {"###.###.###-##", "##.###.###/####-##"};
    public static final String[] TELEFONE = {"(##) ####-####", "(##) #####-####"};
    public static final String[] CEP = {"#####-###"};
    public static final String[] DATA = {"##/##/####"};

    private Mascara() {
    }

    public static void instalar(JTextComponent campo, final String... padroes) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String s, AttributeSet a) throws BadLocationException {
                replace(fb, offset, 0, s, a);
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                replace(fb, offset, length, "", null);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet a) throws BadLocationException {
                String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String novo;
                boolean apagando = texto == null || texto.isEmpty();
                if (apagando && length > 0 && Mascara.apenasDigitos(atual.substring(offset, offset + length)).isEmpty()) {
                    // backspace sobre um símbolo da máscara: apaga o dígito anterior
                    int i = offset - 1;
                    while (i >= 0 && !Character.isDigit(atual.charAt(i))) {
                        i--;
                    }
                    novo = i < 0 ? atual : atual.substring(0, i) + atual.substring(offset + length);
                } else {
                    novo = atual.substring(0, offset) + (texto == null ? "" : texto) + atual.substring(offset + length);
                }
                fb.replace(0, atual.length(), Mascara.aplicar(Mascara.apenasDigitos(novo), padroes), a);
            }
        });
    }

    static String apenasDigitos(String s) {
        return s.replaceAll("\\D", "");
    }

    static String aplicar(String digitos, String[] padroes) {
        String padrao = padroes[padroes.length - 1];
        for (String p : padroes) {
            if (p.replaceAll("[^#]", "").length() >= digitos.length()) {
                padrao = p;
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        int d = 0;
        for (int i = 0; i < padrao.length() && d < digitos.length(); i++) {
            char c = padrao.charAt(i);
            if (c == '#') {
                sb.append(digitos.charAt(d++));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

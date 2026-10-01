package view.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

/**
 * Grade de 12 colunas para montar formulários: cada campo informa quantas colunas ocupa
 * (span). Ao somar 12, a linha é fechada automaticamente. Rótulo fica acima do campo.
 */
public class FormGrid extends JPanel {

    public static final int COLUNAS = 12;
    private int linha = 1;
    private int coluna = 0;
    private JLabel ultimoRotulo;

    public FormGrid() {
        super(new GridBagLayout());
        setOpaque(false);
        // linha "régua" invisível: garante colunas de larguras iguais
        for (int i = 0; i < COLUNAS; i++) {
            GridBagConstraints g = new GridBagConstraints();
            g.gridx = i;
            g.gridy = 0;
            g.weightx = 1;
            g.fill = GridBagConstraints.HORIZONTAL;
            add(Box.createRigidArea(new Dimension(1, 0)), g);
        }
    }

    public FormGrid campo(String rotulo, JComponent componente, int span) {
        if (coluna + span > COLUNAS) {
            novaLinha();
        }
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        JLabel l = Campo.rotulo(rotulo);
        ultimoRotulo = l;
        p.add(l, BorderLayout.NORTH);
        p.add(componente, BorderLayout.CENTER);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = coluna;
        g.gridy = linha;
        g.gridwidth = span;
        g.weightx = span;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH;
        g.insets = new Insets(0, 0, 16, coluna + span >= COLUNAS ? 0 : 14);
        add(p, g);
        coluna += span;
        if (coluna >= COLUNAS) {
            novaLinha();
        }
        return this;
    }

    /** Rótulo do último campo adicionado (permite trocar o texto depois). */
    public JLabel rotuloAtual() {
        return ultimoRotulo;
    }

    /** Título de seção ocupando a linha inteira. */
    public FormGrid secao(String titulo) {
        if (coluna > 0) {
            novaLinha();
        }
        JLabel l = Campo.titulo(titulo, 14f);
        l.setBorder(new MatteBorder(0, 0, 1, 0, Tema.BORDA));
        l.setPreferredSize(new Dimension(10, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = linha;
        g.gridwidth = COLUNAS;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 14, 0);
        add(l, g);
        novaLinha();
        return this;
    }

    /** Componente livre ocupando a linha inteira (ex.: tabela de itens). */
    public FormGrid linhaInteira(JComponent componente) {
        if (coluna > 0) {
            novaLinha();
        }
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = linha;
        g.gridwidth = COLUNAS;
        g.weightx = COLUNAS;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 16, 0);
        add(componente, g);
        novaLinha();
        return this;
    }

    public void novaLinha() {
        linha++;
        coluna = 0;
    }
}

package view.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import util.Formato;

/** Visual das tabelas (grades) do sistema: linhas zebradas, cabeçalho limpo, valores alinhados. */
public final class Tabelas {

    private Tabelas() {
    }

    /** Modelo de tabela que não permite editar as células diretamente. */
    public static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int l, int c) {
                return false;
            }

            /** Usa o tipo dos dados (números ordenam como números e ficam alinhados à direita). */
            @Override
            public Class<?> getColumnClass(int c) {
                for (int r = 0; r < getRowCount(); r++) {
                    Object v = getValueAt(r, c);
                    if (v != null) {
                        return v.getClass();
                    }
                }
                return Object.class;
            }
        };
    }

    public static JScrollPane estilizar(final JTable t) {
        t.setRowHeight(40);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setFont(Tema.fonte(Font.PLAIN, 13f));
        t.setForeground(Tema.TEXTO);
        t.setSelectionBackground(Tema.PRIMARIA_CLARA);
        t.setSelectionForeground(Tema.TEXTO);
        t.setFocusable(true);
        t.setDefaultRenderer(Object.class, new Celula(false));
        t.setDefaultRenderer(Number.class, new Celula(false));
        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(10, 42));
        h.setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foco, int l, int c) {
                JLabel lb = new JLabel(v == null ? "" : v.toString().toUpperCase());
                lb.setOpaque(true);
                lb.setBackground(new Color(0xF8F7FC));
                lb.setForeground(Tema.MUTED);
                lb.setFont(Tema.fonte(Font.BOLD, 11f));
                lb.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDA), BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                Class<?> tipo = tb.getModel().getColumnClass(c);
                lb.setHorizontalAlignment(Number.class.isAssignableFrom(tipo) ? SwingConstants.RIGHT : SwingConstants.LEFT);
                return lb;
            }
        });
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    /** Mostra as colunas indicadas como moeda (R$ 1.234,56), alinhadas à direita. */
    public static void moeda(JTable t, int... colunas) {
        for (int c : colunas) {
            t.getColumnModel().getColumn(c).setCellRenderer(new Celula(true));
        }
    }

    public static void largura(JTable t, int coluna, int largura) {
        t.getColumnModel().getColumn(coluna).setPreferredWidth(largura);
    }

    private static class Celula extends DefaultTableCellRenderer {

        private final boolean moeda;

        Celula(boolean moeda) {
            this.moeda = moeda;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int l, int c) {
            String texto;
            boolean numero = v instanceof Number;
            if (numero && moeda) {
                texto = Formato.moeda(((Number) v).doubleValue());
            } else if (v instanceof Double) {
                texto = Formato.quantidade((Double) v);
            } else {
                texto = v == null ? "" : v.toString();
            }
            super.getTableCellRendererComponent(t, texto, sel, false, l, c);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            setHorizontalAlignment(numero ? SwingConstants.RIGHT : SwingConstants.LEFT);
            setBackground(sel ? Tema.PRIMARIA_CLARA : (l % 2 == 0 ? Color.WHITE : new Color(0xFBFAFE)));
            setForeground(Tema.TEXTO);
            return this;
        }
    }
}

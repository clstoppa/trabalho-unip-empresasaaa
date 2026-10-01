package view.ui;

import java.util.List;
import javax.swing.JComboBox;

/** Item de combo: guarda o código (id) e o texto mostrado. Id 0 = "nenhum". */
public final class Opcao {

    public final int id;
    public final String texto;

    public Opcao(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }

    /** Troca o conteúdo do combo, com um primeiro item vazio (id 0). */
    public static void preencher(JComboBox<Opcao> combo, String textoVazio, List<Opcao> itens) {
        int anterior = idDe(combo);
        combo.removeAllItems();
        combo.addItem(new Opcao(0, textoVazio));
        for (Opcao o : itens) {
            combo.addItem(o);
        }
        selecionar(combo, anterior);
    }

    public static int idDe(JComboBox<Opcao> combo) {
        Object o = combo.getSelectedItem();
        return o instanceof Opcao ? ((Opcao) o).id : 0;
    }

    public static void selecionar(JComboBox<Opcao> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }
}

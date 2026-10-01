package principal;

import conexao.Conexao;
import javax.swing.SwingUtilities;
import view.LoginView;
import view.ui.Dialogos;
import view.ui.Tema;

public class Main {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> {
            Tema.instalar();
            try {
                Conexao.getConexao(); // cria o banco e as tabelas na primeira execução
            } catch (Exception e) {
                Dialogos.erro(null, "Não foi possível abrir o banco de dados:\n" + e.getMessage());
                System.exit(1);
            }
            new LoginView().setVisible(true);
        });
    }
}

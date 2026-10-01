package view.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

/** Paleta, fontes e ajustes globais do visual do sistema. */
public final class Tema {

    public static final Color FUNDO = new Color(0xF4F2FB);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXTO = new Color(0x1E1B2E);
    public static final Color MUTED = new Color(0x6B6880);
    public static final Color BORDA = new Color(0xE3E0EE);
    public static final Color PRIMARIA = new Color(0x6D4AFF);
    public static final Color PRIMARIA_ESCURA = new Color(0x5636E0);
    public static final Color PRIMARIA_CLARA = new Color(0xEDE8FF);
    public static final Color ACENTO = new Color(0xFF8A5B);
    public static final Color PERIGO = new Color(0xE5484D);
    public static final Color SUCESSO = new Color(0x12A594);
    public static final Color MENU = new Color(0x15112B);
    public static final Color MENU_SELECAO = new Color(0x2C2552);
    public static final Color CAMPO_ESCURO = new Color(0x23222E);
    public static final Color CAMPO_DESABILITADO = new Color(0xF3F1F9);

    private static final String FAMILIA = escolherFamilia();

    private Tema() {
    }

    private static String escolherFamilia() {
        Set<String> disponiveis = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String f : new String[]{"Segoe UI", "Inter", "Poppins", "Helvetica Neue", "Arial"}) {
            if (disponiveis.contains(f)) {
                return f;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font fonte(int estilo, float tamanho) {
        return new Font(FAMILIA, estilo, 1).deriveFont(estilo, tamanho);
    }

    /** Aplica o tema em todos os componentes Swing (chamar antes de criar qualquer janela). */
    public static void instalar() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignorada) {
            // mantém o padrão
        }
        FontUIResource base = new FontUIResource(fonte(Font.PLAIN, 13f));
        for (String k : new String[]{"Label.font", "Button.font", "TextField.font", "PasswordField.font", "TextArea.font",
            "ComboBox.font", "Table.font", "TableHeader.font", "Menu.font", "MenuItem.font", "CheckBox.font",
            "RadioButton.font", "OptionPane.messageFont", "OptionPane.buttonFont", "ToolTip.font", "List.font"}) {
            UIManager.put(k, base);
        }
        // Barra de menu e menus suspensos
        UIManager.put("MenuBar.background", MENU);
        UIManager.put("MenuBar.border", new EmptyBorder(0, 0, 0, 0));
        UIManager.put("Menu.background", MENU);
        UIManager.put("Menu.foreground", Color.WHITE);
        UIManager.put("Menu.selectionBackground", MENU_SELECAO);
        UIManager.put("Menu.selectionForeground", Color.WHITE);
        UIManager.put("Menu.border", new EmptyBorder(11, 16, 11, 16));
        UIManager.put("MenuItem.background", Color.WHITE);
        UIManager.put("MenuItem.foreground", TEXTO);
        UIManager.put("MenuItem.selectionBackground", PRIMARIA_CLARA);
        UIManager.put("MenuItem.selectionForeground", PRIMARIA);
        UIManager.put("MenuItem.border", new EmptyBorder(9, 16, 9, 28));
        UIManager.put("PopupMenu.background", Color.WHITE);
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(BORDA));
        // Janelas de mensagem
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("OptionPane.border", new EmptyBorder(14, 14, 10, 14));
        UIManager.put("Panel.background", Color.WHITE);
        // Seleção de texto, dicas e barras de rolagem
        UIManager.put("TextField.selectionBackground", new Color(0xD9CFFF));
        UIManager.put("TextField.selectionForeground", TEXTO);
        UIManager.put("TextArea.selectionBackground", new Color(0xD9CFFF));
        UIManager.put("TextArea.selectionForeground", TEXTO);
        UIManager.put("PasswordField.selectionBackground", new Color(0xD9CFFF));
        UIManager.put("PasswordField.selectionForeground", TEXTO);
        UIManager.put("ToolTip.background", MENU);
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("ToolTip.border", new EmptyBorder(6, 10, 6, 10));
        UIManager.put("ScrollBarUI", ScrollBarModerna.class.getName());
    }
}

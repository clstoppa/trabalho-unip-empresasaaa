package view;

import controller.ResumoController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import model.ResumoModel;
import util.Formato;
import util.NegocioException;
import util.Sessao;
import view.ui.Campo;
import view.ui.Card;
import view.ui.Dialogos;
import view.ui.Tema;

/** Janela principal: barra de menus Cadastros / Movimentos / Sair e área de conteúdo trocável. */
public class Menu extends JFrame {

    private static final String INICIO = "inicio";

    private final CardLayout paginas = new CardLayout();
    private final JPanel conteudo = new JPanel(paginas);
    private final Map<String, CadastroBase> telas = new HashMap<>();
    private final JPanel inicio = new JPanel(new BorderLayout());

    public Menu() {
        super("EMPRESA X - Gerenciamento de Compra e Venda");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                encerrar();
            }
        });
        setMinimumSize(new Dimension(1000, 640));
        setSize(1200, 760);
        setLocationRelativeTo(null);

        setJMenuBar(criarBarra());
        getContentPane().setLayout(new BorderLayout());

        conteudo.setBackground(Tema.FUNDO);
        conteudo.setBorder(new EmptyBorder(24, 28, 16, 28));
        inicio.setOpaque(false);
        conteudo.add(inicio, INICIO);
        getContentPane().add(conteudo, BorderLayout.CENTER);
        getContentPane().add(criarStatus(), BorderLayout.SOUTH);
        mostrarInicio();
    }

    // ---------- barra de menus ----------
    private JMenuBar criarBarra() {
        JMenuBar barra = new JMenuBar();
        barra.setBackground(Tema.MENU);
        barra.setBorder(new EmptyBorder(2, 10, 2, 10));

        JLabel marca = new JLabel("EMPRESA X");
        marca.setFont(Tema.fonte(Font.BOLD, 15f));
        marca.setForeground(Tema.ACENTO);
        marca.setBorder(new EmptyBorder(0, 6, 0, 22));
        barra.add(marca);

        JMenu cadastros = menu("Cadastros");
        cadastros.add(item("Cliente", "cliente", ClientePanel::new, "ctrl 1"));
        cadastros.add(item("Fornecedor", "fornecedor", FornecedorPanel::new, "ctrl 2"));
        cadastros.add(item("Produto", "produto", ProdutoPanel::new, "ctrl 3"));
        cadastros.add(item("Forma de Pagamento", "forma", FormaPagtoPanel::new, "ctrl 4"));
        cadastros.add(item("Usuário", "usuario", UsuarioPanel::new, "ctrl 5"));

        JMenu movimentos = menu("Movimentos");
        movimentos.add(item("Venda", "venda", VendaPanel::new, "ctrl 6"));
        movimentos.add(item("Compra", "compra", CompraPanel::new, "ctrl 7"));

        JMenu sair = menu("Sair");
        JMenuItem trocar = new JMenuItem("Trocar de usuário");
        trocar.addActionListener(e -> trocarUsuario());
        JMenuItem encerrar = new JMenuItem("Encerrar sistema");
        encerrar.addActionListener(e -> encerrar());
        sair.add(trocar);
        sair.addSeparator();
        sair.add(encerrar);

        JMenu inicioMenu = menu("Início");
        inicioMenu.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                inicioMenu.setSelected(false);
                mostrarInicio();
            }

            @Override
            public void menuDeselected(javax.swing.event.MenuEvent e) {
            }

            @Override
            public void menuCanceled(javax.swing.event.MenuEvent e) {
            }
        });

        barra.add(inicioMenu);
        barra.add(cadastros);
        barra.add(movimentos);
        barra.add(sair);
        return barra;
    }

    private static JMenu menu(String texto) {
        JMenu m = new JMenu(texto);
        m.setForeground(Color.WHITE);
        m.setFont(Tema.fonte(Font.BOLD, 14f));
        m.setOpaque(false);
        return m;
    }

    private JMenuItem item(String texto, final String chave, final Supplier<CadastroBase> fabrica, String atalho) {
        JMenuItem it = new JMenuItem(texto);
        it.setAccelerator(KeyStroke.getKeyStroke(atalho));
        it.addActionListener(e -> abrir(chave, fabrica));
        return it;
    }

    private void abrir(String chave, Supplier<CadastroBase> fabrica) {
        CadastroBase tela = telas.get(chave);
        if (tela == null) {
            tela = fabrica.get();
            telas.put(chave, tela);
            conteudo.add(tela, chave);
        }
        tela.aoExibir();
        paginas.show(conteudo, chave);
    }

    // ---------- início ----------
    private void mostrarInicio() {
        inicio.removeAll();

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        String nome = Sessao.getUsuario() == null ? "" : Sessao.getUsuario().getNome().split(" ")[0];
        titulos.add(Campo.titulo("Olá, " + nome + " \uD83D\uDC4B", 28f));
        titulos.add(Box.createVerticalStrut(4));
        titulos.add(Campo.subtitulo("Resumo da operação. Use o menu acima para cadastrar ou lançar movimentos."));
        inicio.add(titulos, BorderLayout.NORTH);

        JPanel grade = new JPanel(new GridLayout(0, 3, 18, 18));
        grade.setOpaque(false);
        try {
            ResumoModel r = new ResumoController().carregar();
            grade.add(cartao("Total vendido", Formato.moeda(r.getTotalVendido()), r.getVendas() + " vendas registradas", Tema.PRIMARIA));
            grade.add(cartao("Total comprado", Formato.moeda(r.getTotalComprado()), "Entradas de fornecedores", Tema.ACENTO));
            grade.add(cartao("Estoque baixo", String.valueOf(r.getEstoqueBaixo()), "Produtos abaixo do mínimo",
                    r.getEstoqueBaixo() > 0 ? Tema.PERIGO : Tema.SUCESSO));
            grade.add(cartao("Clientes", String.valueOf(r.getClientes()), "Cadastrados", Tema.SUCESSO));
            grade.add(cartao("Fornecedores", String.valueOf(r.getFornecedores()), "Cadastrados", Tema.SUCESSO));
            grade.add(cartao("Produtos", String.valueOf(r.getProdutos()), "No catálogo", Tema.PRIMARIA));
        } catch (NegocioException e) {
            Dialogos.erro(this, e.getMessage());
        }
        JPanel meio = new JPanel(new BorderLayout());
        meio.setOpaque(false);
        meio.setBorder(new EmptyBorder(24, 0, 0, 0));
        meio.add(grade, BorderLayout.NORTH);
        inicio.add(meio, BorderLayout.CENTER);
        inicio.revalidate();
        inicio.repaint();
    }

    private JPanel cartao(String titulo, String valor, String legenda, Color cor) {
        Card c = new Card(new BorderLayout(0, 6), 20);
        JPanel faixa = new JPanel();
        faixa.setBackground(cor);
        faixa.setPreferredSize(new Dimension(5, 10));
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Campo.rotulo(titulo.toUpperCase()));
        textos.add(Box.createVerticalStrut(8));
        textos.add(Campo.rotuloCor(valor, Tema.TEXTO, 26f, Font.BOLD));
        textos.add(Box.createVerticalStrut(4));
        textos.add(Campo.subtitulo(legenda));
        c.add(textos, BorderLayout.CENTER);
        c.add(faixa, BorderLayout.WEST);
        return c;
    }

    // ---------- barra de status ----------
    private final JLabel status = new JLabel(" ");

    private JPanel criarStatus() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Tema.MENU);
        p.setBorder(new EmptyBorder(6, 18, 6, 18));
        JLabel usuario = new JLabel("Usuário: " + (Sessao.getUsuario() == null ? "-" : Sessao.getUsuario().getNome()));
        usuario.setForeground(new Color(0xCFCBE6));
        usuario.setFont(Tema.fonte(Font.PLAIN, 12f));
        JLabel data = new JLabel(Formato.data(LocalDate.now()));
        data.setForeground(new Color(0xCFCBE6));
        data.setFont(Tema.fonte(Font.PLAIN, 12f));
        p.add(usuario, BorderLayout.WEST);
        p.add(data, BorderLayout.EAST);
        return p;
    }

    // ---------- sair ----------
    private void trocarUsuario() {
        if (Dialogos.confirmar(this, "Deseja sair e entrar com outro usuário?")) {
            Sessao.setUsuario(null);
            dispose();
            new LoginView().setVisible(true);
        }
    }

    private void encerrar() {
        if (Dialogos.confirmar(this, "Deseja realmente encerrar o sistema?")) {
            System.exit(0);
        }
    }
}

package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import util.NegocioException;
import view.ui.Botao;
import view.ui.Campo;
import view.ui.CampoTexto;
import view.ui.Card;
import view.ui.Dialogos;
import view.ui.Tabelas;
import view.ui.Tema;

/**
 * Esqueleto de toda tela de cadastro: cabeçalho, página de LISTA (busca + grade) e página de
 * FORMULÁRIO (Salvar/Cancelar). As telas concretas só informam colunas, campos e o que fazer
 * ao gravar/excluir, sempre chamando o Controller.
 */
public abstract class CadastroBase extends JPanel {

    private static final String LISTA = "lista";
    private static final String FORM = "form";

    private final CardLayout paginas = new CardLayout();
    private final JPanel conteudo = new JPanel(paginas);
    private final JLabel lblContagem = Campo.subtitulo(" ");
    private final JLabel lblStatus = Campo.rotuloCor(" ", Tema.SUCESSO, 13f, java.awt.Font.BOLD);
    private final JLabel lblFormTitulo = Campo.titulo(" ", 18f);
    private final Timer limpaStatus = new Timer(4000, e -> lblStatus.setText(" "));
    private final Timer atrasoBusca = new Timer(250, e -> recarregar());
    private DefaultTableModel modelo;
    protected JTable tabela;
    private CampoTexto busca;
    private Botao btEditar;
    private Botao btExcluir;
    private int idEdicao;
    private boolean emFormulario;

    protected CadastroBase() {
        super(new BorderLayout(0, 20));
        setBackground(Tema.FUNDO);
        setBorder(new EmptyBorder(26, 32, 26, 32));
        limpaStatus.setRepeats(false);
        atrasoBusca.setRepeats(false);
    }

    // ---------- contrato das telas concretas ----------
    protected abstract String titulo();

    protected abstract String singular();

    protected abstract String[] colunas();

    /** Linhas da grade. A primeira coluna é SEMPRE o código (Integer). */
    protected abstract List<Object[]> listar(String filtro) throws NegocioException;

    protected abstract JComponent criarFormulario();

    protected abstract void novoRegistro();

    protected abstract void editarRegistro(int codigo) throws NegocioException;

    protected abstract void gravarRegistro() throws NegocioException;

    protected abstract void excluirRegistro(int codigo) throws NegocioException;

    // ---------- pontos opcionais ----------
    protected String textoNovo() {
        return "Novo " + singular();
    }

    protected String textoEditar() {
        return "Editar " + singular();
    }

    protected String descricao() {
        return "Consulte, inclua, altere e exclua registros.";
    }

    protected String placeholderBusca() {
        return "Buscar...";
    }

    protected void configurarTabela(JTable t) {
    }

    /** Recarrega combos e listas auxiliares (chamado sempre que a tela é exibida). */
    protected void recarregarListas() throws NegocioException {
    }

    /** Código do registro em edição (0 = registro novo). */
    protected final int idEdicao() {
        return idEdicao;
    }

    // ---------- montagem ----------
    /** Deve ser chamado no fim do construtor da tela concreta. */
    protected final void montar() {
        JPanel titulos = new JPanel();
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.setOpaque(false);
        titulos.add(Campo.titulo(titulo(), 26f));
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(Campo.subtitulo(descricao()));

        Botao btNovo = new Botao("+   " + textoNovo(), Botao.Tipo.PRIMARIO);
        btNovo.addActionListener(e -> novo());
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        direita.setOpaque(false);
        direita.add(lblStatus);
        direita.add(btNovo);
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(titulos, BorderLayout.WEST);
        topo.add(direita, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        conteudo.setOpaque(false);
        conteudo.add(criarPaginaLista(), LISTA);
        conteudo.add(criarPaginaForm(), FORM);
        add(conteudo, BorderLayout.CENTER);

        // Ctrl+S salva quando o formulário está aberto
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control S"), "salvar");
        getActionMap().put("salvar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isShowing() && emFormulario) {
                    salvar();
                }
            }
        });
    }

    private JComponent criarPaginaLista() {
        Card card = new Card(new BorderLayout(0, 14));

        busca = Campo.texto(placeholderBusca());
        busca.setPreferredSize(new Dimension(340, 40));
        busca.getDocument().addDocumentListener(new SimpleDocListener(() -> atrasoBusca.restart()));

        btEditar = new Botao("Editar", Botao.Tipo.SECUNDARIO);
        btExcluir = new Botao("Excluir", Botao.Tipo.PERIGO);
        btEditar.addActionListener(e -> editar());
        btExcluir.addActionListener(e -> excluir());
        btEditar.setEnabled(false);
        btExcluir.setEnabled(false);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acoes.setOpaque(false);
        acoes.add(btEditar);
        acoes.add(btExcluir);
        JPanel esquerda = new JPanel(new BorderLayout());
        esquerda.setOpaque(false);
        esquerda.add(busca, BorderLayout.WEST);
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.add(esquerda, BorderLayout.WEST);
        barra.add(acoes, BorderLayout.EAST);

        modelo = Tabelas.modelo(colunas());
        tabela = new JTable(modelo);
        tabela.setAutoCreateRowSorter(true);
        JScrollPane sp = Tabelas.estilizar(tabela);
        configurarTabela(tabela);
        tabela.getColumnModel().getColumn(0).setMaxWidth(90);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            boolean sel = tabela.getSelectedRow() >= 0;
            btEditar.setEnabled(sel);
            btExcluir.setEnabled(sel);
        });
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabela.getSelectedRow() >= 0) {
                    editar();
                }
            }
        });

        card.add(barra, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        card.add(lblContagem, BorderLayout.SOUTH);
        return card;
    }

    private JComponent criarPaginaForm() {
        Card card = new Card(new BorderLayout(0, 16), 24);
        card.add(lblFormTitulo, BorderLayout.NORTH);

        JPanel embrulho = new JPanel(new BorderLayout());
        embrulho.setOpaque(false);
        embrulho.add(criarFormulario(), BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(embrulho, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rolagem.setBorder(null);
        rolagem.setOpaque(false);
        rolagem.getViewport().setOpaque(false);
        rolagem.getVerticalScrollBar().setUnitIncrement(18);
        card.add(rolagem, BorderLayout.CENTER);

        Botao cancelar = new Botao("Cancelar", Botao.Tipo.SECUNDARIO);
        Botao salvar = new Botao("Salvar", Botao.Tipo.PRIMARIO);
        cancelar.addActionListener(e -> voltar());
        salvar.addActionListener(e -> salvar());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rodape.setOpaque(false);
        rodape.add(cancelar);
        rodape.add(salvar);
        card.add(rodape, BorderLayout.SOUTH);
        return card;
    }

    // ---------- fluxo ----------
    /** Chamado pelo Menu toda vez que a tela é mostrada. */
    public final void aoExibir() {
        try {
            recarregarListas();
        } catch (NegocioException e) {
            Dialogos.erro(this, e.getMessage());
        }
        paginas.show(conteudo, LISTA);
        recarregar();
    }

    protected final void recarregar() {
        try {
            List<Object[]> linhas = listar(busca.getText().trim());
            modelo.setRowCount(0);
            for (Object[] l : linhas) {
                modelo.addRow(l);
            }
            lblContagem.setText(linhas.size() + (linhas.size() == 1 ? " registro" : " registros"));
        } catch (NegocioException e) {
            Dialogos.erro(this, e.getMessage());
        }
    }

    protected final void mostrarStatus(String mensagem) {
        lblStatus.setText(mensagem);
        limpaStatus.restart();
    }

    private void novo() {
        idEdicao = 0;
        novoRegistro();
        lblFormTitulo.setText(textoNovo());
        paginas.show(conteudo, FORM);
        emFormulario = true;
    }

    private void editar() {
        int visual = tabela.getSelectedRow();
        if (visual < 0) {
            Dialogos.aviso(this, "Selecione um registro na lista.");
            return;
        }
        int id = ((Number) modelo.getValueAt(tabela.convertRowIndexToModel(visual), 0)).intValue();
        try {
            idEdicao = id;
            editarRegistro(id);
            lblFormTitulo.setText(textoEditar() + "  ·  #" + id);
            paginas.show(conteudo, FORM);
            emFormulario = true;
        } catch (NegocioException e) {
            Dialogos.erro(this, e.getMessage());
        }
    }

    private void salvar() {
        try {
            gravarRegistro();
            String s = singular();
            mostrarStatus(Character.toUpperCase(s.charAt(0)) + s.substring(1) + " salvo(a) com sucesso");
            voltar();
        } catch (NegocioException e) {
            Dialogos.aviso(this, e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            Dialogos.erro(this, "Erro inesperado: " + e);
        }
    }

    private void excluir() {
        int visual = tabela.getSelectedRow();
        if (visual < 0) {
            return;
        }
        int id = ((Number) modelo.getValueAt(tabela.convertRowIndexToModel(visual), 0)).intValue();
        if (!Dialogos.confirmar(this, "Excluir o registro #" + id + "?\nEsta ação não pode ser desfeita.")) {
            return;
        }
        try {
            excluirRegistro(id);
            mostrarStatus("Registro excluído");
            recarregar();
        } catch (NegocioException e) {
            Dialogos.aviso(this, e.getMessage());
        }
    }

    private void voltar() {
        try {
            recarregarListas();
        } catch (NegocioException e) {
            Dialogos.erro(this, e.getMessage());
        }
        paginas.show(conteudo, LISTA);
        emFormulario = false;
        recarregar();
    }

    /** Leva o cursor ao campo indicado assim que o formulário aparece. */
    protected static void focar(JComponent c) {
        SwingUtilities.invokeLater(c::requestFocusInWindow);
    }
}

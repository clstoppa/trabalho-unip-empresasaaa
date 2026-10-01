package view;

import controller.CompraController;
import controller.FornecedorController;
import controller.ProdutoController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.CompraModel;
import model.CompraProdutoModel;
import model.FornecedorModel;
import model.ProdutoModel;
import util.Formato;
import util.NegocioException;
import util.Sessao;
import view.ui.Botao;
import view.ui.Campo;
import view.ui.CampoArea;
import view.ui.CampoTexto;
import view.ui.Dialogos;
import view.ui.FormGrid;
import view.ui.Mascara;
import view.ui.Opcao;
import view.ui.Tabelas;
import view.ui.Tema;

public class CompraPanel extends CadastroBase {

    private final CompraController controller = new CompraController();
    private final FornecedorController fornecedores = new FornecedorController();
    private final ProdutoController produtos = new ProdutoController();

    private final FormGrid form = new FormGrid();
    private final JComboBox<Opcao> cmbFornecedor = Campo.combo();
    private final CampoTexto txtEmissao = Campo.texto("dd/mm/aaaa");
    private final CampoTexto txtEntrada = Campo.texto("dd/mm/aaaa");
    private final CampoTexto txtUsuario = Campo.somenteLeitura();

    private final JComboBox<Opcao> cmbProduto = Campo.combo();
    private final CampoTexto txtQtde = Campo.texto("1");
    private final CampoTexto txtPreco = Campo.texto("0,00");
    private final CampoTexto txtDescItem = Campo.texto("0,00");
    private final DefaultTableModel modeloItens = Tabelas.modelo("Produto", "Qtde", "Preço de compra", "Desconto", "Total");
    private final JTable tabItens = new JTable(modeloItens);

    private final CampoTexto txtDescGeral = Campo.texto("0,00");
    private final CampoArea txtObs = Campo.area(60);
    private final JLabel lblValor = Campo.rotuloCor("R$ 0,00", Tema.TEXTO, 15f, java.awt.Font.BOLD);
    private final JLabel lblTotal = Campo.rotuloCor("R$ 0,00", Tema.PRIMARIA, 20f, java.awt.Font.BOLD);

    private final List<CompraProdutoModel> itens = new ArrayList<>();
    private final Map<Integer, ProdutoModel> mapaProdutos = new HashMap<>();

    public CompraPanel() {
        Mascara.instalar(txtEmissao, Mascara.DATA);
        Mascara.instalar(txtEntrada, Mascara.DATA);
        Tabelas.moeda(tabItens, 2, 3, 4);
        for (CampoTexto t : new CampoTexto[]{txtQtde, txtPreco, txtDescItem, txtDescGeral}) {
            t.setHorizontalAlignment(CampoTexto.RIGHT);
        }
        form.secao("Dados da compra");
        form.campo("Fornecedor *", cmbFornecedor, 6);
        form.campo("Emissão *", txtEmissao, 2);
        form.campo("Entrada", txtEntrada, 2);
        form.campo("Registrada por", txtUsuario, 2);
        form.novaLinha();

        form.secao("Produtos");
        form.campo("Produto", cmbProduto, 5);
        form.campo("Qtde", txtQtde, 2);
        form.campo("Preço de compra (R$)", txtPreco, 2);
        form.campo("Desconto (R$)", txtDescItem, 2);
        Botao btAdd = new Botao("Adicionar", Botao.Tipo.PRIMARIO);
        form.campo(" ", btAdd, 1);
        form.novaLinha();

        JScrollPane sp = Tabelas.estilizar(tabItens);
        sp.setPreferredSize(new Dimension(100, 150));
        Botao btRem = new Botao("Remover selecionado", Botao.Tipo.LINK);
        btRem.addActionListener(e -> removerItem());
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(sp, BorderLayout.CENTER);
        JPanel dir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        dir.setOpaque(false);
        dir.add(btRem);
        p.add(dir, BorderLayout.SOUTH);
        form.linhaInteira(p);
        form.novaLinha();

        form.secao("Fechamento");
        form.campo("Desconto geral (R$)", txtDescGeral, 4);
        form.campo("Valor dos produtos", lblValor, 4);
        form.campo("Total da compra", lblTotal, 4);
        form.novaLinha();
        form.campo("Observações", txtObs, 12);

        cmbProduto.addActionListener(e -> sugerirPreco());
        btAdd.addActionListener(e -> adicionarItem());
        txtDescGeral.getDocument().addDocumentListener(new SimpleDocListener(this::atualizarTotais));
        montar();
    }

    @Override
    protected String titulo() {
        return "Compras";
    }

    @Override
    protected String singular() {
        return "compra";
    }

    @Override
    protected String descricao() {
        return "Registre as compras de fornecedores. O estoque é acrescido automaticamente.";
    }

    @Override
    protected String textoNovo() {
        return "Nova compra";
    }

    @Override
    protected String placeholderBusca() {
        return "Buscar por fornecedor ou número...";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Nº", "Emissão", "Fornecedor", "Entrada", "Total"};
    }

    @Override
    protected void configurarTabela(JTable t) {
        Tabelas.moeda(t, 4);
        Tabelas.largura(t, 2, 300);
    }

    @Override
    protected List<Object[]> listar(String filtro) throws NegocioException {
        List<Object[]> linhas = new ArrayList<>();
        for (CompraModel c : controller.consultar(filtro)) {
            linhas.add(new Object[]{c.getCodigo(), Formato.data(c.getEmissao()), c.getFornecedorNome(),
                c.getDtEntrada() == null ? "" : Formato.data(c.getDtEntrada()), c.getTotal()});
        }
        return linhas;
    }

    @Override
    protected void recarregarListas() throws NegocioException {
        List<Opcao> fo = new ArrayList<>();
        for (FornecedorModel f : fornecedores.ativos()) {
            fo.add(new Opcao(f.getForCodigo(), f.getNome()));
        }
        Opcao.preencher(cmbFornecedor, "Selecione o fornecedor...", fo);
        mapaProdutos.clear();
        List<Opcao> pr = new ArrayList<>();
        for (ProdutoModel p : produtos.ativos()) {
            mapaProdutos.put(p.getCodigo(), p);
            pr.add(new Opcao(p.getCodigo(), p.getNome() + "  (estoque " + Formato.quantidade(p.getEstoque()) + ")"));
        }
        Opcao.preencher(cmbProduto, "Selecione o produto...", pr);
    }

    @Override
    protected JComponent criarFormulario() {
        return form;
    }

    @Override
    protected void novoRegistro() {
        Opcao.selecionar(cmbFornecedor, 0);
        txtEmissao.setText(Formato.data(LocalDate.now()));
        txtEntrada.setText(Formato.data(LocalDate.now()));
        txtUsuario.setText(Sessao.getUsuario() == null ? "" : Sessao.getUsuario().getNome());
        itens.clear();
        txtDescGeral.setText("");
        txtObs.setText("");
        limparEntradaItem();
        atualizarTabela();
        focar(cmbFornecedor);
    }

    @Override
    protected void editarRegistro(int codigo) throws NegocioException {
        CompraModel c = controller.buscar(codigo);
        if (c == null) {
            throw new NegocioException("Compra não encontrada.");
        }
        Opcao.selecionar(cmbFornecedor, c.getForCodigo());
        txtEmissao.setText(Formato.data(c.getEmissao()));
        txtEntrada.setText(c.getDtEntrada() == null ? "" : Formato.data(c.getDtEntrada()));
        txtUsuario.setText(c.getUsuarioNome());
        itens.clear();
        itens.addAll(c.getItens());
        txtDescGeral.setText(c.getDesconto() == 0 ? "" : Formato.decimal(c.getDesconto()));
        txtObs.setText(c.getObs());
        limparEntradaItem();
        atualizarTabela();
    }

    @Override
    protected void gravarRegistro() throws NegocioException {
        CompraModel c = idEdicao() == 0 ? new CompraModel() : controller.buscar(idEdicao());
        c.setForCodigo(Opcao.idDe(cmbFornecedor));
        c.setEmissao(Formato.data(txtEmissao.getText(), "Emissão"));
        c.setDtEntrada(txtEntrada.getText().replaceAll("\\D", "").isEmpty() ? null : Formato.data(txtEntrada.getText(), "Entrada"));
        if (idEdicao() == 0 && Sessao.getUsuario() != null) {
            c.setUsuCodigo(Sessao.getUsuario().getCodigo());
        }
        c.setDesconto(lerOpcional(txtDescGeral, "Desconto geral"));
        c.setObs(txtObs.getText().trim());
        c.setItens(new ArrayList<>(itens));
        controller.gravar(c);
    }

    @Override
    protected void excluirRegistro(int codigo) throws NegocioException {
        controller.excluir(codigo);
    }

    // ---------- itens ----------
    private static double lerOpcional(CampoTexto t, String nome) throws NegocioException {
        return t.getText().trim().isEmpty() ? 0 : Formato.numero(t.getText(), nome);
    }

    private void sugerirPreco() {
        ProdutoModel p = mapaProdutos.get(Opcao.idDe(cmbProduto));
        if (p != null) {
            txtPreco.setText(Formato.decimal(p.getCusto()));
            if (txtQtde.getText().trim().isEmpty()) {
                txtQtde.setText("1");
            }
        }
    }

    private void limparEntradaItem() {
        Opcao.selecionar(cmbProduto, 0);
        txtQtde.setText("1");
        txtPreco.setText("");
        txtDescItem.setText("");
    }

    private void adicionarItem() {
        try {
            ProdutoModel p = mapaProdutos.get(Opcao.idDe(cmbProduto));
            if (p == null) {
                throw new NegocioException("Selecione o produto.");
            }
            CompraProdutoModel i = new CompraProdutoModel();
            i.setProCodigo(p.getCodigo());
            i.setProNome(p.getNome());
            i.setQtde(Formato.numero(txtQtde.getText(), "Quantidade"));
            i.setPreco(Formato.numero(txtPreco.getText(), "Preço de compra"));
            i.setDesconto(lerOpcional(txtDescItem, "Desconto do item"));
            if (i.getQtde() <= 0) {
                throw new NegocioException("A quantidade deve ser maior que zero.");
            }
            if (i.getPreco() < 0 || i.getDesconto() < 0) {
                throw new NegocioException("Preço e desconto não podem ser negativos.");
            }
            if (i.getDesconto() > i.getQtde() * i.getPreco() + 0.005) {
                throw new NegocioException("O desconto excede o valor do item.");
            }
            i.setTotal(CompraController.totalItem(i.getQtde(), i.getPreco(), i.getDesconto()));
            itens.add(i);
            limparEntradaItem();
            atualizarTabela();
            focar(cmbProduto);
        } catch (NegocioException e) {
            Dialogos.aviso(this, e.getMessage());
        }
    }

    private void removerItem() {
        int l = tabItens.getSelectedRow();
        if (l < 0) {
            Dialogos.aviso(this, "Selecione um produto na lista para remover.");
            return;
        }
        itens.remove(tabItens.convertRowIndexToModel(l));
        atualizarTabela();
    }

    private void atualizarTabela() {
        modeloItens.setRowCount(0);
        for (CompraProdutoModel i : itens) {
            modeloItens.addRow(new Object[]{i.getProNome(), Formato.quantidade(i.getQtde()), i.getPreco(), i.getDesconto(), i.getTotal()});
        }
        atualizarTotais();
    }

    private void atualizarTotais() {
        CompraModel c = new CompraModel();
        c.setItens(itens);
        try {
            c.setDesconto(lerOpcional(txtDescGeral, "Desconto geral"));
        } catch (NegocioException e) {
            c.setDesconto(0);
        }
        CompraController.calcular(c);
        lblValor.setText(Formato.moeda(c.getValor()));
        lblTotal.setText(Formato.moeda(c.getTotal()));
    }
}

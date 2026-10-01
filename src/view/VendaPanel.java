package view;

import controller.ClienteController;
import controller.FormaPagtoController;
import controller.ProdutoController;
import controller.VendaController;
import java.awt.Dimension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.ClienteModel;
import model.FormaPagtoModel;
import model.ProdutoModel;
import model.VendaModel;
import model.VendaPagtoModel;
import model.VendaProdutoModel;
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

public class VendaPanel extends CadastroBase {

    private final VendaController controller = new VendaController();
    private final ClienteController clientes = new ClienteController();
    private final ProdutoController produtos = new ProdutoController();
    private final FormaPagtoController formas = new FormaPagtoController();

    private final FormGrid form = new FormGrid();
    private final JComboBox<Opcao> cmbCliente = Campo.combo();
    private final CampoTexto txtData = Campo.texto("dd/mm/aaaa");
    private final CampoTexto txtVendedor = Campo.somenteLeitura();

    private final JComboBox<Opcao> cmbProduto = Campo.combo();
    private final CampoTexto txtQtde = Campo.texto("1");
    private final CampoTexto txtPreco = Campo.texto("0,00");
    private final CampoTexto txtDescItem = Campo.texto("0,00");
    private final DefaultTableModel modeloItens = Tabelas.modelo("Produto", "Qtde", "Preço", "Desconto", "Total");
    private final JTable tabItens = new JTable(modeloItens);

    private final JComboBox<Opcao> cmbForma = Campo.combo();
    private final CampoTexto txtValorPagto = Campo.texto("0,00");
    private final DefaultTableModel modeloPagtos = Tabelas.modelo("Forma de pagamento", "Valor");
    private final JTable tabPagtos = new JTable(modeloPagtos);

    private final CampoTexto txtDescGeral = Campo.texto("0,00");
    private final CampoArea txtObs = Campo.area(60);
    private final JLabel lblValor = Campo.rotuloCor("R$ 0,00", Tema.TEXTO, 15f, java.awt.Font.BOLD);
    private final JLabel lblTotal = Campo.rotuloCor("R$ 0,00", Tema.PRIMARIA, 20f, java.awt.Font.BOLD);
    private final JLabel lblPago = Campo.rotuloCor("R$ 0,00", Tema.TEXTO, 15f, java.awt.Font.BOLD);
    private final JLabel lblRestante = Campo.rotuloCor("R$ 0,00", Tema.ACENTO, 15f, java.awt.Font.BOLD);

    private final List<VendaProdutoModel> itens = new ArrayList<>();
    private final List<VendaPagtoModel> pagtos = new ArrayList<>();
    private final Map<Integer, ProdutoModel> mapaProdutos = new HashMap<>();
    private final Map<Integer, String> nomesFormas = new HashMap<>();

    public VendaPanel() {
        Mascara.instalar(txtData, Mascara.DATA);
        Tabelas.moeda(tabItens, 2, 3, 4);
        Tabelas.moeda(tabPagtos, 1);
        for (CampoTexto t : new CampoTexto[]{txtQtde, txtPreco, txtDescItem, txtValorPagto, txtDescGeral}) {
            t.setHorizontalAlignment(CampoTexto.RIGHT);
        }
        form.secao("Dados da venda");
        form.campo("Cliente *", cmbCliente, 6);
        form.campo("Data *", txtData, 3);
        form.campo("Vendedor", txtVendedor, 3);
        form.novaLinha();

        form.secao("Produtos");
        form.campo("Produto", cmbProduto, 5);
        form.campo("Qtde", txtQtde, 2);
        form.campo("Preço (R$)", txtPreco, 2);
        form.campo("Desconto (R$)", txtDescItem, 2);
        Botao btAddItem = new Botao("Adicionar", Botao.Tipo.PRIMARIO);
        form.campo(" ", btAddItem, 1);
        form.novaLinha();
        form.linhaInteira(tabelaComRemocao(tabItens, () -> removerItem()));
        form.novaLinha();

        form.secao("Pagamento");
        form.campo("Forma de pagamento", cmbForma, 5);
        form.campo("Valor (R$)", txtValorPagto, 3);
        Botao btAddPagto = new Botao("Adicionar", Botao.Tipo.PRIMARIO);
        form.campo(" ", btAddPagto, 2);
        form.novaLinha();
        form.linhaInteira(tabelaComRemocao(tabPagtos, () -> removerPagto()));
        form.novaLinha();

        form.secao("Fechamento");
        form.campo("Desconto geral (R$)", txtDescGeral, 3);
        form.campo("Valor dos produtos", lblValor, 3);
        form.campo("Total da venda", lblTotal, 3);
        form.campo("Total pago / Restante", resumoPago(), 3);
        form.novaLinha();
        form.campo("Observações", txtObs, 12);

        cmbProduto.addActionListener(e -> sugerirPreco());
        btAddItem.addActionListener(e -> adicionarItem());
        btAddPagto.addActionListener(e -> adicionarPagto());
        txtDescGeral.getDocument().addDocumentListener(new SimpleDocListener(this::atualizarTotais));
        montar();
    }

    private JComponent resumoPago() {
        javax.swing.JPanel p = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.add(lblPago);
        p.add(Campo.rotulo("/"));
        p.add(lblRestante);
        return p;
    }

    private JComponent tabelaComRemocao(JTable t, Runnable remover) {
        JScrollPane sp = Tabelas.estilizar(t);
        sp.setPreferredSize(new Dimension(100, 130));
        Botao bt = new Botao("Remover selecionado", Botao.Tipo.LINK);
        bt.addActionListener(e -> remover.run());
        javax.swing.JPanel p = new javax.swing.JPanel(new java.awt.BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(sp, java.awt.BorderLayout.CENTER);
        javax.swing.JPanel dir = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        dir.setOpaque(false);
        dir.add(bt);
        p.add(dir, java.awt.BorderLayout.SOUTH);
        return p;
    }

    // ---------- CadastroBase ----------
    @Override
    protected String titulo() {
        return "Vendas";
    }

    @Override
    protected String singular() {
        return "venda";
    }

    @Override
    protected String descricao() {
        return "Registre vendas com produtos e formas de pagamento. O estoque é baixado automaticamente.";
    }

    @Override
    protected String textoNovo() {
        return "Nova venda";
    }

    @Override
    protected String placeholderBusca() {
        return "Buscar por cliente ou número...";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Nº", "Data", "Cliente", "Vendedor", "Total"};
    }

    @Override
    protected void configurarTabela(JTable t) {
        Tabelas.moeda(t, 4);
        Tabelas.largura(t, 2, 300);
    }

    @Override
    protected List<Object[]> listar(String filtro) throws NegocioException {
        List<Object[]> linhas = new ArrayList<>();
        for (VendaModel v : controller.consultar(filtro)) {
            linhas.add(new Object[]{v.getCodigo(), Formato.data(v.getData()), v.getClienteNome(), v.getUsuarioNome(), v.getTotal()});
        }
        return linhas;
    }

    @Override
    protected void recarregarListas() throws NegocioException {
        List<Opcao> cl = new ArrayList<>();
        for (ClienteModel c : clientes.ativos()) {
            cl.add(new Opcao(c.getCliCodigo(), c.getNome()));
        }
        Opcao.preencher(cmbCliente, "Selecione o cliente...", cl);

        mapaProdutos.clear();
        List<Opcao> pr = new ArrayList<>();
        for (ProdutoModel p : produtos.ativos()) {
            mapaProdutos.put(p.getCodigo(), p);
            pr.add(new Opcao(p.getCodigo(), p.getNome() + "  (estoque " + Formato.quantidade(p.getEstoque()) + ")"));
        }
        Opcao.preencher(cmbProduto, "Selecione o produto...", pr);

        nomesFormas.clear();
        List<Opcao> fp = new ArrayList<>();
        for (FormaPagtoModel f : formas.ativas()) {
            nomesFormas.put(f.getCodigo(), f.getNome());
            fp.add(new Opcao(f.getCodigo(), f.getNome()));
        }
        Opcao.preencher(cmbForma, "Selecione...", fp);
    }

    @Override
    protected JComponent criarFormulario() {
        return form;
    }

    @Override
    protected void novoRegistro() {
        Opcao.selecionar(cmbCliente, 0);
        txtData.setText(Formato.data(LocalDate.now()));
        txtVendedor.setText(Sessao.getUsuario() == null ? "" : Sessao.getUsuario().getNome());
        itens.clear();
        pagtos.clear();
        txtDescGeral.setText("");
        txtObs.setText("");
        limparEntradaItem();
        txtValorPagto.setText("");
        Opcao.selecionar(cmbForma, 0);
        atualizarTabelas();
        focar(cmbCliente);
    }

    @Override
    protected void editarRegistro(int codigo) throws NegocioException {
        VendaModel v = controller.buscar(codigo);
        if (v == null) {
            throw new NegocioException("Venda não encontrada.");
        }
        Opcao.selecionar(cmbCliente, v.getCliCodigo());
        txtData.setText(Formato.data(v.getData()));
        txtVendedor.setText(v.getUsuarioNome());
        itens.clear();
        itens.addAll(v.getItens());
        pagtos.clear();
        pagtos.addAll(v.getPagamentos());
        txtDescGeral.setText(v.getDesconto() == 0 ? "" : Formato.decimal(v.getDesconto()));
        txtObs.setText(v.getObs());
        limparEntradaItem();
        txtValorPagto.setText("");
        Opcao.selecionar(cmbForma, 0);
        atualizarTabelas();
    }

    @Override
    protected void gravarRegistro() throws NegocioException {
        VendaModel v = idEdicao() == 0 ? new VendaModel() : controller.buscar(idEdicao());
        v.setCliCodigo(Opcao.idDe(cmbCliente));
        v.setData(Formato.data(txtData.getText(), "Data"));
        if (idEdicao() == 0 && Sessao.getUsuario() != null) {
            v.setUsuCodigo(Sessao.getUsuario().getCodigo());
        }
        v.setDesconto(lerOpcional(txtDescGeral, "Desconto geral"));
        v.setObs(txtObs.getText().trim());
        v.setItens(new ArrayList<>(itens));
        v.setPagamentos(new ArrayList<>(pagtos));
        controller.gravar(v);
    }

    @Override
    protected void excluirRegistro(int codigo) throws NegocioException {
        controller.excluir(codigo);
    }

    // ---------- itens e pagamentos ----------
    private static double lerOpcional(CampoTexto t, String nome) throws NegocioException {
        return t.getText().trim().isEmpty() ? 0 : Formato.numero(t.getText(), nome);
    }

    private void sugerirPreco() {
        ProdutoModel p = mapaProdutos.get(Opcao.idDe(cmbProduto));
        if (p != null) {
            txtPreco.setText(Formato.decimal(p.getPreco()));
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
            VendaProdutoModel i = new VendaProdutoModel();
            i.setProCodigo(p.getCodigo());
            i.setProNome(p.getNome());
            i.setQtde(Formato.numero(txtQtde.getText(), "Quantidade"));
            i.setPreco(Formato.numero(txtPreco.getText(), "Preço"));
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
            double jaNaVenda = 0;
            for (VendaProdutoModel x : itens) {
                if (x.getProCodigo() == p.getCodigo()) {
                    jaNaVenda += x.getQtde();
                }
            }
            if (idEdicao() == 0 && jaNaVenda + i.getQtde() > p.getEstoque() + 0.0001) {
                throw new NegocioException("Estoque insuficiente de \"" + p.getNome() + "\" (disponível: "
                        + Formato.quantidade(p.getEstoque()) + ").");
            }
            i.setTotal(VendaController.totalItem(i.getQtde(), i.getPreco(), i.getDesconto()));
            itens.add(i);
            limparEntradaItem();
            atualizarTabelas();
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
        atualizarTabelas();
    }

    private void adicionarPagto() {
        try {
            int fpg = Opcao.idDe(cmbForma);
            if (fpg == 0) {
                throw new NegocioException("Selecione a forma de pagamento.");
            }
            double valor;
            if (txtValorPagto.getText().trim().isEmpty()) {
                valor = Formato.arredondar(totalAtual() - pagoAtual()); // sugere o restante
            } else {
                valor = Formato.numero(txtValorPagto.getText(), "Valor do pagamento");
            }
            if (valor <= 0) {
                throw new NegocioException("Informe um valor de pagamento maior que zero.");
            }
            VendaPagtoModel p = new VendaPagtoModel();
            p.setFpgCodigo(fpg);
            p.setFpgNome(nomesFormas.get(fpg));
            p.setValor(valor);
            pagtos.add(p);
            txtValorPagto.setText("");
            Opcao.selecionar(cmbForma, 0);
            atualizarTabelas();
        } catch (NegocioException e) {
            Dialogos.aviso(this, e.getMessage());
        }
    }

    private void removerPagto() {
        int l = tabPagtos.getSelectedRow();
        if (l < 0) {
            Dialogos.aviso(this, "Selecione um pagamento na lista para remover.");
            return;
        }
        pagtos.remove(tabPagtos.convertRowIndexToModel(l));
        atualizarTabelas();
    }

    private double descontoGeralAtual() {
        try {
            return lerOpcional(txtDescGeral, "Desconto geral");
        } catch (NegocioException e) {
            return 0;
        }
    }

    private double totalAtual() {
        VendaModel v = new VendaModel();
        v.setItens(itens);
        v.setDesconto(descontoGeralAtual());
        VendaController.calcular(v);
        return v.getTotal();
    }

    private double pagoAtual() {
        VendaModel v = new VendaModel();
        v.setPagamentos(pagtos);
        return VendaController.totalPago(v);
    }

    private void atualizarTabelas() {
        modeloItens.setRowCount(0);
        for (VendaProdutoModel i : itens) {
            modeloItens.addRow(new Object[]{i.getProNome(), Formato.quantidade(i.getQtde()), i.getPreco(), i.getDesconto(), i.getTotal()});
        }
        modeloPagtos.setRowCount(0);
        for (VendaPagtoModel p : pagtos) {
            modeloPagtos.addRow(new Object[]{p.getFpgNome(), p.getValor()});
        }
        atualizarTotais();
    }

    private void atualizarTotais() {
        VendaModel v = new VendaModel();
        v.setItens(itens);
        v.setDesconto(descontoGeralAtual());
        VendaController.calcular(v);
        double pago = pagoAtual();
        lblValor.setText(Formato.moeda(v.getValor()));
        lblTotal.setText(Formato.moeda(v.getTotal()));
        lblPago.setText(Formato.moeda(pago));
        lblRestante.setText(Formato.moeda(Formato.arredondar(v.getTotal() - pago)));
    }
}

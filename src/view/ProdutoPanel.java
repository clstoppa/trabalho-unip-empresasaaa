package view;

import controller.ProdutoController;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import model.ProdutoModel;
import util.Formato;
import util.NegocioException;
import view.ui.Campo;
import view.ui.CampoArea;
import view.ui.CampoTexto;
import view.ui.FormGrid;
import view.ui.Mascara;
import view.ui.Tabelas;

public class ProdutoPanel extends CadastroBase {

    private final ProdutoController controller = new ProdutoController();
    private final FormGrid form = new FormGrid();
    private final CampoTexto txtNome = Campo.texto();
    private final CampoTexto txtUnidade = Campo.texto("UN, KG, CX...");
    private final CampoTexto txtEstoque = Campo.texto("0");
    private final CampoTexto txtPreco = Campo.texto("0,00");
    private final CampoTexto txtCusto = Campo.texto("0,00");
    private final CampoTexto txtAtacado = Campo.texto("0,00");
    private final CampoTexto txtMinimo = Campo.texto("0");
    private final CampoTexto txtMaximo = Campo.texto("0");
    private final CampoTexto txtEmbalagem = Campo.texto("0");
    private final CampoTexto txtPeso = Campo.texto("0");
    private final CampoTexto txtCadastro = Campo.texto("dd/mm/aaaa");
    private final CampoArea txtObs = Campo.area(70);
    private final JCheckBox chkAtivo = Campo.check("Produto ativo");

    public ProdutoPanel() {
        Mascara.instalar(txtCadastro, Mascara.DATA);
        form.secao("Identificação");
        form.campo("Nome *", txtNome, 6);
        form.campo("Unidade *", txtUnidade, 2);
        form.campo("Cadastro", txtCadastro, 2);
        form.campo(" ", chkAtivo, 2);
        form.novaLinha();
        form.secao("Preços");
        form.campo("Preço de venda (R$) *", txtPreco, 3);
        form.campo("Preço de atacado (R$)", txtAtacado, 3);
        form.campo("Custo (R$)", txtCusto, 3);
        form.novaLinha();
        form.secao("Estoque");
        form.campo("Estoque atual", txtEstoque, 3);
        form.campo("Estoque mínimo", txtMinimo, 3);
        form.campo("Estoque máximo", txtMaximo, 3);
        form.novaLinha();
        form.campo("Embalagem (un. por caixa)", txtEmbalagem, 3);
        form.campo("Peso (kg)", txtPeso, 3);
        form.novaLinha();
        form.campo("Observações", txtObs, 12);
        for (CampoTexto t : new CampoTexto[]{txtEstoque, txtPreco, txtCusto, txtAtacado, txtMinimo, txtMaximo, txtEmbalagem, txtPeso}) {
            t.setHorizontalAlignment(CampoTexto.RIGHT);
        }
        montar();
    }

    @Override
    protected String titulo() {
        return "Produtos";
    }

    @Override
    protected String singular() {
        return "produto";
    }

    @Override
    protected String descricao() {
        return "Itens vendidos e comprados, com preço e estoque.";
    }

    @Override
    protected String placeholderBusca() {
        return "Buscar produto pelo nome...";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Cód.", "Produto", "Un.", "Estoque", "Preço", "Custo", "Situação"};
    }

    @Override
    protected void configurarTabela(JTable t) {
        Tabelas.moeda(t, 4, 5);
        Tabelas.largura(t, 1, 300);
    }

    @Override
    protected List<Object[]> listar(String filtro) throws NegocioException {
        List<Object[]> linhas = new ArrayList<>();
        for (ProdutoModel p : controller.consultar(filtro)) {
            linhas.add(new Object[]{p.getCodigo(), p.getNome(), p.getUnidade(), Formato.quantidade(p.getEstoque()),
                p.getPreco(), p.getCusto(), p.isAtivo() ? "Ativo" : "Inativo"});
        }
        return linhas;
    }

    @Override
    protected JComponent criarFormulario() {
        return form;
    }

    @Override
    protected void novoRegistro() {
        for (CampoTexto t : new CampoTexto[]{txtNome, txtEstoque, txtPreco, txtCusto, txtAtacado, txtMinimo, txtMaximo, txtEmbalagem, txtPeso}) {
            t.setText("");
        }
        txtUnidade.setText("UN");
        txtCadastro.setText(Formato.data(LocalDate.now()));
        txtObs.setText("");
        chkAtivo.setSelected(true);
        focar(txtNome);
    }

    @Override
    protected void editarRegistro(int codigo) throws NegocioException {
        ProdutoModel p = controller.buscar(codigo);
        if (p == null) {
            throw new NegocioException("Produto não encontrado.");
        }
        txtNome.setText(p.getNome());
        txtUnidade.setText(p.getUnidade());
        txtEstoque.setText(Formato.quantidade(p.getEstoque()));
        txtPreco.setText(Formato.decimal(p.getPreco()));
        txtCusto.setText(Formato.decimal(p.getCusto()));
        txtAtacado.setText(Formato.decimal(p.getAtacado()));
        txtMinimo.setText(Formato.quantidade(p.getMinimo()));
        txtMaximo.setText(Formato.quantidade(p.getMaximo()));
        txtEmbalagem.setText(String.valueOf(p.getEmbalagem()));
        txtPeso.setText(Formato.quantidade(p.getPeso()));
        txtCadastro.setText(Formato.data(p.getCadastro()));
        txtObs.setText(p.getObs());
        chkAtivo.setSelected(p.isAtivo());
    }

    @Override
    protected void gravarRegistro() throws NegocioException {
        ProdutoModel p = idEdicao() == 0 ? new ProdutoModel() : controller.buscar(idEdicao());
        p.setNome(txtNome.getText().trim());
        p.setUnidade(txtUnidade.getText().trim());
        p.setEstoque(Formato.numero(txtEstoque.getText(), "Estoque"));
        p.setPreco(Formato.numero(txtPreco.getText(), "Preço de venda"));
        p.setCusto(Formato.numero(txtCusto.getText(), "Custo"));
        p.setAtacado(Formato.numero(txtAtacado.getText(), "Preço de atacado"));
        p.setMinimo(Formato.numero(txtMinimo.getText(), "Estoque mínimo"));
        p.setMaximo(Formato.numero(txtMaximo.getText(), "Estoque máximo"));
        p.setEmbalagem((int) Formato.numero(txtEmbalagem.getText(), "Embalagem"));
        p.setPeso(Formato.numero(txtPeso.getText(), "Peso"));
        p.setCadastro(Formato.data(txtCadastro.getText(), "Cadastro"));
        p.setObs(txtObs.getText().trim());
        p.setAtivo(chkAtivo.isSelected());
        controller.gravar(p);
    }

    @Override
    protected void excluirRegistro(int codigo) throws NegocioException {
        controller.excluir(codigo);
    }
}

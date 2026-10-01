package view;

import java.awt.FlowLayout;
import java.awt.Dimension;
import java.time.LocalDate;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import model.PessoaModel;
import util.Formato;
import view.ui.Campo;
import view.ui.CampoCombo;
import view.ui.CampoTexto;
import view.ui.FormGrid;
import view.ui.Mascara;

/**
 * Bloco de formulário com todos os campos da tabela PESSOA. É reutilizado por Cliente e
 * Fornecedor, que só acrescentam os campos próprios ao final.
 */
public class PessoaForm extends FormGrid {

    private static final String[] UFS = {"", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
        "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

    private final JRadioButton rbFisica = Campo.radio("Física");
    private final JRadioButton rbJuridica = Campo.radio("Jurídica");
    private final CampoTexto txtNome = Campo.texto();
    private final CampoTexto txtFantasia = Campo.texto();
    private final CampoTexto txtDocumento = Campo.texto("000.000.000-00");
    private final CampoTexto txtRgIe = Campo.texto();
    private final CampoTexto txtCep = Campo.texto("00000-000");
    private final CampoTexto txtEndereco = Campo.texto();
    private final CampoTexto txtNumero = Campo.texto();
    private final CampoTexto txtComplemento = Campo.texto();
    private final CampoTexto txtBairro = Campo.texto();
    private final CampoTexto txtCidade = Campo.texto();
    private final CampoCombo<String> cbUf = Campo.combo();
    private final CampoTexto txtFone1 = Campo.texto();
    private final CampoTexto txtFone2 = Campo.texto();
    private final CampoTexto txtCelular = Campo.texto();
    private final CampoTexto txtEmail = Campo.texto();
    private final CampoTexto txtSite = Campo.texto();
    private final CampoTexto txtCadastro = Campo.somenteLeitura();

    public PessoaForm() {
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbFisica);
        grupo.add(rbJuridica);
        rbFisica.setSelected(true);
        rbFisica.addActionListener(e -> ajustarTipo());
        rbJuridica.addActionListener(e -> ajustarTipo());
        for (String uf : UFS) {
            cbUf.addItem(uf);
        }
        Mascara.instalar(txtDocumento, Mascara.CPF_CNPJ);
        Mascara.instalar(txtCep, Mascara.CEP);
        Mascara.instalar(txtFone1, Mascara.TELEFONE);
        Mascara.instalar(txtFone2, Mascara.TELEFONE);
        Mascara.instalar(txtCelular, Mascara.TELEFONE);

        JPanel tipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 8));
        tipo.setOpaque(false);
        tipo.setPreferredSize(new Dimension(40, 40));
        tipo.add(rbFisica);
        tipo.add(rbJuridica);

        secao("Identificação");
        campo("Tipo de pessoa", tipo, 4);
        campo("CPF / CNPJ *", txtDocumento, 4);
        campo("RG / Inscrição estadual", txtRgIe, 4);
        campo("Nome / Razão social *", txtNome, 8);
        campo("Nome fantasia", txtFantasia, 4);
        secao("Endereço");
        campo("CEP", txtCep, 3);
        campo("Endereço", txtEndereco, 6);
        campo("Número", txtNumero, 3);
        campo("Complemento", txtComplemento, 3);
        campo("Bairro", txtBairro, 3);
        campo("Cidade", txtCidade, 4);
        campo("UF", cbUf, 2);
        secao("Contato");
        campo("Telefone 1", txtFone1, 3);
        campo("Telefone 2", txtFone2, 3);
        campo("Celular", txtCelular, 3);
        campo("Cadastrado em", txtCadastro, 3);
        campo("E-mail", txtEmail, 6);
        campo("Site", txtSite, 6);
        secao("Informações adicionais");
    }

    private void ajustarTipo() {
        txtDocumento.setPlaceholder(rbFisica.isSelected() ? "000.000.000-00" : "00.000.000/0000-00");
    }

    public void focarNome() {
        txtNome.requestFocusInWindow();
    }

    public void limpar() {
        rbFisica.setSelected(true);
        ajustarTipo();
        for (CampoTexto c : new CampoTexto[]{txtNome, txtFantasia, txtDocumento, txtRgIe, txtCep, txtEndereco, txtNumero,
            txtComplemento, txtBairro, txtCidade, txtFone1, txtFone2, txtCelular, txtEmail, txtSite}) {
            c.setText("");
        }
        cbUf.setSelectedIndex(0);
        txtCadastro.setText(Formato.data(LocalDate.now()));
    }

    public void preencher(PessoaModel p) {
        limpar();
        if (p.isFisica()) {
            rbFisica.setSelected(true);
        } else {
            rbJuridica.setSelected(true);
        }
        ajustarTipo();
        txtNome.setText(p.getNome());
        txtFantasia.setText(p.getFantasia());
        txtDocumento.setText(p.getCpfCnpj());
        txtRgIe.setText(p.getRgIe());
        txtCep.setText(p.getCep());
        txtEndereco.setText(p.getEndereco());
        txtNumero.setText(p.getNumero());
        txtComplemento.setText(p.getComplemento());
        txtBairro.setText(p.getBairro());
        txtCidade.setText(p.getCidade());
        cbUf.setSelectedItem(p.getUf() == null ? "" : p.getUf());
        txtFone1.setText(p.getFone1());
        txtFone2.setText(p.getFone2());
        txtCelular.setText(p.getCelular());
        txtEmail.setText(p.getEmail());
        txtSite.setText(p.getSite());
        txtCadastro.setText(Formato.data(p.getCadastro()));
    }

    /** Copia o que está na tela para o model (não mexe em códigos nem na data de cadastro). */
    public void ler(PessoaModel p) {
        p.setFisica(rbFisica.isSelected());
        p.setNome(txtNome.getText().trim());
        p.setFantasia(txtFantasia.getText().trim());
        p.setCpfCnpj(txtDocumento.getText().trim());
        p.setRgIe(txtRgIe.getText().trim());
        p.setCep(txtCep.getText().trim());
        p.setEndereco(txtEndereco.getText().trim());
        p.setNumero(txtNumero.getText().trim());
        p.setComplemento(txtComplemento.getText().trim());
        p.setBairro(txtBairro.getText().trim());
        p.setCidade(txtCidade.getText().trim());
        p.setUf((String) cbUf.getSelectedItem());
        p.setFone1(txtFone1.getText().trim());
        p.setFone2(txtFone2.getText().trim());
        p.setCelular(txtCelular.getText().trim());
        p.setEmail(txtEmail.getText().trim());
        p.setSite(txtSite.getText().trim());
    }
}

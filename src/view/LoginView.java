package view;

import controller.LoginController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.FlowLayout;
import java.awt.geom.Path2D;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import util.NegocioException;
import view.ui.Botao;
import view.ui.Campo;
import view.ui.CampoSenha;
import view.ui.CampoTexto;
import view.ui.Dialogos;
import view.ui.Tema;

/** Tela de login: céu estrelado com montanhas pintadas em Java2D e cartão escuro central. */
public class LoginView extends JFrame {

    private final LoginController controller = new LoginController();
    private final CampoTexto txtLogin = new CampoTexto(true);
    private final CampoSenha txtSenha = new CampoSenha(true);
    private final JLabel lblErro = new JLabel(" ");
    private final Botao btEntrar = new Botao("Entrar", Botao.Tipo.DEGRADE);

    public LoginView() {
        super("Login - EMPRESA X");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 560));
        setSize(1040, 680);
        setLocationRelativeTo(null);

        Fundo fundo = new Fundo();
        fundo.setLayout(new GridBagLayout());
        fundo.add(criarCartao());
        setContentPane(fundo);
        getRootPane().setDefaultButton(btEntrar);
    }

    private JComponent criarCartao() {
        JPanel cartao = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18, 17, 28, 225));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 26, 26);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 26, 26);
                g2.dispose();
            }
        };
        cartao.setOpaque(false);
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        cartao.setBorder(new EmptyBorder(34, 40, 30, 40));
        cartao.setPreferredSize(new Dimension(400, 470));

        JLabel titulo = new JLabel("Faça o seu login");
        titulo.setFont(Tema.fonte(Font.BOLD, 26f));
        titulo.setForeground(Color.WHITE);
        JLabel ponto = new JLabel(" \u2022");
        ponto.setFont(Tema.fonte(Font.BOLD, 26f));
        ponto.setForeground(Tema.ACENTO);
        JPanel linhaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaTitulo.setOpaque(false);
        linhaTitulo.add(titulo);
        linhaTitulo.add(ponto);
        linhaTitulo.setAlignmentX(0f);
        cartao.add(linhaTitulo);

        JLabel sub = new JLabel("EMPRESA X \u00B7 Gerenciamento de Compra e Venda");
        sub.setFont(Tema.fonte(Font.PLAIN, 12f));
        sub.setForeground(new Color(0xA19DB8));
        sub.setAlignmentX(0f);
        cartao.add(Box.createVerticalStrut(4));
        cartao.add(sub);
        cartao.add(Box.createVerticalStrut(26));

        txtLogin.setPlaceholder("E-mail ou login");
        txtSenha.setToolTipText("Senha");
        cartao.add(alinhar(rotuloEscuro("E-MAIL")));
        cartao.add(Box.createVerticalStrut(6));
        cartao.add(alinhar(txtLogin));
        cartao.add(Box.createVerticalStrut(16));
        cartao.add(alinhar(rotuloEscuro("SENHA")));
        cartao.add(Box.createVerticalStrut(6));
        cartao.add(alinhar(txtSenha));
        cartao.add(Box.createVerticalStrut(8));

        Botao esqueci = new Botao("esqueci minha senha", Botao.Tipo.LINK);
        esqueci.setForeground(new Color(0xB7A6FF));
        esqueci.addActionListener(e -> dialogoRedefinir());
        JPanel linhaEsqueci = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        linhaEsqueci.setOpaque(false);
        linhaEsqueci.add(esqueci);
        cartao.add(alinhar(linhaEsqueci));

        lblErro.setFont(Tema.fonte(Font.PLAIN, 12f));
        lblErro.setForeground(new Color(0xFF7B80));
        cartao.add(Box.createVerticalStrut(4));
        cartao.add(alinhar(lblErro));
        cartao.add(Box.createVerticalStrut(8));

        btEntrar.setFont(Tema.fonte(Font.BOLD, 15f));
        btEntrar.setPreferredSize(new Dimension(320, 46));
        btEntrar.addActionListener(e -> entrar());
        cartao.add(alinhar(btEntrar));
        cartao.add(Box.createVerticalStrut(18));

        Botao conta = new Botao("ainda não tenho uma conta", Botao.Tipo.LINK);
        conta.setForeground(new Color(0xB7A6FF));
        conta.addActionListener(e -> dialogoCriarConta());
        JPanel linhaConta = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        linhaConta.setOpaque(false);
        linhaConta.add(conta);
        cartao.add(alinhar(linhaConta));
        return cartao;
    }

    private static JLabel rotuloEscuro(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Tema.fonte(Font.BOLD, 11f));
        l.setForeground(new Color(0x8F8BA8));
        return l;
    }

    private static JComponent alinhar(JComponent c) {
        c.setAlignmentX(0f);
        if (c instanceof CampoTexto || c instanceof CampoSenha) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            c.setPreferredSize(new Dimension(320, 44));
        }
        if (c instanceof JPanel || c instanceof JLabel) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        }
        return c;
    }

    private void entrar() {
        lblErro.setText(" ");
        try {
            controller.autenticar(txtLogin.getText(), new String(txtSenha.getPassword()));
            Menu menu = new Menu();
            menu.setVisible(true);
            dispose();
        } catch (NegocioException e) {
            lblErro.setText(e.getMessage());
            txtSenha.setText("");
            txtSenha.requestFocusInWindow();
        }
    }

    // ---------- diálogos ----------
    private JDialog novoDialogo(String titulo) {
        JDialog d = new JDialog(this, titulo, true);
        d.setResizable(false);
        return d;
    }

    private JPanel painelDialogo(String titulo, String texto) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(24, 28, 22, 28));
        JLabel t = Campo.titulo(titulo, 20f);
        t.setAlignmentX(0f);
        JLabel s = Campo.subtitulo(texto);
        s.setAlignmentX(0f);
        p.add(t);
        p.add(Box.createVerticalStrut(2));
        p.add(s);
        p.add(Box.createVerticalStrut(16));
        return p;
    }

    private static void campoNoPainel(JPanel p, String rotulo, JComponent c) {
        JLabel l = Campo.rotulo(rotulo);
        l.setAlignmentX(0f);
        c.setAlignmentX(0f);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        c.setPreferredSize(new Dimension(340, 40));
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(c);
        p.add(Box.createVerticalStrut(12));
    }

    private void dialogoCriarConta() {
        final JDialog d = novoDialogo("Criar conta");
        JPanel p = painelDialogo("Criar conta", "Cadastre um novo usuário de acesso.");
        final CampoTexto nome = Campo.texto();
        final CampoTexto login = Campo.texto();
        final CampoSenha senha = Campo.senha();
        final CampoSenha conf = Campo.senha();
        campoNoPainel(p, "Nome", nome);
        campoNoPainel(p, "E-mail ou login", login);
        campoNoPainel(p, "Senha", senha);
        campoNoPainel(p, "Confirmar senha", conf);
        rodapeDialogo(p, d, "Criar conta", () -> {
            controller.criarConta(nome.getText(), login.getText(), new String(senha.getPassword()), new String(conf.getPassword()));
            txtLogin.setText(login.getText().trim());
            txtSenha.setText("");
            lblErro.setText(" ");
            Dialogos.info(d, "Conta criada! Agora é só entrar.");
        });
        d.setContentPane(p);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void dialogoRedefinir() {
        final JDialog d = novoDialogo("Redefinir senha");
        JPanel p = painelDialogo("Redefinir senha", "Informe o login e escolha uma nova senha.");
        final CampoTexto login = Campo.texto();
        login.setText(txtLogin.getText());
        final CampoSenha senha = Campo.senha();
        final CampoSenha conf = Campo.senha();
        campoNoPainel(p, "E-mail ou login", login);
        campoNoPainel(p, "Nova senha", senha);
        campoNoPainel(p, "Confirmar nova senha", conf);
        rodapeDialogo(p, d, "Salvar nova senha", () -> {
            controller.redefinirSenha(login.getText(), new String(senha.getPassword()), new String(conf.getPassword()));
            Dialogos.info(d, "Senha alterada com sucesso.");
        });
        d.setContentPane(p);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private interface AcaoDialogo {
        void executar() throws NegocioException;
    }

    private void rodapeDialogo(JPanel p, final JDialog d, String textoOk, final AcaoDialogo acao) {
        Botao cancelar = new Botao("Cancelar", Botao.Tipo.SECUNDARIO);
        Botao ok = new Botao(textoOk, Botao.Tipo.PRIMARIO);
        cancelar.addActionListener(e -> d.dispose());
        ok.addActionListener(e -> {
            try {
                acao.executar();
                d.dispose();
            } catch (NegocioException ex) {
                Dialogos.aviso(d, ex.getMessage());
            }
        });
        JPanel r = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        r.setOpaque(false);
        r.add(cancelar);
        r.add(ok);
        r.setAlignmentX(0f);
        p.add(r);
        d.getRootPane().setDefaultButton(ok);
    }

    /** Fundo noturno: gradiente, estrelas, lua e três camadas de montanhas. */
    private static class Fundo extends JPanel {

        private final float[][] estrelas = new float[160][3];

        Fundo() {
            Random r = new Random(7);
            for (float[] e : estrelas) {
                e[0] = r.nextFloat();
                e[1] = r.nextFloat() * 0.72f;
                e[2] = 0.6f + r.nextFloat() * 1.8f;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, new Color(0x0B0A1E), 0, h, new Color(0x3A2468)));
            g2.fillRect(0, 0, w, h);
            for (float[] e : estrelas) {
                int a = 110 + (int) (e[2] * 55) % 140;
                g2.setColor(new Color(255, 255, 255, Math.min(255, a)));
                g2.fillOval((int) (e[0] * w), (int) (e[1] * h), Math.round(e[2]), Math.round(e[2]));
            }
            g2.setColor(new Color(255, 236, 214, 235));
            g2.fillOval((int) (w * 0.82), (int) (h * 0.12), 54, 54);
            g2.setColor(new Color(255, 236, 214, 40));
            g2.fillOval((int) (w * 0.82) - 18, (int) (h * 0.12) - 18, 90, 90);
            montanha(g2, w, h, 0.70f, 0.10f, new Color(0x2A1D55), 3);
            montanha(g2, w, h, 0.78f, 0.07f, new Color(0x1D1442), 5);
            montanha(g2, w, h, 0.88f, 0.05f, new Color(0x110C2B), 8);
            g2.dispose();
        }

        private void montanha(Graphics2D g2, int w, int h, float base, float amp, Color cor, int picos) {
            Path2D.Float p = new Path2D.Float();
            p.moveTo(0, h);
            int passos = picos * 2;
            for (int i = 0; i <= passos; i++) {
                float x = w * i / (float) passos;
                float y = h * (base - (i % 2 == 1 ? amp : amp * 0.15f * (i % 4)));
                p.lineTo(x, y);
            }
            p.lineTo(w, h);
            p.closePath();
            g2.setColor(cor);
            g2.fill(p);
        }
    }
}

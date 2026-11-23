package banco.gui;

import banco.excecao.OperacaoInvalidaException;
import banco.modelo.Banco;
import banco.modelo.Conta;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Tela de cadastro de contas
public class FormCadastroConta extends JFrame {

    private Banco banco;
    private JTextField txtNumero = new JTextField();
    private JTextField txtTitular = new JTextField();
    private JTextField txtSaldo = new JTextField("0");
    private JComboBox<String> cmbTipo = new JComboBox<>(
            new String[]{Banco.CORRENTE, Banco.POUPANCA, Banco.SALARIO});

    public FormCadastroConta(Banco banco) {
        super("Cadastro de Contas");
        this.banco = banco;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // fecha só esta janela
        setLayout(new BorderLayout(10, 10));

        // Aviso no topo
        JLabel aviso = new JLabel("<html><small>Corrente: limite de "
                + Conta.moeda(Banco.LIMITE_PADRAO) + " | Poupança: rendimento de "
                + (Banco.TAXA_PADRAO * 100) + "%</small></html>");
        aviso.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        add(aviso, BorderLayout.NORTH);

        // Campos no centro (4 linhas, 2 colunas)
        JPanel campos = new JPanel(new GridLayout(4, 2, 8, 8));
        campos.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        campos.add(new JLabel("Tipo de conta:"));
        campos.add(cmbTipo);
        campos.add(new JLabel("Número da conta:"));
        campos.add(txtNumero);
        campos.add(new JLabel("Titular:"));
        campos.add(txtTitular);
        campos.add(new JLabel("Saldo inicial (R$):"));
        campos.add(txtSaldo);
        add(campos, BorderLayout.CENTER);

        // Botões embaixo
        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnLimpar = new JButton("Limpar");
        btnCadastrar.addActionListener(e -> cadastrar());
        btnLimpar.addActionListener(e -> limpar());
        JPanel botoes = new JPanel(new FlowLayout());
        botoes.add(btnCadastrar);
        botoes.add(btnLimpar);
        add(botoes, BorderLayout.SOUTH);

        setSize(420, 260);
        setLocationRelativeTo(null);
    }

    private void cadastrar() {
        try {
            String textoSaldo = txtSaldo.getText().trim();
            double saldo = textoSaldo.isEmpty() ? 0 : Util.lerValor(textoSaldo);
            String tipo = (String) cmbTipo.getSelectedItem();

            Conta conta = banco.criarConta(tipo, txtNumero.getText(), txtTitular.getText(), saldo);
            Util.salvar(this, banco);

            JOptionPane.showMessageDialog(this,
                    tipo + " " + conta.getNumero() + " criada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limpar();
        } catch (OperacaoInvalidaException | IllegalArgumentException e) {
            // Mostra a mensagem da exceção para o usuário
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpar() {
        txtNumero.setText("");
        txtTitular.setText("");
        txtSaldo.setText("0");
        cmbTipo.setSelectedIndex(0);
    }
}

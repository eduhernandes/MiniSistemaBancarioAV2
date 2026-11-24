package banco.gui;

import banco.excecao.ContaInexistenteException;
import banco.excecao.OperacaoInvalidaException;
import banco.excecao.SaldoInsuficienteException;
import banco.modelo.Banco;
import banco.modelo.Conta;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Tela de depósito, saque, transferência, saldo e rendimento
public class FormOperacoes extends JFrame {

    private Banco banco;
    private JTextField txtConta = new JTextField();
    private JTextField txtValor = new JTextField();
    private JTextField txtDestino = new JTextField();

    public FormOperacoes(Banco banco) {
        super("Operações Bancárias");
        this.banco = banco;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Campos
        JPanel campos = new JPanel(new GridLayout(3, 2, 8, 8));
        campos.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        campos.add(new JLabel("Número da conta:"));
        campos.add(txtConta);
        campos.add(new JLabel("Valor (R$):"));
        campos.add(txtValor);
        campos.add(new JLabel("Conta de destino (só transferência):"));
        campos.add(txtDestino);
        add(campos, BorderLayout.CENTER);

        // Botões
        JButton btnDepositar = new JButton("Depositar");
        JButton btnSacar = new JButton("Sacar");
        JButton btnTransferir = new JButton("Transferir");
        JButton btnSaldo = new JButton("Consultar saldo");
        JButton btnRendimento = new JButton("Aplicar rendimento");
        JButton btnLimpar = new JButton("Limpar");

        btnDepositar.addActionListener(e -> depositar());
        btnSacar.addActionListener(e -> sacar());
        btnTransferir.addActionListener(e -> transferir());
        btnSaldo.addActionListener(e -> consultarSaldo());
        btnRendimento.addActionListener(e -> aplicarRendimento());
        btnLimpar.addActionListener(e -> limpar());

        JPanel botoes = new JPanel(new GridLayout(2, 3, 8, 8));
        botoes.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        botoes.add(btnDepositar);
        botoes.add(btnSacar);
        botoes.add(btnTransferir);
        botoes.add(btnSaldo);
        botoes.add(btnRendimento);
        botoes.add(btnLimpar);
        add(botoes, BorderLayout.SOUTH);

        setSize(560, 240);
        setLocationRelativeTo(null);
    }

    private void depositar() {
        try {
            Conta conta = banco.buscarConta(txtConta.getText());
            double valor = Util.lerValor(txtValor.getText());
            conta.depositar(valor);
            Util.salvar(this, banco);
            sucesso("Depósito realizado!\nNovo saldo: " + Conta.moeda(conta.getSaldo()));
        } catch (ContaInexistenteException | IllegalArgumentException e) {
            erro(e.getMessage());
        }
    }

    // [VÍDEO] try/catch: o try roda a operação; o catch pega a exceção lançada pelas regras e mostra a mensagem
    //        em um JOptionPane. O sistema não quebra. O '|' (multi-catch) trata vários tipos no mesmo bloco.
    private void sacar() {
        try {
            Conta conta = banco.buscarConta(txtConta.getText());
            double valor = Util.lerValor(txtValor.getText());
            conta.sacar(valor);
            Util.salvar(this, banco);
            sucesso("Saque realizado!\nNovo saldo: " + Conta.moeda(conta.getSaldo()));
        } catch (ContaInexistenteException | SaldoInsuficienteException | IllegalArgumentException e) {
            erro(e.getMessage());
        }
    }

    // [VÍDEO] Aqui podem ocorrer 4 exceções: conta inexistente, saldo insuficiente, operação inválida (Conta
    //        Salário) e valor inválido. Depois do sucesso, Util.salvar grava no banco de dados.
    private void transferir() {
        try {
            double valor = Util.lerValor(txtValor.getText());
            banco.transferir(txtConta.getText(), txtDestino.getText(), valor);
            Util.salvar(this, banco);
            sucesso("Transferência de " + Conta.moeda(valor) + " realizada com sucesso!");
        } catch (ContaInexistenteException | SaldoInsuficienteException
                | OperacaoInvalidaException | IllegalArgumentException e) {
            erro(e.getMessage());
        }
    }

    private void consultarSaldo() {
        try {
            Conta conta = banco.buscarConta(txtConta.getText());
            sucesso(conta.getResumo());
        } catch (ContaInexistenteException e) {
            erro(e.getMessage());
        }
    }

    private void aplicarRendimento() {
        try {
            banco.aplicarRendimento(txtConta.getText());
            Util.salvar(this, banco);
            sucesso("Rendimento aplicado! Veja o novo saldo em \"Consultar saldo\".");
        } catch (ContaInexistenteException | OperacaoInvalidaException e) {
            erro(e.getMessage());
        }
    }

    private void limpar() {
        txtConta.setText("");
        txtValor.setText("");
        txtDestino.setText("");
    }

    private void sucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

package banco.gui;

import banco.excecao.ContaInexistenteException;
import banco.modelo.Banco;
import banco.modelo.Conta;
import banco.modelo.Transacao;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// Tela do extrato: mostra as transações da conta em uma tabela
public class FormExtrato extends JFrame {

    private Banco banco;
    private JTextField txtConta = new JTextField(10);
    private JLabel lblResumo = new JLabel(" ");

    // DefaultTableModel guarda os dados da tabela. Apagamos e preenchemos a cada consulta.
    // [VÍDEO] DefaultTableModel guarda os dados da JTable. A cada consulta eu limpo e preencho de novo.
    private DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Data/Hora", "Tipo", "Valor", "Destino/Origem"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false; // o usuário não pode editar as células
        }
    };
    private JTable tabela = new JTable(modelo);

    public FormExtrato(Banco banco) {
        super("Extrato Bancário");
        this.banco = banco;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Topo: número da conta + botão
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> consultar());
        txtConta.addActionListener(e -> consultar()); // Enter também consulta
        topo.add(new JLabel("Número da conta:"));
        topo.add(txtConta);
        topo.add(btnConsultar);
        add(topo, BorderLayout.NORTH);

        // Centro: tabela com barra de rolagem
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Rodapé: titular e saldo
        lblResumo.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        add(lblResumo, BorderLayout.SOUTH);

        setSize(640, 380);
        setLocationRelativeTo(null);
    }

    // [VÍDEO] Percorre conta.getExtrato() e adiciona uma linha na tabela para cada Transacao.
    private void consultar() {
        try {
            Conta conta = banco.buscarConta(txtConta.getText());

            modelo.setRowCount(0); // limpa a tabela
            for (Transacao t : conta.getExtrato()) {
                String sinal = t.isSaida() ? "- " : "+ ";
                modelo.addRow(new Object[]{
                    t.getDataHoraFormatada(),
                    t.getTipo().getDescricao(),
                    sinal + Conta.moeda(t.getValor()),
                    t.getContaRelacionada()
                });
            }
            lblResumo.setText(conta.getTipoConta() + " " + conta.getNumero() + " - "
                    + conta.getTitular() + "   |   Saldo atual: " + Conta.moeda(conta.getSaldo()));
        } catch (ContaInexistenteException e) {
            modelo.setRowCount(0);
            lblResumo.setText(" ");
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

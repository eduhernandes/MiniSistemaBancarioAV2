package banco.gui;

import banco.modelo.Banco;
import banco.modelo.Persistencia;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// Janela principal: só tem o MENU. Cada opção abre uma outra janela.
public class FormPrincipal extends JFrame {

    // Existe UM único Banco, compartilhado com todas as janelas (passado pelo construtor)
    private Banco banco;

    public FormPrincipal(Banco banco) {
        super("Mini Sistema Bancário - IF Baiano");
        this.banco = banco;

        setDefaultCloseOperation(EXIT_ON_CLOSE); // fechar esta janela encerra o programa
        setSize(520, 300);
        setLocationRelativeTo(null); // centraliza na tela

        montarMenu();

        JLabel boasVindas = new JLabel(
                "<html><center><h2>Mini Sistema Bancário</h2>"
                + "Use o menu acima para cadastrar contas,<br>fazer operações e consultar o extrato.<br><br>"
                + "<small>Contas de exemplo: 001 (Corrente), 002 (Poupança) e 003 (Salário)</small>"
                + "</center></html>", SwingConstants.CENTER);
        add(boasVindas);
    }

    private void montarMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu menuContas = new JMenu("Contas");
        JMenuItem itemCadastro = new JMenuItem("Cadastrar conta");
        itemCadastro.addActionListener(e -> new FormCadastroConta(banco).setVisible(true));
        menuContas.add(itemCadastro);

        JMenu menuOperacoes = new JMenu("Operações");
        JMenuItem itemOperacoes = new JMenuItem("Operações bancárias");
        itemOperacoes.addActionListener(e -> new FormOperacoes(banco).setVisible(true));
        menuOperacoes.add(itemOperacoes);

        JMenu menuExtrato = new JMenu("Extrato");
        JMenuItem itemExtrato = new JMenuItem("Consultar extrato");
        itemExtrato.addActionListener(e -> new FormExtrato(banco).setVisible(true));
        menuExtrato.add(itemExtrato);

        JMenu menuSistema = new JMenu("Sistema");
        JMenuItem itemSobre = new JMenuItem("Sobre");
        itemSobre.addActionListener(e -> new FormSobre(this).setVisible(true));
        menuSistema.add(itemSobre);

        menuSistema.addSeparator(); // linha separando "Sobre" de "Sair"

        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> System.exit(0));
        menuSistema.add(itemSair);

        barra.add(menuContas);
        barra.add(menuOperacoes);
        barra.add(menuExtrato);
        barra.add(menuSistema);
        setJMenuBar(barra);
    }

    // Ponto de entrada do programa
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // se não conseguir, usa o visual padrão do Java
        }
        SwingUtilities.invokeLater(() -> {
            Banco banco = null;
            try {
                banco = Persistencia.carregar(); // lê o banco.dat (null se não existir)
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null,
                        "Não foi possível ler o arquivo " + Persistencia.ARQUIVO
                        + ". Será iniciado um banco novo.\n" + e.getMessage(),
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            if (banco == null) { // primeira execução (ou arquivo ilegível)
                banco = new Banco();
                banco.carregarContasDeExemplo();
            }
            new FormPrincipal(banco).setVisible(true);
        });
    }
}

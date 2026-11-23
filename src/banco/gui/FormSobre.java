package banco.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// Janela "Sobre": mostra os dados do trabalho.
// JDialog é uma janela de diálogo; modal = precisa ser fechada antes de voltar à janela principal.
public class FormSobre extends JDialog {

    // Edite aqui os dados que aparecem na janela
    private static final String ALUNO = "Eduardo de Almeida Hernandes";
    private static final String PROFESSORA = "Roberta Mércia Rodrigues de Oliveira";

    public FormSobre(JFrame janelaPai) {
        super(janelaPai, "Sobre", true);
        setLayout(new BorderLayout(10, 10));

        JLabel texto = new JLabel(
                "<html><center>"
                + "<h2>Mini Sistema Bancário</h2>"
                + "<b>AV2 - Avaliação Virtual 2</b><br>"
                + "Programação Orientada a Objetos<br><br>"
                + "Instituto Federal Baiano (IF Baiano)<br>"
                + "Curso Técnico em Informática 2026 - Módulo II<br>"
                + "Disciplina: TI10-POO-2026.2<br><br>"
                + "<b>Aluno:</b> " + ALUNO + "<br>"
                + "<b>Professora:</b> " + PROFESSORA
                + "</center></html>", SwingConstants.CENTER);
        texto.setBorder(BorderFactory.createEmptyBorder(15, 25, 5, 25));
        add(texto, BorderLayout.CENTER);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose()); // fecha só esta janela
        JPanel botoes = new JPanel(new FlowLayout());
        botoes.add(btnFechar);
        add(botoes, BorderLayout.SOUTH);

        pack(); // ajusta o tamanho da janela ao conteúdo
        setLocationRelativeTo(janelaPai);
    }
}

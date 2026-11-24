package banco.gui;

import banco.modelo.Banco;
import banco.modelo.BancoDados;
import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;

// Métodos auxiliares usados pelas telas
public class Util {

    // Fica false quando o banco de dados não pôde ser aberto na inicialização
    private static boolean salvamentoAtivo = true;

    public static void desativarSalvamento() {
        salvamentoAtivo = false;
    }

    // Grava o banco no SQLite. Se falhar, avisa o usuário (a operação em si já foi feita).
    // [VÍDEO] Salva a cada operação concluída (e não só ao fechar), para não perder dados se o programa
    //        for encerrado pelo botão Stop do NetBeans.
    public static void salvar(Component pai, Banco banco) {
        if (!salvamentoAtivo) {
            return; // sistema rodando só em memória
        }
        try {
            BancoDados.salvar(banco);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pai,
                    "A operação foi realizada, mas não foi possível gravar no banco de dados:\n"
                    + e.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Converte o texto digitado em número. Aceita vírgula (150,50) ou ponto (150.50).
    public static double lerValor(String texto) {
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Digite um valor numérico válido (ex.: 150,50).");
        }
    }
}

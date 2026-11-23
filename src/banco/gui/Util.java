package banco.gui;

import banco.modelo.Banco;
import banco.modelo.Persistencia;
import java.awt.Component;
import java.io.IOException;
import javax.swing.JOptionPane;

// Métodos auxiliares usados pelas telas
public class Util {

    // Salva o banco no arquivo. Se falhar, avisa o usuário (a operação em si já foi feita).
    public static void salvar(Component pai, Banco banco) {
        try {
            Persistencia.salvar(banco);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(pai,
                    "A operação foi realizada, mas não foi possível salvar em "
                    + Persistencia.ARQUIVO + ":\n" + e.getMessage(),
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

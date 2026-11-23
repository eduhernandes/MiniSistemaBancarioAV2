package banco.modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// Grava e lê o Banco inteiro em um arquivo (serialização de objetos).
// Como o Banco guarda as contas e cada conta guarda o extrato, um único writeObject salva tudo.
public class Persistencia {

    // O arquivo fica na pasta de onde o programa é executado (no NetBeans, a pasta do projeto)
    public static final String ARQUIVO = "banco.dat";

    // Transforma o Banco em bytes e grava no arquivo
    public static void salvar(Banco banco) throws IOException {
        // try-with-resources: o arquivo é fechado automaticamente no final
        try (ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            saida.writeObject(banco);
        }
    }

    // Lê o arquivo e reconstrói o Banco. Devolve null se o arquivo ainda não existe.
    public static Banco carregar() throws IOException, ClassNotFoundException {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) {
            return null; // primeira execução
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (Banco) entrada.readObject();
        }
    }
}

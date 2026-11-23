package banco.excecao;

// Lançada quando o número de conta digitado não existe no banco.
public class ContaInexistenteException extends Exception {

    public ContaInexistenteException(String mensagem) {
        super(mensagem);
    }
}

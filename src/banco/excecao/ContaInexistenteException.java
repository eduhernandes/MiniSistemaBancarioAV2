package banco.excecao;

// Lançada quando o número de conta digitado não existe no banco.
// [VÍDEO] Exceção PERSONALIZADA para número de conta que não existe.
public class ContaInexistenteException extends Exception {

    public ContaInexistenteException(String mensagem) {
        super(mensagem);
    }
}

package banco.excecao;

// Lançada quando a operação não é permitida pelas regras do banco.
// Ex.: transferir para Conta Salário, número de conta repetido.
public class OperacaoInvalidaException extends Exception {

    public OperacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}

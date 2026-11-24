package banco.excecao;

// Lançada quando a operação não é permitida pelas regras do banco.
// Ex.: transferir para Conta Salário, número de conta repetido.
// [VÍDEO] Exceção PERSONALIZADA para operação que as regras do banco não permitem (ex.: transferir para Salário).
public class OperacaoInvalidaException extends Exception {

    public OperacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}

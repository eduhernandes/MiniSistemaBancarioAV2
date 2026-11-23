package banco.excecao;

// Exceção PERSONALIZADA: lançada quando o saldo não permite a operação.
// "extends Exception" = exceção verificada: o compilador obriga o try/catch (ou throws).
public class SaldoInsuficienteException extends Exception {

    public SaldoInsuficienteException(String mensagem) {
        super(mensagem); // guarda a mensagem, que depois lemos com getMessage()
    }
}

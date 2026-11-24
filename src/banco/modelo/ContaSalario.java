package banco.modelo;

import banco.excecao.SaldoInsuficienteException;

// Conta Salário: sem cheque especial, sem rendimento, NÃO recebe transferências.
public class ContaSalario extends Conta {

    public ContaSalario(String numero, String titular, double saldoInicial) {
        super(numero, titular, saldoInicial);
    }

    // Aqui está a regra: esta conta NÃO aceita receber transferências.
    // [VÍDEO] ÚNICA regra da Conta Salário sobre transferência: devolve false. A verificação em si
    //        acontece na classe Conta, então não repito código aqui.
    @Override
    public boolean aceitaTransferencia() {
        return false;
    }

    // Regra da conta salário: só pode sacar até o saldo
    @Override
    protected void verificarSaque(double valor) throws SaldoInsuficienteException {
        if (valor > getSaldo()) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo atual: " + moeda(getSaldo()));
        }
    }

    @Override
    public String getTipoConta() {
        return "Conta Salário";
    }

    @Override
    public String getResumo() {
        return getTipoConta() + " " + getNumero() + " - " + getTitular()
                + "\nSaldo: " + moeda(getSaldo());
    }
}

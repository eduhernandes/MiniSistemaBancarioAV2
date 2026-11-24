package banco.modelo;

import banco.excecao.SaldoInsuficienteException;

public class ContaCorrente extends Conta {

    private double limiteChequeEspecial;

    public ContaCorrente(String numero, String titular, double saldoInicial, double limite) {
        super(numero, titular, saldoInicial);
        this.limiteChequeEspecial = limite;
    }

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    // Saldo negativo = cheque especial em uso
    public double getChequeEspecialUtilizado() {
        if (getSaldo() < 0) {
            return -getSaldo();
        }
        return 0;
    }

    public double getChequeEspecialDisponivel() {
        return limiteChequeEspecial - getChequeEspecialUtilizado();
    }

    // Regra da corrente: pode sacar até saldo + limite do cheque especial
    // [VÍDEO] Aqui está o 'throw new ...': LANÇA a exceção personalizada quando o valor passa de saldo + limite.
    @Override
    protected void verificarSaque(double valor) throws SaldoInsuficienteException {
        if (valor > getSaldo() + limiteChequeEspecial) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Disponível (saldo + cheque especial): "
                    + moeda(getSaldo() + limiteChequeEspecial));
        }
    }

    @Override
    public String getTipoConta() {
        return "Conta Corrente";
    }

    @Override
    public String getResumo() {
        return getTipoConta() + " " + getNumero() + " - " + getTitular()
                + "\nSaldo: " + moeda(getSaldo())
                + "\nLimite do cheque especial: " + moeda(limiteChequeEspecial)
                + "\nCheque especial utilizado: " + moeda(getChequeEspecialUtilizado())
                + "\nCheque especial disponível: " + moeda(getChequeEspecialDisponivel());
    }
}

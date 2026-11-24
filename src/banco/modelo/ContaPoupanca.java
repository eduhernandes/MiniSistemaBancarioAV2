package banco.modelo;

import banco.excecao.SaldoInsuficienteException;

public class ContaPoupanca extends Conta {

    private static final long serialVersionUID = 1L;


    private double taxaRendimento; // ex.: 0.005 = 0,5%

    public ContaPoupanca(String numero, String titular, double saldoInicial, double taxa) {
        super(numero, titular, saldoInicial);
        this.taxaRendimento = taxa;
    }

    public double getTaxaRendimento() {
        return taxaRendimento;
    }

    // Soma ao saldo o rendimento (saldo x taxa) e registra no extrato
    public void aplicarRendimento() {
        double rendimento = getSaldo() * taxaRendimento;
        setSaldo(getSaldo() + rendimento);
        registrar(new Transacao(Transacao.Tipo.RENDIMENTO, rendimento, "-"));
    }

    // Regra da poupança: só pode sacar até o saldo
    @Override
    protected void verificarSaque(double valor) throws SaldoInsuficienteException {
        if (valor > getSaldo()) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo atual: " + moeda(getSaldo()));
        }
    }

    @Override
    public String getTipoConta() {
        return "Conta Poupança";
    }

    @Override
    public String getResumo() {
        return getTipoConta() + " " + getNumero() + " - " + getTitular()
                + "\nSaldo: " + moeda(getSaldo())
                + "\nTaxa de rendimento: " + (taxaRendimento * 100) + "%";
    }
}

package banco.modelo;

import banco.excecao.OperacaoInvalidaException;
import banco.excecao.SaldoInsuficienteException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// CLASSE ABSTRATA: modelo das contas. Guarda o que é comum a todas.
// "implements Serializable" permite gravar o objeto em arquivo (as filhas herdam isso).
public abstract class Conta implements Serializable {

    // Identifica a versão da classe no arquivo .dat (mesma linha em todas as classes gravadas)
    private static final long serialVersionUID = 1L;

    // private: encapsulamento. Só esta classe mexe diretamente nesses dados.
    private String numero;
    private String titular;
    private double saldo;
    private List<Transacao> transacoes = new ArrayList<>(); // histórico (extrato)

    public Conta(String numero, String titular, double saldoInicial) {
        if (Double.isNaN(saldoInicial) || Double.isInfinite(saldoInicial) || saldoInicial < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldoInicial;
        if (saldoInicial > 0) {
            registrar(new Transacao(Transacao.Tipo.DEPOSITO, saldoInicial, "-"));
        }
    }

    // ---------- Métodos ABSTRATOS: cada tipo de conta escreve o seu ----------

    // Regra de saque da conta: se NÃO puder sacar o valor, lança SaldoInsuficienteException.
    protected abstract void verificarSaque(double valor) throws SaldoInsuficienteException;

    // Nome do tipo da conta (ex.: "Conta Corrente")
    public abstract String getTipoConta();

    // Texto com o resumo do saldo da conta
    public abstract String getResumo();

    // ---------- Leitura dos dados (public) ----------
    public String getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    // protected: só as classes filhas podem alterar o saldo
    protected void setSaldo(double novoSaldo) {
        this.saldo = novoSaldo;
    }

    // Por padrão toda conta aceita receber transferência. A ContaSalario muda isso.
    public boolean aceitaTransferencia() {
        return true;
    }

    // ---------- Operações ----------

    public void depositar(double valor) {
        validarValor(valor);
        saldo += valor;
        registrar(new Transacao(Transacao.Tipo.DEPOSITO, valor, "-"));
    }

    public void sacar(double valor) throws SaldoInsuficienteException {
        validarValor(valor);
        verificarSaque(valor); // aplica a regra da conta (pode lançar exceção)
        saldo -= valor;
        registrar(new Transacao(Transacao.Tipo.SAQUE, valor, "-"));
    }

    public void transferir(Conta destino, double valor)
            throws SaldoInsuficienteException, OperacaoInvalidaException {
        if (destino == null) {
            throw new OperacaoInvalidaException("Conta de destino não informada.");
        }
        if (destino == this) {
            throw new OperacaoInvalidaException("A conta de destino deve ser diferente da conta de origem.");
        }
        if (!destino.aceitaTransferencia()) {
            throw new OperacaoInvalidaException("A conta " + destino.getNumero() + " ("
                    + destino.getTipoConta() + ") não pode receber transferências.");
        }
        validarValor(valor);
        verificarSaque(valor); // usa a regra de saque da conta de ORIGEM

        // Tudo certo: tira da origem e coloca no destino
        saldo -= valor;
        registrar(new Transacao(Transacao.Tipo.TRANSFERENCIA_ENVIADA, valor, destino.getNumero()));
        destino.saldo += valor;
        destino.registrar(new Transacao(Transacao.Tipo.TRANSFERENCIA_RECEBIDA, valor, this.numero));
    }

    // Devolve uma CÓPIA da lista, para ninguém alterar o histórico por fora
    public List<Transacao> getExtrato() {
        return new ArrayList<>(transacoes);
    }

    // ---------- Auxiliares ----------

    protected void registrar(Transacao transacao) {
        transacoes.add(transacao);
    }

    void adicionarTransacaoPersistida(Transacao transacao) {
        transacoes.add(transacao);
    }

    void limparTransacoesPersistidas() {
        transacoes.clear();
    }

    // IllegalArgumentException: exceção do próprio Java para argumento inválido
    protected void validarValor(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }
    }

    // Formata como moeda: R$ 1.234,50
    public static String moeda(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %,.2f", valor);
    }
}

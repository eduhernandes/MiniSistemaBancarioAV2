package banco.modelo;

import banco.excecao.OperacaoInvalidaException;
import banco.excecao.SaldoInsuficienteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// CLASSE ABSTRATA: modelo das contas. Guarda o que é comum a todas.
// [VÍDEO] 'abstract' impede criar 'new Conta()'. Ela é só o MODELO do que é comum às 3 contas.
public abstract class Conta {

    // private: encapsulamento. Só esta classe mexe diretamente nesses dados.
    // [VÍDEO] Citar os 3 modificadores: private (atributos), protected (setSaldo) e public (getters e operações).
    private String numero;
    private String titular;
    private double saldo;
    // [VÍDEO] Estrutura de dados do extrato: ArrayList<Transacao>. Cada operação adiciona um objeto aqui.
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
    // [VÍDEO] Método ABSTRATO: cada filha escreve a sua regra de saque.
    //        Corrente = saldo + cheque especial. Poupança e Salário = só o saldo.
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

    // [VÍDEO] 'throws' DECLARA que o método pode lançar a exceção. Quem LANÇA é o 'throw' (veja nas filhas).
    //        Valor <= 0 lança IllegalArgumentException (validarValor, mais abaixo).
    public void sacar(double valor) throws SaldoInsuficienteException {
        validarValor(valor);
        verificarSaque(valor); // aplica a regra da conta (pode lançar exceção)
        saldo -= valor;
        registrar(new Transacao(Transacao.Tipo.SAQUE, valor, "-"));
    }

    // [VÍDEO] REGRA DE TRANSFERÊNCIA CENTRALIZADA: escrita uma vez só, aqui.
    //        1) pergunta ao destino se aceita (aceitaTransferencia)  2) usa a regra de saque da ORIGEM
    //        (polimorfismo)  3) registra no extrato das duas contas. Uma conta nova já herda essa regra.
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
    // [VÍDEO] Devolve uma CÓPIA: quem chama não consegue apagar nem alterar o histórico original.
    public List<Transacao> getExtrato() {
        return new ArrayList<>(transacoes);
    }

    // ---------- Auxiliares ----------

    protected void registrar(Transacao transacao) {
        transacoes.add(transacao);
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

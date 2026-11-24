package banco.modelo;

import banco.excecao.ContaInexistenteException;
import banco.excecao.OperacaoInvalidaException;
import banco.excecao.SaldoInsuficienteException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

// Guarda todas as contas. Usa um Map: a CHAVE é o número da conta e o VALOR é a Conta.
// Assim a busca por número é direta.
public class Banco implements Serializable {

    private static final long serialVersionUID = 1L;

    // Nomes dos tipos (usados também no JComboBox da tela de cadastro)
    public static final String CORRENTE = "Conta Corrente";
    public static final String POUPANCA = "Conta Poupança";
    public static final String SALARIO = "Conta Salário";

    // Valores fixos usados no cadastro (mesmos da AV1)
    public static final double LIMITE_PADRAO = 500.0;
    public static final double TAXA_PADRAO = 0.005;

    // LinkedHashMap mantém a ordem em que as contas foram criadas
    private Map<String, Conta> contas = new LinkedHashMap<>();

    public Conta criarConta(String tipo, String numero, String titular, double saldoInicial)
            throws OperacaoInvalidaException {
        numero = numero.trim();
        titular = titular.trim();
        if (numero.isEmpty() || titular.isEmpty()) {
            throw new OperacaoInvalidaException("Informe o número da conta e o titular.");
        }
        if (contas.containsKey(numero)) {
            throw new OperacaoInvalidaException("Já existe uma conta com o número " + numero + ".");
        }

        Conta conta;
        if (tipo.equals(CORRENTE)) {
            conta = new ContaCorrente(numero, titular, saldoInicial, LIMITE_PADRAO);
        } else if (tipo.equals(POUPANCA)) {
            conta = new ContaPoupanca(numero, titular, saldoInicial, TAXA_PADRAO);
        } else if (tipo.equals(SALARIO)) {
            conta = new ContaSalario(numero, titular, saldoInicial);
        } else {
            throw new OperacaoInvalidaException("Tipo de conta inválido.");
        }

        contas.put(numero, conta);
        return conta;
    }

    // Procura a conta pelo número. Se não achar, lança ContaInexistenteException.
    public Conta buscarConta(String numero) throws ContaInexistenteException {
        String chave = (numero == null) ? "" : numero.trim();
        Conta conta = contas.get(chave);
        if (conta == null) {
            throw new ContaInexistenteException("Não existe conta com o número \"" + chave + "\".");
        }
        return conta;
    }

    public boolean estaVazio() {
        return contas.isEmpty();
    }

    Collection<Conta> contasPersistidas() {
        return contas.values();
    }

    void adicionarContaPersistida(Conta conta) {
        contas.put(conta.getNumero(), conta);
    }

    public void transferir(String numeroOrigem, String numeroDestino, double valor)
            throws ContaInexistenteException, SaldoInsuficienteException, OperacaoInvalidaException {
        Conta origem = buscarConta(numeroOrigem);
        Conta destino = buscarConta(numeroDestino);
        origem.transferir(destino, valor);
    }

    public void aplicarRendimento(String numero)
            throws ContaInexistenteException, OperacaoInvalidaException {
        Conta conta = buscarConta(numero);
        if (!(conta instanceof ContaPoupanca)) {
            throw new OperacaoInvalidaException("Rendimento só pode ser aplicado em Conta Poupança.");
        }
        ((ContaPoupanca) conta).aplicarRendimento();
    }

    // Cria 3 contas para facilitar os testes e a demonstração
    public void carregarContasDeExemplo() {
        try {
            criarConta(CORRENTE, "001", "Eduardo", 1000);
            criarConta(POUPANCA, "002", "Eduardo", 2000);
            criarConta(SALARIO, "003", "Eduardo", 1500);
        } catch (OperacaoInvalidaException e) {
            System.out.println("Erro ao criar contas de exemplo: " + e.getMessage());
        }
    }
}

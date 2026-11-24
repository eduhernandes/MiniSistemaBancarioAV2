package banco.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Representa UMA movimentação da conta (uma linha do extrato).
public class Transacao {

    // enum = lista fixa de valores possíveis para o tipo da transação
    // [VÍDEO] enum = lista fixa de tipos. Tem 5: depósito, saque, transferência enviada/recebida e rendimento.
    public enum Tipo {
        DEPOSITO("Depósito"),
        SAQUE("Saque"),
        TRANSFERENCIA_ENVIADA("Transferência enviada"),
        TRANSFERENCIA_RECEBIDA("Transferência recebida"),
        RENDIMENTO("Rendimento");

        private String descricao;

        Tipo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    private Tipo tipo;
    private double valor;
    private LocalDateTime dataHora;
    // Conta destino (se enviou) ou conta de origem (se recebeu). "-" quando não se aplica.
    private String contaRelacionada;

    public Transacao(Tipo tipo, double valor, String contaRelacionada) {
        this.tipo = tipo;
        this.valor = valor;
        this.contaRelacionada = contaRelacionada;
        this.dataHora = LocalDateTime.now(); // data e hora do momento da operação
    }

    // Construtor usado ao LER do banco de dados: mantém a data/hora original da transação
    public Transacao(Tipo tipo, double valor, LocalDateTime dataHora, String contaRelacionada) {
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
        this.contaRelacionada = contaRelacionada;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public String getContaRelacionada() {
        return contaRelacionada;
    }

    public String getDataHoraFormatada() {
        return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    // true se o dinheiro SAIU da conta (usado para mostrar o sinal - ou +)
    public boolean isSaida() {
        return tipo == Tipo.SAQUE || tipo == Tipo.TRANSFERENCIA_ENVIADA;
    }
}

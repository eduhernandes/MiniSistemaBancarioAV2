package banco.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

// Grava e lê as contas e seus extratos em um banco SQLite.
public class Persistencia {

    public static final String ARQUIVO = "banco.db";
    private static final String URL = "jdbc:sqlite:" + ARQUIVO;

    public static void salvar(Banco banco) throws SQLException {
        try (Connection conexao = conectar()) {
            criarEstrutura(conexao);
            conexao.setAutoCommit(false);
            try {
                try (Statement comando = conexao.createStatement()) {
                    comando.executeUpdate("DELETE FROM transacoes");
                    comando.executeUpdate("DELETE FROM contas");
                }

                salvarContas(conexao, banco);
                salvarTransacoes(conexao, banco);
                conexao.commit();
            } catch (SQLException e) {
                try {
                    conexao.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    public static Banco carregar() throws SQLException {
        try (Connection conexao = conectar()) {
            criarEstrutura(conexao);
            Banco banco = new Banco();
            Map<String, Conta> contas = new LinkedHashMap<>();

            try (Statement comando = conexao.createStatement();
                    ResultSet resultados = comando.executeQuery(
                            "SELECT numero, titular, tipo, saldo, parametro FROM contas ORDER BY id")) {
                while (resultados.next()) {
                    Conta conta = criarConta(resultados);
                    conta.limparTransacoesPersistidas();
                    contas.put(conta.getNumero(), conta);
                    banco.adicionarContaPersistida(conta);
                }
            }

            try (Statement comando = conexao.createStatement();
                    ResultSet resultados = comando.executeQuery(
                            "SELECT numero_conta, tipo, valor, conta_relacionada, data_hora "
                            + "FROM transacoes ORDER BY id")) {
                while (resultados.next()) {
                    String numeroConta = resultados.getString("numero_conta");
                    Conta conta = contas.get(numeroConta);
                    if (conta == null) {
                        throw new SQLException("O extrato referencia uma conta inexistente: " + numeroConta);
                    }
                    try {
                        Transacao transacao = new Transacao(
                                Transacao.Tipo.valueOf(resultados.getString("tipo")),
                                resultados.getDouble("valor"),
                                resultados.getString("conta_relacionada"),
                                LocalDateTime.parse(resultados.getString("data_hora")));
                        conta.adicionarTransacaoPersistida(transacao);
                    } catch (IllegalArgumentException | DateTimeParseException e) {
                        throw new SQLException("Não foi possível ler uma transação do banco de dados.", e);
                    }
                }
            }
            return banco;
        }
    }

    private static Connection conectar() throws SQLException {
        Connection conexao = DriverManager.getConnection(URL);
        try (Statement comando = conexao.createStatement()) {
            comando.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            try {
                conexao.close();
            } catch (SQLException closeError) {
                e.addSuppressed(closeError);
            }
            throw e;
        }
        return conexao;
    }

    private static void criarEstrutura(Connection conexao) throws SQLException {
        try (Statement comando = conexao.createStatement()) {
            comando.executeUpdate("CREATE TABLE IF NOT EXISTS contas ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "numero TEXT NOT NULL UNIQUE, "
                    + "titular TEXT NOT NULL, "
                    + "tipo TEXT NOT NULL, "
                    + "saldo REAL NOT NULL, "
                    + "parametro REAL NOT NULL)");
            comando.executeUpdate("CREATE TABLE IF NOT EXISTS transacoes ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "numero_conta TEXT NOT NULL, "
                    + "tipo TEXT NOT NULL, "
                    + "valor REAL NOT NULL, "
                    + "conta_relacionada TEXT NOT NULL, "
                    + "data_hora TEXT NOT NULL, "
                    + "FOREIGN KEY (numero_conta) REFERENCES contas(numero) ON DELETE CASCADE)");
            comando.executeUpdate("CREATE INDEX IF NOT EXISTS idx_transacoes_conta "
                    + "ON transacoes(numero_conta, id)");
        }
    }

    private static void salvarContas(Connection conexao, Banco banco) throws SQLException {
        String sql = "INSERT INTO contas (numero, titular, tipo, saldo, parametro) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (Conta conta : banco.contasPersistidas()) {
                comando.setString(1, conta.getNumero());
                comando.setString(2, conta.getTitular());
                comando.setString(3, conta.getTipoConta());
                comando.setDouble(4, conta.getSaldo());
                comando.setDouble(5, parametroConta(conta));
                comando.addBatch();
            }
            comando.executeBatch();
        }
    }

    private static void salvarTransacoes(Connection conexao, Banco banco) throws SQLException {
        String sql = "INSERT INTO transacoes "
                + "(numero_conta, tipo, valor, conta_relacionada, data_hora) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (Conta conta : banco.contasPersistidas()) {
                for (Transacao transacao : conta.getExtrato()) {
                    comando.setString(1, conta.getNumero());
                    comando.setString(2, transacao.getTipo().name());
                    comando.setDouble(3, transacao.getValor());
                    comando.setString(4, transacao.getContaRelacionada());
                    comando.setString(5, transacao.getDataHoraPersistida().toString());
                    comando.addBatch();
                }
            }
            comando.executeBatch();
        }
    }

    private static double parametroConta(Conta conta) throws SQLException {
        if (conta instanceof ContaCorrente) {
            return ((ContaCorrente) conta).getLimiteChequeEspecial();
        }
        if (conta instanceof ContaPoupanca) {
            return ((ContaPoupanca) conta).getTaxaRendimento();
        }
        if (conta instanceof ContaSalario) {
            return 0;
        }
        throw new SQLException("Tipo de conta não suportado: " + conta.getClass().getName());
    }

    private static Conta criarConta(ResultSet resultados) throws SQLException {
        String numero = resultados.getString("numero");
        String titular = resultados.getString("titular");
        String tipo = resultados.getString("tipo");
        double saldo = resultados.getDouble("saldo");
        double parametro = resultados.getDouble("parametro");
        double saldoInicial = Math.max(saldo, 0);
        Conta conta;
        if (Banco.CORRENTE.equals(tipo)) {
            conta = new ContaCorrente(numero, titular, saldoInicial, parametro);
        } else if (Banco.POUPANCA.equals(tipo)) {
            conta = new ContaPoupanca(numero, titular, saldoInicial, parametro);
        } else if (Banco.SALARIO.equals(tipo)) {
            conta = new ContaSalario(numero, titular, saldoInicial);
        } else {
            throw new SQLException("Tipo de conta desconhecido no banco de dados: " + tipo);
        }
        conta.setSaldo(saldo);
        return conta;
    }
}

package banco.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Acesso ao banco de dados SQLite via JDBC.
// O SQLite guarda tudo em um único arquivo (banco.db), sem precisar instalar servidor.
// [VÍDEO] Persistência com SQLite via JDBC. Duas tabelas: conta e transacao (1 conta tem N transações).
//        O driver sqlite-jdbc (pasta lib) foi adicionado em Libraries. Os dados ficam no arquivo banco.db.
public class BancoDados {

    // jdbc:sqlite:arquivo -> o arquivo é criado automaticamente se não existir.
    // Fica na pasta de onde o programa é executado (no NetBeans, a pasta do projeto).
    public static final String URL = "jdbc:sqlite:banco.db";

    private static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Cria as 2 tabelas se ainda não existirem
    private static void criarTabelas(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS conta ("
                    + "numero TEXT PRIMARY KEY, "
                    + "tipo TEXT NOT NULL, "
                    + "titular TEXT NOT NULL, "
                    + "saldo REAL NOT NULL, "
                    + "limite_cheque_especial REAL, "   // só Conta Corrente
                    + "taxa_rendimento REAL)");         // só Conta Poupança
            st.executeUpdate("CREATE TABLE IF NOT EXISTS transacao ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "conta_numero TEXT NOT NULL, "
                    + "tipo TEXT NOT NULL, "
                    + "valor REAL NOT NULL, "
                    + "data_hora TEXT NOT NULL, "
                    + "conta_relacionada TEXT NOT NULL, "
                    + "FOREIGN KEY (conta_numero) REFERENCES conta(numero))");
        }
    }

    // Lê as tabelas e monta o objeto Banco com as contas e seus extratos.
    // Se o banco de dados ainda estiver vazio, devolve um Banco sem contas.
    // [VÍDEO] Ao abrir o sistema: lê as tabelas e reconstrói os objetos (Corrente, Poupança ou Salário).
    public static Banco carregar() throws SQLException {
        Banco banco = new Banco();
        Map<String, Conta> porNumero = new HashMap<>();

        // try-with-resources: a conexão é fechada automaticamente no final
        try (Connection con = conectar()) {
            criarTabelas(con);

            // 1) Lê as contas
            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT numero, tipo, titular, saldo, "
                            + "limite_cheque_especial, taxa_rendimento FROM conta ORDER BY rowid")) {
                while (rs.next()) {
                    Conta conta = montarConta(rs);
                    banco.adicionarConta(conta);
                    porNumero.put(conta.getNumero(), conta);
                }
            }

            // 2) Lê as transações e coloca cada uma no extrato da sua conta
            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT conta_numero, tipo, valor, data_hora, "
                            + "conta_relacionada FROM transacao ORDER BY id")) {
                while (rs.next()) {
                    Conta conta = porNumero.get(rs.getString("conta_numero"));
                    if (conta != null) {
                        conta.registrar(new Transacao(
                                Transacao.Tipo.valueOf(rs.getString("tipo")),
                                rs.getDouble("valor"),
                                LocalDateTime.parse(rs.getString("data_hora")),
                                rs.getString("conta_relacionada")));
                    }
                }
            }
        }
        return banco;
    }

    // Cria o objeto da subclasse certa a partir da linha da tabela "conta"
    private static Conta montarConta(ResultSet rs) throws SQLException {
        String numero = rs.getString("numero");
        String tipo = rs.getString("tipo");
        String titular = rs.getString("titular");
        double saldo = rs.getDouble("saldo");

        // Saldo inicial 0 para não criar de novo o "depósito inicial" no extrato
        Conta conta;
        if (tipo.equals(Banco.CORRENTE)) {
            conta = new ContaCorrente(numero, titular, 0, rs.getDouble("limite_cheque_especial"));
        } else if (tipo.equals(Banco.POUPANCA)) {
            conta = new ContaPoupanca(numero, titular, 0, rs.getDouble("taxa_rendimento"));
        } else if (tipo.equals(Banco.SALARIO)) {
            conta = new ContaSalario(numero, titular, 0);
        } else {
            throw new SQLException("Tipo de conta desconhecido no banco de dados: " + tipo);
        }
        conta.setSaldo(saldo); // o saldo gravado já inclui todas as transações
        return conta;
    }

    // Grava o Banco inteiro. Apaga as tabelas e insere tudo de novo, dentro de UMA transação:
    // ou grava tudo, ou não grava nada (rollback). Para poucos dados, é simples e seguro.
    // [VÍDEO] Grava dentro de UMA transação: commit se tudo deu certo, rollback se algo falhou.
    //        PreparedStatement com '?' evita erros de texto e SQL injection.
    public static void salvar(Banco banco) throws SQLException {
        try (Connection con = conectar()) {
            criarTabelas(con);
            con.setAutoCommit(false); // começa a transação

            try {
                try (Statement st = con.createStatement()) {
                    st.executeUpdate("DELETE FROM transacao");
                    st.executeUpdate("DELETE FROM conta");
                }

                // "?" são parâmetros: PreparedStatement evita erros de texto e SQL injection
                try (PreparedStatement psConta = con.prepareStatement(
                        "INSERT INTO conta (numero, tipo, titular, saldo, "
                        + "limite_cheque_especial, taxa_rendimento) VALUES (?, ?, ?, ?, ?, ?)");
                        PreparedStatement psTransacao = con.prepareStatement(
                                "INSERT INTO transacao (conta_numero, tipo, valor, data_hora, "
                                + "conta_relacionada) VALUES (?, ?, ?, ?, ?)")) {

                    for (Conta conta : banco.getContas()) {
                        psConta.setString(1, conta.getNumero());
                        psConta.setString(2, conta.getTipoConta());
                        psConta.setString(3, conta.getTitular());
                        psConta.setDouble(4, conta.getSaldo());

                        // Colunas específicas: ficam NULL quando não se aplicam ao tipo da conta
                        if (conta instanceof ContaCorrente) {
                            psConta.setDouble(5, ((ContaCorrente) conta).getLimiteChequeEspecial());
                        } else {
                            psConta.setNull(5, Types.REAL);
                        }
                        if (conta instanceof ContaPoupanca) {
                            psConta.setDouble(6, ((ContaPoupanca) conta).getTaxaRendimento());
                        } else {
                            psConta.setNull(6, Types.REAL);
                        }
                        psConta.executeUpdate();

                        for (Transacao t : conta.getExtrato()) {
                            psTransacao.setString(1, conta.getNumero());
                            psTransacao.setString(2, t.getTipo().name());
                            psTransacao.setDouble(3, t.getValor());
                            psTransacao.setString(4, t.getDataHora().toString());
                            psTransacao.setString(5, t.getContaRelacionada());
                            psTransacao.executeUpdate();
                        }
                    }
                }
                con.commit(); // confirma tudo
            } catch (SQLException e) {
                con.rollback(); // desfaz tudo se algo falhou
                throw e;
            }
        }
    }
}

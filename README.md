# Mini Sistema Bancário em Java - AV2

Projeto da AV2 de Programação Orientada a Objetos - IF Baiano
Técnico em Informática 2026 - Módulo II
Professora: Roberta Mércia Rodrigues de Oliveira

Evolução do mini sistema bancário da AV1, com:
- Tratamento de exceções (exceções personalizadas e try/catch)
- Extrato bancário com `ArrayList<Transacao>`
- Cadastro de contas em `Map<String, Conta>` (classe `Banco`)
- Interface gráfica em Swing (menu, formulários e `JTable`)
- Persistência das contas e dos extratos em SQLite via JDBC (arquivo `banco.db`)

## Estrutura
- `banco.modelo`: Conta (abstrata), ContaCorrente, ContaPoupanca, ContaSalario, Transacao, Banco, Persistencia
- `banco.excecao`: SaldoInsuficienteException, OperacaoInvalidaException, ContaInexistenteException
- `banco.gui`: FormPrincipal, FormCadastroConta, FormOperacoes, FormExtrato, FormSobre, Util

## Como executar
1. Abra este projeto no NetBeans.
2. Execute a classe `banco.gui.FormPrincipal` (botão direito > Run File, ou Shift + F6).
3. Na primeira execução o sistema abre com 3 contas de exemplo: 001 (Corrente), 002 (Poupança) e 003 (Salário).
4. Os dados são salvos automaticamente em `banco.db` (na pasta de execução) a cada operação e carregados ao abrir. Na primeira execução, são criadas 3 contas de exemplo. Para voltar ao estado inicial, feche o programa e apague o arquivo `banco.db`.

O driver SQLite JDBC e sua dependência SLF4J ficam na pasta `lib`. Arquivos `banco.dat` de versões anteriores não são importados automaticamente.

Tecnologias: Java, Swing, SQLite, JDBC, NetBeans.

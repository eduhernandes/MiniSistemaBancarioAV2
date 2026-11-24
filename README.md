# Mini Sistema Bancário em Java - AV2

Projeto da AV2 de Programação Orientada a Objetos - IF Baiano
Técnico em Informática 2026 - Módulo II
Professora: Roberta Mércia Rodrigues de Oliveira

Evolução do mini sistema bancário da AV1, com:
- Tratamento de exceções (exceções personalizadas e try/catch)
- Extrato bancário com `ArrayList<Transacao>`
- Contas cadastradas em `Map<String, Conta>` (classe `Banco`)
- Interface gráfica em Swing (menu, formulários e `JTable`)
- Persistência dos dados em banco de dados SQLite (JDBC)

## Estrutura
- `src/banco/modelo`: Conta (abstrata), ContaCorrente, ContaPoupanca, ContaSalario, Transacao, Banco, BancoDados
- `src/banco/excecao`: SaldoInsuficienteException, OperacaoInvalidaException, ContaInexistenteException
- `src/banco/gui`: FormPrincipal, FormCadastroConta, FormOperacoes, FormExtrato, FormSobre, Util
- `lib/sqlite-jdbc-3.42.0.0.jar`: driver JDBC do SQLite (biblioteca de terceiros, projeto xerial/sqlite-jdbc)

## Como executar no NetBeans
1. Crie um projeto *Java Application* (desmarque "Create Main Class").
2. Copie a pasta `banco` para dentro de `Source Packages`.
3. Copie a pasta `lib` para a pasta do projeto (no mesmo nível de `src` e `build.xml`).
4. No NetBeans: botão direito em **Libraries** > **Add JAR/Folder** > escolha `lib/sqlite-jdbc-3.42.0.0.jar` (deixe marcado "Relative Path").
5. Execute a classe `banco.gui.FormPrincipal` (botão direito > Run File, ou Shift + F6).

Na primeira execução o sistema cria o arquivo `banco.db` (na pasta do projeto) e as 3 contas de exemplo: 001 (Corrente), 002 (Poupança) e 003 (Salário). Os dados são salvos a cada operação e carregados ao abrir. Para voltar ao estado inicial, feche o programa e apague o `banco.db`.

Se o driver não estiver nas bibliotecas, o sistema avisa e funciona sem salvar os dados.

Tecnologias: Java 8+, Swing, SQLite (JDBC), NetBeans.

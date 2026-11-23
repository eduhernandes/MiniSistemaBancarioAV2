# Mini Sistema Bancário em Java - AV2

Projeto da AV2 de Programação Orientada a Objetos - IF Baiano
Técnico em Informática 2026 - Módulo II
Professora: Roberta Mércia Rodrigues de Oliveira

Evolução do mini sistema bancário da AV1, com:
- Tratamento de exceções (exceções personalizadas e try/catch)
- Extrato bancário com `ArrayList<Transacao>`
- Cadastro de contas em `Map<String, Conta>` (classe `Banco`)
- Interface gráfica em Swing (menu, formulários e `JTable`)
- Persistência dos dados com serialização de objetos (arquivo `banco.dat`)

## Estrutura
- `banco.modelo`: Conta (abstrata), ContaCorrente, ContaPoupanca, ContaSalario, Transacao, Banco, Persistencia
- `banco.excecao`: SaldoInsuficienteException, OperacaoInvalidaException, ContaInexistenteException
- `banco.gui`: FormPrincipal, FormCadastroConta, FormOperacoes, FormExtrato, FormSobre, Util

## Como executar
1. No NetBeans, crie um projeto Java Application (sem classe principal) e copie a pasta `banco` para dentro de `Source Packages`.
2. Execute a classe `banco.gui.FormPrincipal` (botão direito > Run File, ou Shift + F6).
3. Na primeira execução o sistema abre com 3 contas de exemplo: 001 (Corrente), 002 (Poupança) e 003 (Salário).
4. Os dados são salvos automaticamente em `banco.dat` (na pasta do projeto) a cada operação e carregados ao abrir. Para voltar ao estado inicial, apague esse arquivo.

Tecnologias: Java, Swing, NetBeans.

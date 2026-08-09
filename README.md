# Sistema de Controle de Gastos

Backend para controle de gastos, receitas e investimentos pessoais ou familiares. A aplicação permite cadastrar, consultar, atualizar e excluir registros financeiros, além de acompanhar investimentos e indicadores em um dashboard.

---

## Objetivo

Oferecer um gerenciamento simples de movimentações financeiras, separando receitas e gastos, controlando investimentos em renda fixa e consolidando as informações financeiras em um dashboard.

---

## Funcionalidades

### Registros financeiros

Permite cadastrar registros com:

- tipo: `ENTRADA` ou `SAIDA`;
- categoria compatível com o tipo;
- descrição;
- valor;
- data, preenchida automaticamente quando não informada.

Também permite listar todos os registros e buscar por ID, categoria, tipo, descrição ou período, além de atualizar e excluir registros existentes.

### Investimentos

Permite cadastrar investimentos diretamente pelo endpoint `POST /scontg/investimentos` nas categorias:

- CDB;
- LCI;
- LCA;
- Poupança;
- Outros.

No cadastro direto, o investimento recebe automaticamente o tipo `INVESTIMENTO`. A aplicação também permite realizar aportes pelo endpoint `POST /scontg/registros/aportar`, que cria um investimento do tipo `APORTE` a partir do saldo disponível.

Os investimentos podem ser consultados por ID, categoria, tipo ou período, listados, atualizados e excluídos.

### Rendimentos e tributação

Para investimentos que rendem, a aplicação calcula:

- juros compostos com taxa mensal ou anual;
- IOF regressivo nos primeiros 30 dias;
- Imposto de Renda por faixa de prazo;
- isenção automática de IR para LCI, LCA, Poupança e Outros;
- rendimento e valor bruto calculados em tempo real, sem persistência desses valores.

Investimentos da categoria `OUTROS` não rendem: a taxa é ajustada automaticamente para zero e não há cobrança de IR.

### Saque de investimentos

Permite saque parcial ou total de um investimento, desde que o valor seja positivo e não ultrapasse o valor disponível. O saque cria automaticamente um registro de entrada na categoria `INVESTIMENTO`.

Também há uma prévia de saque em `GET /scontg/investimentos/{id}/previsao-saque`, com valor bruto, IOF, IR e valor líquido disponível.

### Dashboard

Fornece uma visão consolidada com:

- saldo total e mensal;
- receitas e gastos totais e mensais;
- investimentos totais e do mês atual;
- patrimônio total;
- rendimento total e rendimento das aplicações cadastradas no mês atual;
- gastos agrupados por categoria, no total e no mês atual.

---

## Regras de negócio

- Categorias de registro devem ser compatíveis com o tipo informado.
- Valores negativos são rejeitados.
- Aportes e saques devem ter valor maior que zero.
- A taxa de juros não pode ser negativa.
- A isenção de IR é definida automaticamente pela categoria do investimento.
- O valor originalmente aplicado não é alterado na atualização de um investimento; o saldo disponível muda após saque.
- Datas de registros e investimentos podem ser preenchidas automaticamente com a data atual.

---

## Tecnologias utilizadas

### Backend

- Java 21
- Spring Boot 4
- Spring Data JPA / Hibernate
- Bean Validation

### Banco de dados

- SQLite em execução local
- H2 para testes automatizados

### Ferramentas e bibliotecas

- Maven
- Lombok
- JUnit 5
- WebTestClient
- big-math para cálculos financeiros com `BigDecimal`

---

## Arquitetura

```text
Controller → Service → Repository → Banco de dados
```

---

## Conceitos aplicados

- Programação orientada a objetos
- Injeção de dependência
- CRUD
- Validações de regras de negócio
- Enumerações
- Persistência com JPA
- Arquitetura REST
- Testes automatizados de integração
- Cálculos financeiros com `BigDecimal`

---

## Status do projeto

```text
Versão atual: 1.4.3
Correção: valores negativos passaram a ser rejeitados em registros e investimentos.
```

---

## Autor

Pedro da Mata Santos

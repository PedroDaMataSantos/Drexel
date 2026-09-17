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

A aplicação possui suporte a investimentos de renda fixa nas categorias:

- CDB;
- LCI;
- LCA;
- Poupança;
- Outros.

Os aportes são realizados por `POST /scontg/registros/aportar`, que cria um investimento do tipo `APORTE` a partir do saldo disponível. A API também permite listar investimentos, consultá-los por ID, categoria, tipo de aporte ou período e excluí-los.

### Rendimentos e tributação

Para investimentos que rendem, a aplicação calcula:

- juros compostos com taxa mensal ou anual;
- IOF regressivo nos primeiros 30 dias;
- Imposto de Renda por faixa de prazo;
- isenção automática de IR para LCI, LCA, Poupança e Outros;
- rendimento e valor bruto calculados em tempo real, sem persistência desses valores.

Investimentos da categoria `OUTROS` não rendem: a taxa é ajustada automaticamente para zero e não há cobrança de IR.

### Saque de investimentos

As regras de saque parcial ou total validam que o valor seja positivo e não ultrapasse o saldo disponível. O cálculo considera valor bruto, IOF, IR e valor líquido. A exposição dessas operações por endpoint ainda não faz parte dos controllers atuais.

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

O projeto utiliza uma organização em camadas, com pacote-base `com.damatapedro.controle_gastos`:

```text
api/controller                 → endpoints REST
application/service            → casos de uso e regras de aplicação
application/dto                → contratos de entrada e saída
application/mapper             → conversão entre entidades e DTOs
application/calculation        → cálculos de rendimento, tributação e saque
domain/entity                  → entidades de negócio/JPA
domain/enumeration             → enumerações do domínio
infrastructure/repository      → persistência com Spring Data JPA
```

Fluxo principal:

```text
Controller → Application Service → Repository → Banco de dados
```

## Endpoints disponíveis

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/scontg/registros` | Cria um registro financeiro. |
| `POST` | `/scontg/registros/aportar` | Cria um aporte em renda fixa. |
| `GET` | `/scontg/registros` | Lista os registros. |
| `GET` | `/scontg/registros/{id}` | Busca um registro por ID. |
| `GET` | `/scontg/registros/categoria/{categoria}` | Filtra registros por categoria. |
| `GET` | `/scontg/registros/tipo/{tipo}` | Filtra registros por tipo. |
| `GET` | `/scontg/registros/descricao/{descricao}` | Filtra registros por descrição. |
| `GET` | `/scontg/registros/periodo?inicio=&fim=` | Filtra registros por período. |
| `PUT` | `/scontg/registros/{id}` | Atualiza um registro. |
| `DELETE` | `/scontg/registros/{id}` | Exclui um registro. |
| `GET` | `/scontg/investimentos` | Lista os investimentos. |
| `GET` | `/scontg/investimentos/{id}` | Busca um investimento por ID. |
| `GET` | `/scontg/investimentos/categoria/{categoria}` | Filtra investimentos por categoria. |
| `GET` | `/scontg/investimentos/tipo/{isAporte}` | Filtra investimentos por tipo de aporte. |
| `GET` | `/scontg/investimentos/periodo?inicio=&fim=` | Filtra investimentos por período. |
| `DELETE` | `/scontg/investimentos/{id}` | Exclui um investimento. |
| `GET` | `/scontg/dashboard` | Retorna os indicadores consolidados. |

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
Estrutura atual: arquitetura em camadas.
Pacote-base: com.damatapedro.controle_gastos.
Testes automatizados: 9 testes passando.
```

---

## Autor

Pedro da Mata Santos

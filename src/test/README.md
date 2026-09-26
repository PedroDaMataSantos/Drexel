# Testes dos services

Execute `mvn test` com Java 21. O banco de testes é H2 em memória.
Os testes de API usam MockMvc através de WebTestClient: passam pelos controllers,
serialização e persistência, sem abrir uma porta TCP. Não substituem testes de
rede/deploy.

## Cobertura de cenários

| Service | Métodos públicos | Cenários |
| --- | --- | --- |
| InvestimentoService | create, delete, findById | Sucesso; tipo não suportado; IDs inexistentes; validação na persistência |
| InvestimentoService | findAll, findByCategoria, findByPeriodo, findByIsAporte | Dados presentes e listas vazias; seleção por IDs e limites inclusivos do período |
| RegistroService | create, update, delete, findById | Sucesso; categoria incompatível; IDs inexistentes; preservação da data quando omitida e dos dados após rejeição |
| RegistroService | findAll, findByCategoria, findByPeriodo, findByTipo, findByDescricao | Dados presentes e consultas vazias |
| RegistroService | investir, validarCategoria_Tipo | Sucesso; saldo exato; saldo insuficiente; valores zero/negativos; categoria incompatível |
| RendaFixaService | create, update | Sucesso; data omitida; categoria nula/incompatível; valor inválido; atualização de ID inexistente |
| RendaFixaService | sacar, previsaoSaque | Sucesso; ID inexistente; saque integral/parcial; saldo insuficiente; zero/negativo; arredondamento e saques sucessivos; regressão tributária |
| DashboardService | todos os 17 métodos públicos | Agregações com dados e sem dados; total versus mês; categorias; aportes; montagem dos 14 campos da resposta |

Uma consulta sem resultados não é uma falha: retorna uma lista ou soma vazia.
O Dashboard não declara exceções de negócio próprias; seus cenários de ausência
de dados verificam zeros e mapas completos. Esta matriz não significa cobertura
exaustiva de entradas, falhas de infraestrutura ou 100% de branches.

## Regressão de produção mantida visível

`ServicesCenariosTest.saqueParcialNaoReiniciaIdadeTributariaDoSaldoRestante`
deve permanecer ativo. Cenário: CDB de 1000 a 20% anuais por 365 dias,
saque líquido de 582,50. Restam bruto 600 e principal 500. A previsão restante
esperada é IOF 0, IR 17,50 e líquido 582,50. Antes da correção do usuário,
o código produzia IOF 96, IR 0,90 e líquido 503,10 por usar a data do último
saque também para impostos. Após a correção, este teste passou.

`RendaFixaServiceSaqueTest.deveSimularAplicacaoEmJaneiroSaqueEmSetembroENovosJurosEmOutubro`
fixa as datas: aplicação em 01/01/2026, saque em 25/09/2026 e consulta em
25/10/2026. Usa o service e calculators reais, com repositórios simulados.
Para 1000 a 20% anuais, no modelo de 365 dias corridos do projeto, verifica
bruto 1142,67, IR 28,53 e líquido 1114,14 antes do saque. Ao sacar 557,07,
restam principal 500 e bruto 571,34. Em outubro, o bruto esperado é 579,97,
IR 15,99 e líquido 563,98. IOF zero em todas essas etapas.
As expectativas são valores independentes, não saídas reutilizadas do calculator.
Esse cenário não valida uma convenção bancária de CDI/dias úteis.

Fontes consultadas para as regras tributárias e permanência do saldo aplicado:
- https://cdn.c6bank.com.br/c6-site-docs/material-tecnico-cdb.pdf
- https://cms-assets-p.c6bank.com.br/uploads/termos-e-condicoes-cdb-resgate-automatico.pdf

## Contrato que ainda merece decisão de negócio

`rendimentoMensal` atualmente soma o rendimento acumulado de aplicações
**criadas no mês**, e não os rendimentos gerados no mês por toda a carteira.
O teste caracteriza esse filtro existente, sem afirmar que equivale ao segundo
conceito. Da mesma forma, o total investido usa o valor originalmente aplicado.

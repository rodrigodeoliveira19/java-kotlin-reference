# Task 12 - Testes

## Objetivo
Assegurar qualidade com cobertura de casos de sucesso, validação, persistência, concorrência e falhas operacionais.

## Casos de teste obrigatórios
- CSV válido.
- Cabeçalho incorreto.
- Quantidade incorreta de colunas.
- UUID inválido.
- Data inválida.
- Valor decimal inválido.
- Campo obrigatório ausente.
- Persistência em lotes.
- Rollback de transação.
- UUID duplicado.
- Falha de conexão com o PostgreSQL.
- Falha de movimentação no S3.
- Arquivo movido para `processado/` após sucesso.
- Arquivo movido para `erro/` após falha.
- Recuperação de falha entre o commit no banco e a movimentação do arquivo.

## Tipos de teste
- Testes unitários.
- Testes de integração com Testcontainers.
- Validação real do comportamento de batching e transações.

## Status atual
- Cobertura unitária implementada para CSV válido, cabeçalho, colunas, UUID, data, valor, campo obrigatório e fluxo de erro.
- Testes de integração do orquestrador e batch foram adicionados para validar persistência e rejeição de duplicados.
- A suíte principal está passando com `./gradlew test --no-daemon`.

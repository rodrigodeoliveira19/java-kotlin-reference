# Task 8 - Idempotência

## Objetivo
Evitar duplicações ao reprocessar arquivos e definir a política para registros já existentes.

## Requisitos
- O campo `id` será a chave primária da tabela.
- O sistema deve evitar duplicações quando um arquivo for reprocessado.
- Definir explicitamente se um UUID já existente será ignorado ou causará falha no processamento.
- Não depender apenas da movimentação de objetos no S3 para impedir duplicações.
- Considerar falhas que ocorram entre o commit no PostgreSQL e a movimentação do arquivo para `processado/`.

## Regra de decisão
- A política de idempotência precisa ser clara e consistente com a transação e a recuperação de falhas.

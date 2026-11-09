# Task 3 - Fluxo de processamento

## Objetivo
Definir o ciclo completo de importação do CSV, desde a descoberta do arquivo até o destino final.

## Fluxo principal
1. Identificar arquivos CSV em `entrada/`.
2. Reservar o arquivo para evitar processamento concorrente.
3. Movê-lo logicamente para `processamento/`.
4. Ler o CSV em streaming.
5. Validar cabeçalho, quantidade de colunas, UUIDs, datas, valores e campos obrigatórios.
6. Converter cada linha em um objeto Java.
7. Persistir os registros no PostgreSQL utilizando Micronaut Data JPA e Hibernate.
8. Utilizar batching JDBC e transações por lote, com configuração e testes que comprovem seu funcionamento.
9. Após a conclusão bem-sucedida, mover o arquivo para `processado/`.
10. Em caso de falha, registrar o erro e tentar mover o arquivo para `erro/`.

## Regra de movimentação
- Nunca excluir a origem antes de confirmar a cópia do objeto.

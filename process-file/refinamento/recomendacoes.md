# Estratégias para processamento de arquivos CSV

## Necessidade e escolha recomendada

| Necessidade | Escolha recomendada |
|---|---|
| Um arquivo grande por vez e tempo de processamento aceitável | Uma instância processadora |
| API com várias réplicas, mas um único processamento global por vez | ShedLock |
| Vários arquivos independentes para processar simultaneamente | Fila ou tabela de tarefas com workers |
| Um único arquivo precisa ser processado muito mais rápido | Particionamento e paralelismo real |
| Retomar após falhas sem recomeçar tudo | Estado persistido e processamento idempotente |

## Recomendação final

Não adicione várias instâncias somente porque cada CSV contém 1 milhão de linhas.

Primeiro, implemente:

1. **Processamento em streaming:** leia o arquivo progressivamente, evitando carregar todo o conteúdo na memória.
2. **Inserção eficiente no PostgreSQL:** utilize JDBC Batch ou `COPY`, conforme as necessidades de validação e transformação dos dados.
3. **Retomada segura:** persista o estado do processamento para recuperar arquivos ou lotes após falhas.
4. **Idempotência:** evite duplicidades quando um arquivo ou lote precisar ser processado novamente.
5. **Monitoramento de desempenho:** meça o tempo de leitura, validação e persistência para identificar os gargalos.

Depois, avalie os resultados e escale a aplicação conforme a necessidade.

**Importante:** o ShedLock evita a execução concorrente de uma tarefa agendada entre instâncias que compartilham o mesmo mecanismo de bloqueio. Ele não distribui automaticamente os registros de um arquivo entre vários workers.

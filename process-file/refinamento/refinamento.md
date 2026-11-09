# Projeto: Importador CSV com Java, Micronaut, JPA/Hibernate, PostgreSQL e AWS S3

Desenvolva uma aplicação backend utilizando Java e Micronaut para importar arquivos CSV enviados manualmente a um bucket S3 e persistir seus registros em uma tabela PostgreSQL.

## 1. Tecnologias

- Java LTS compatível com a versão estável escolhida do Micronaut.
- Micronaut Framework.
- Micronaut Data JPA.
- Hibernate ORM.
- PostgreSQL.
- Flyway para migrations.
- AWS SDK for Java v2.
- Apache Commons CSV.
- JUnit e Mockito.
- Testcontainers para testes de integração com PostgreSQL.

Não utilizar Spring Boot.

## 2. Organização do bucket S3

Utilizar os seguintes prefixos:

- `entrada/`: arquivos aguardando processamento.
- `processamento/`: arquivos em processamento.
- `processado/`: arquivos processados com sucesso.
- `erro/`: arquivos cujo processamento falhou.

Os arquivos serão enviados manualmente. Implementar um agendador Micronaut que consulte periodicamente `entrada/`.

O bucket, a região AWS, o intervalo do agendamento e o tamanho dos lotes deverão ser configuráveis externamente.

## 3. Fluxo de processamento

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

A movimentação no S3 deverá considerar que a operação normalmente consiste em copiar o objeto para outra chave e excluir a chave original. Nunca excluir a origem antes de confirmar a cópia.

## 4. Schema do CSV

Utilizar exatamente as seguintes sete colunas, nesta ordem:

```csv
id,id_cliente,nome,email,status,valor,data
550e8400-e29b-41d4-a716-446655440000,660e8400-e29b-41d4-a716-446655440000,Joao,joao@example.com,ATIVO,150.50,2026-10-27
```

O exemplo representa um registro fictício.

### Mapeamento para PostgreSQL

| Campo | Tipo PostgreSQL | Mapeamento Java |
|---|---|---|
| `id` | `UUID` | `java.util.UUID` |
| `id_cliente` | `UUID` | `java.util.UUID` |
| `nome` | `VARCHAR(255)` | `String` |
| `email` | `VARCHAR(255)` | `String` |
| `status` | `VARCHAR(30)` | `String` |
| `valor` | `NUMERIC(15,2)` | `java.math.BigDecimal` |
| `data` | `DATE` | `java.time.LocalDate` |

Considerar esses tipos como a definição inicial do schema. Não converter `valor` para `double`, pois `BigDecimal` é mais adequado para valores decimais que exigem precisão.

## 5. Entidade JPA

Criar uma entidade chamada `Registro`, mapeada para uma tabela PostgreSQL chamada `registros`.

Requisitos:

- Utilizar `@Entity` e `@Table(name = "registros")`.
- Utilizar `@Id` no campo `id`.
- Mapear `id` como UUID fornecido pelo CSV, sem gerar outro identificador.
- Mapear `id_cliente` como UUID, sem tratá-lo como chave primária.
- Mapear `valor` como `BigDecimal`, com precisão 15 e escala 2.
- Mapear `data` como `LocalDate`.
- Utilizar `@Column` explícito para cada coluna.
- Definir restrições de nulidade conforme as regras de validação.
- Não criar relacionamentos JPA para `id_cliente` sem confirmação de que existe uma entidade de cliente e uma chave estrangeira correspondente.

## 6. Leitura e validação

Implementar um componente de leitura usando Apache Commons CSV.

- Ler o arquivo em streaming.
- Validar que o cabeçalho contém exatamente as sete colunas esperadas, na ordem definida.
- Validar a quantidade de campos em cada registro.
- Validar UUIDs nos campos `id` e `id_cliente`.
- Converter `valor` para `BigDecimal`.
- Converter `data` no formato ISO `yyyy-MM-dd` para `LocalDate`.
- Validar os campos obrigatórios.
- Informar o número da linha e o motivo da falha.
- Não carregar o arquivo inteiro em memória.
- Definir uma política explícita para linhas inválidas.

## 7. Persistência com Hibernate

Utilizar Micronaut Data JPA e Hibernate.

- Criar um repositório para a entidade `Registro`.
- Implementar inserção em lotes.
- Configurar e validar o batching do Hibernate.
- Controlar o tamanho do contexto de persistência, utilizando `flush()` e `clear()` quando apropriado.
- Utilizar transações por lote, evitando uma única transação para arquivos enormes.
- Testar o comportamento real do batching.
- Tratar violações de chave primária e outras restrições do PostgreSQL.

Definir a política para UUIDs duplicados. Nesta versão, não realizar atualizações automáticas de registros existentes sem uma regra de negócio explícita.

## 8. Idempotência

O campo `id` será a chave primária da tabela.

O sistema deverá evitar duplicações quando um arquivo for reprocessado. Definir explicitamente se um UUID já existente será ignorado ou se causará falha no processamento.

Não depender apenas da movimentação dos objetos no S3 para impedir duplicações.

Considerar falhas que ocorram depois do commit no PostgreSQL e antes da movimentação do arquivo para `processado/`.

## 9. Tratamento de erros

Não criar uma tabela de controle de importações nesta primeira versão. Essa funcionalidade ficará para uma versão futura.

Registrar nos logs:

- Nome e chave S3 do arquivo.
- Etapa do processamento.
- Número da linha, quando aplicável.
- Tipo e mensagem do erro.
- Quantidade de registros processados, quando disponível.

Em caso de falha:

1. Interromper o fluxo conforme a política transacional definida.
2. Executar rollback do lote que falhou.
3. Registrar o erro.
4. Tentar mover o arquivo para `erro/`.
5. Nunca marcar um arquivo com falha como processado.
6. Preservar o arquivo caso a movimentação falhe.
7. Considerar que lotes anteriores podem já ter sido confirmados no banco.

Não assumir que o rollback de um lote desfaz os lotes anteriores.

## 10. Agendamento e concorrência

- Utilizar o agendamento do Micronaut.
- Evitar que o mesmo arquivo seja processado simultaneamente.
- Considerar múltiplas instâncias da aplicação.
- Não considerar a movimentação de objetos S3 uma garantia de exclusão mútua.
- Implementar uma estratégia de reserva e recuperação de arquivos interrompidos.

## 11. Segurança e configuração

- Utilizar a cadeia padrão de provedores de credenciais AWS.
- Não armazenar credenciais no código.
- Externalizar as configurações do S3 e do PostgreSQL.
- Utilizar Flyway para criar e versionar o schema.
- Evitar registrar dados sensíveis dos registros nos logs.

## 12. Testes

Criar testes unitários e de integração para:

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

## 13. Estrutura do código

Separar responsabilidades em componentes para:

- Descoberta de arquivos S3.
- Movimentação de arquivos S3.
- Leitura e validação do CSV.
- Orquestração da importação.s
- Entidade JPA.
- Repositório Micronaut Data.
- Configuração da aplicação.

## 14. Estratégia de implementação

Antes de implementar:

1. Apresentar a arquitetura e a estrutura de pacotes.
2. Informar as dependências e versões escolhidas.
3. Explicar as transações, o batching e a idempotência.
4. Apresentar as migrations e os testes.
5. Implementar por etapas pequenas e testáveis.

Não implementar a tabela de controle de importações nesta versão.
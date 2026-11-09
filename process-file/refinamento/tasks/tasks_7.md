# Task 7 - Persistência com Hibernate

## Objetivo
Garantir persistência eficiente e segura dos registros no PostgreSQL com batching e controle de transações.

## Requisitos
- Utilizar Micronaut Data JPA e Hibernate.
- Criar um repositório para a entidade `Registro`.
- Implementar inserção em lotes.
- Configurar e validar o batching do Hibernate.
- Controlar o tamanho do contexto de persistência usando `flush()` e `clear()` quando apropriado.
- Utilizar transações por lote, evitando uma única transação para arquivos enormes.
- Testar o comportamento real do batching.
- Tratar violações de chave primária e outras restrições do PostgreSQL.

## Política de negócio
- Definir explicitamente o comportamento para UUIDs duplicados.
- Nesta versão, não realizar atualizações automáticas de registros existentes sem regra de negócio definida.

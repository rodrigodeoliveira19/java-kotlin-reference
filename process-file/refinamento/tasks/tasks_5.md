# Task 5 - Entidade JPA

## Objetivo
Criar a entidade `Registro` e mapear corretamente a tabela PostgreSQL `registros`.

## Requisitos
- Utilizar `@Entity` e `@Table(name = "registros")`.
- Utilizar `@Id` no campo `id`.
- O campo `id` deve ser o UUID fornecido pelo CSV, sem geração automática.
- Mapear `id_cliente` como UUID, sem tratá-lo como chave primária.
- Mapear `valor` como `BigDecimal`, com precisão 15 e escala 2.
- Mapear `data` como `LocalDate`.
- Adicionar `@Column` explícito para cada coluna.
- Definir restrições de nulidade conforme as regras de validação.
- Não criar relacionamentos JPA para `id_cliente` sem confirmação de entidade de cliente e chave estrangeira.

## Restrições
- Não assumir que `id_cliente` é uma FK sem definição explícita.

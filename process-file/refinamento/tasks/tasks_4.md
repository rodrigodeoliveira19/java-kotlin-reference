# Task 4 - Schema do CSV e mapeamento para PostgreSQL

## Objetivo
Definir o formato do CSV de entrada e o schema inicial da tabela `registros`.

## CSV esperado
Utilizar exatamente as sete colunas, nesta ordem:

```csv
id,id_cliente,nome,email,status,valor,data
550e8400-e29b-41d4-a716-446655440000,660e8400-e29b-41d4-a716-446655440000,Joao,joao@example.com,ATIVO,150.50,2026-10-27
```

## Mapeamento do schema
| Campo | Tipo PostgreSQL | Mapeamento Java |
|---|---|---|
| `id` | `UUID` | `java.util.UUID` |
| `id_cliente` | `UUID` | `java.util.UUID` |
| `nome` | `VARCHAR(255)` | `String` |
| `email` | `VARCHAR(255)` | `String` |
| `status` | `VARCHAR(30)` | `String` |
| `valor` | `NUMERIC(15,2)` | `java.math.BigDecimal` |
| `data` | `DATE` | `java.time.LocalDate` |

## Observações
- O valor decimal deve ser representado por `BigDecimal`, não por `double`.
- O exemplo é apenas um registro fictício.

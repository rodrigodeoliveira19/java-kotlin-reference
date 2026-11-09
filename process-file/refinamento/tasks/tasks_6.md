# Task 6 - Leitura e validação do CSV

## Objetivo
Implementar leitura em streaming do arquivo CSV, validando cada linha antes de persistir no banco.

## Requisitos
- Usar Apache Commons CSV.
- Ler o arquivo em streaming.
- Validar que o cabeçalho contém exatamente as sete colunas esperadas e na ordem definida.
- Validar a quantidade de campos em cada registro.
- Validar UUIDs nos campos `id` e `id_cliente`.
- Converter `valor` para `BigDecimal`.
- Converter `data` no formato ISO `yyyy-MM-dd` para `LocalDate`.
- Validar os campos obrigatórios.
- Informar o número da linha e o motivo da falha.
- Não carregar o arquivo inteiro em memória.
- Definir uma política explícita para linhas inválidas.

## Observações
- A validação deve ser detalhada para facilitar diagnóstico e rastreio.
- A política de linhas inválidas deve ser documentada no código e nos testes.

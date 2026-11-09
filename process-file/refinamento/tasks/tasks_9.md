# Task 9 - Tratamento de erros

## Objetivo
Definir como o sistema deve registrar, interromper, recuperar e reportar falhas no processo de importação.

## Requisitos
- Não criar uma tabela de controle de importações nesta primeira versão.
- Registrar nos logs:
  - Nome e chave S3 do arquivo.
  - Etapa do processamento.
  - Número da linha, quando aplicável.
  - Tipo e mensagem do erro.
  - Quantidade de registros processados, quando disponível.
- Em caso de falha:
  1. Interromper o fluxo conforme a política transacional definida.
  2. Executar rollback do lote que falhou.
  3. Registrar o erro.
  4. Tentar mover o arquivo para `erro/`.
  5. Nunca marcar um arquivo com falha como processado.
  6. Preservar o arquivo caso a movimentação falhe.
  7. Considerar que lotes anteriores podem já ter sido confirmados no banco.

## Restrições
- Não assumir que o rollback de um lote desfaz todos os lotes anteriores.

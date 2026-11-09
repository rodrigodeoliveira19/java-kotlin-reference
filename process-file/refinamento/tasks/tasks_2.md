# Task 2 - Organização do bucket S3

## Objetivo
Definir a estrutura de armazenamento dos arquivos no bucket S3 e como a varredura de entrada será executada.

## Requisitos
- Utilizar os prefixos:
  - `entrada/`: arquivos aguardando processamento.
  - `processamento/`: arquivos em processamento.
  - `processado/`: arquivos processados com sucesso.
  - `erro/`: arquivos cujo processamento falhou.
- Implementar um agendador Micronaut que consulte periodicamente `entrada/`.
- Configurar externamente:
  - bucket S3
  - região AWS
  - intervalo do agendamento
  - tamanho dos lotes

## Observações
- Os arquivos serão enviados manualmente.
- A movimentação no S3 deve seguir a estratégia de copiar o objeto para outra chave e excluir a chave original somente após confirmar a cópia.

# Task 10 - Agendamento e concorrência

## Objetivo
Implementar o processamento periódico do bucket e garantir que o mesmo arquivo não seja processado simultaneamente em múltiplas instâncias.

## Requisitos
- Utilizar o agendamento do Micronaut.
- Evitar que o mesmo arquivo seja processado simultaneamente.
- Considerar múltiplas instâncias da aplicação.
- Não considerar a movimentação de objetos S3 como garantia de exclusão mútua.
- Implementar estratégia de reserva e recuperação de arquivos interrompidos.

## Estratégia implementada
- Redis + Shedlock para evitar execução concorrente do agendador em múltiplas instâncias.
- PostgreSQL para registrar a reserva/lease do arquivo e permitir recuperação após falha.
- O arquivo é reservado antes do processamento por `ArquivoProcessamentoService`, com `lease_expires_at` e identificador do processamento atual.
- Se a execução cair, a lease expira e a aplicação pode recuperar o arquivo em uma nova tentativa.
- O `CsvImportScheduler` chama a recuperação de arquivos expirados antes da varredura do bucket.

## Observações
- A concorrência deve ser tratada no nível de arquivo e serviço, não apenas no S3.
- A recuperação deve cobrir falhas de execução entre a seleção e o processamento final.
- A estratégia evita que objetos no S3 sejam tratados como exclusão mútua, e articula a execução do agendador com a reserva persistida em banco.

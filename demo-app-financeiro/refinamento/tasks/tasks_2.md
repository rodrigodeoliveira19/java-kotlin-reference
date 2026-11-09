# tasks_2.md

## Entregável 2 — Casos de uso e camada HTTP

### Objetivo
Definir a entrada da aplicação por meio de casos de uso e controllers REST, sem acoplar a camada HTTP diretamente às regras de negócio.

### Escopo
- Criar os ports de entrada:
  - `CreatePaymentUseCase`
  - `GetPaymentUseCase`
- Definir comandos e DTOs:
  - `CreatePaymentCommand`
  - `CreatePaymentRequest`
  - `PaymentResponse`
- Implementar o controller:
  - `PaymentController`
- Implementar o tratador global de exceções:
  - `GlobalExceptionHandler`
- Definir o endpoint de leitura individual do pagamento.

### Regras de implementação
- O controller deve converter HTTP -> DTO -> Command -> Use Case.
- O controller não deve conter lógica de decisão por meio de pagamento.
- O controller não deve realizar acesso direto a Redis, DynamoDB ou gateway externo.
- O endpoint deve receber o header `Idempotency-Key` e repassar para o caso de uso.

### Critérios de aceite
- Existe `POST /api/v1/payments` e `GET /api/v1/payments/{id}`.
- O request de criação contém os dados básicos do pagamento e o header de idempotência.
- O controller delega a execução para o caso de uso correto.
- Erros de validação e de domínio retornam respostas HTTP consistentes.

### Checklist de implementação
- [ ] Criar `CreatePaymentUseCase`.
- [ ] Criar `GetPaymentUseCase`.
- [ ] Definir `CreatePaymentCommand`.
- [ ] Definir DTO de request/response.
- [ ] Implementar `PaymentController`.
- [ ] Implementar `GlobalExceptionHandler`.
- [ ] Validar contratos de entrada e saída.
- [ ] Confirmar que o controller não acessa infraestrutura diretamente.

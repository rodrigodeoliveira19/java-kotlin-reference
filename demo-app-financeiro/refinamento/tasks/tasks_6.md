# tasks_6.md

## Entregável 6 — Infraestrutura, documentação e validação final

### Objetivo
Concluir a aplicação com infraestrutura local, documentação da API e testes que validem as regras de negócio e as integrações abordadas no refinamento.

### Escopo
- Configurar Docker Compose com:
  - LocalStack
  - Redis
- Definir `application.yml` com propriedades de Redis, DynamoDB e endpoints.
- Implementar documentação OpenAPI/Swagger para:
  - `POST /api/v1/payments`
  - `GET /api/v1/payments/{id}`
  - `POST /api/v1/payment-simulator/transactions`
- Implementar testes unitários para:
  - `PaymentService`
  - `PaymentProcessorFactory`
  - `CreditCardPaymentProcessor`
  - `DebitCardPaymentProcessor`
  - `PixPaymentProcessor`
  - `RedisIdempotencyAdapter`

### Regras de implementação
- A documentação deve mostrar status HTTP esperados, request body e header `Idempotency-Key`.
- Os testes devem verificar fluxo principal, duplicidade e seleção de processadores.
- A infraestrutura local deve permitir execução do serviço com Redis e DynamoDB locais.

### Critérios de aceite
- O ambiente local pode ser iniciado com Docker Compose.
- A API está documentada em Swagger/OpenAPI.
- Os testes unitários cobrem os cenários principais definidos no refinamento.
- O projeto está pronto para que o código da API seja gerado a partir destes entregáveis.

### Checklist de implementação
- [ ] Criar `docker-compose.yml` com LocalStack e Redis.
- [ ] Definir `application.yml` para ambiente local.
- [ ] Adicionar configuração do Spring para Redis.
- [ ] Adicionar OpenAPI/Swagger.
- [ ] Documentar endpoints principais.
- [ ] Implementar testes unitários do `PaymentService`.
- [ ] Implementar testes da `PaymentProcessorFactory`.
- [ ] Implementar testes dos processors.
- [ ] Implementar testes do adapter Redis.
- [ ] Executar a suíte de testes e garantir resultado estável.

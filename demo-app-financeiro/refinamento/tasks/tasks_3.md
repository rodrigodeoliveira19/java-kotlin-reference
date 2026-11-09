# tasks_3.md

## Entregável 3 — Estratégia de processamento e orquestração do caso de uso

### Objetivo
Implementar a lógica de seleção do processador por meio de pagamento e centralizar a orquestração do fluxo do pagamento no serviço de aplicação.

### Escopo
- Definir a interface `PaymentProcessor`.
- Implementar as estratégias:
  - `CreditCardPaymentProcessor`
  - `DebitCardPaymentProcessor`
  - `PixPaymentProcessor`
- Implementar `PaymentProcessorFactory` com resolução por `PaymentMethod`.
- Implementar `PaymentService` como `CreatePaymentUseCase`.
- Definir os output ports necessários para o serviço:
  - `PaymentRepository`
  - `PaymentGateway`
  - `IdempotencyRepository`
- Definir resultado do processamento (`PaymentResult`) e regras de atualização de status.

### Regras de implementação
- O `PaymentService` orquestra:
  1. criação do pagamento;
  2. tentativa de aquisição da idempotency key;
  3. busca do pagamento existente quando a chave já foi usada;
  4. persistência inicial;
  5. seleção do processador;
  6. chamada ao gateway;
  7. atualização do status e persistência final.
- A lógica condicional `if/when` de seleção por método de pagamento deve ficar fora do controller.
- O Factory deve ser extensível para novos meios de pagamento no futuro.

### Critérios de aceite
- O serviço seleciona o processador correto conforme `PaymentMethod`.
- A factory lança `UnsupportedPaymentMethodException` para método inválido.
- O fluxo do pagamento alterna corretamente entre `CREATED`, `PROCESSING`, `APPROVED`, `DECLINED`, `FAILED` e `PENDING`.
- O `PaymentService` não conhece detalhes de Redis, DynamoDB, HTTP ou parceiro externo.

### Checklist de implementação
- [ ] Criar `PaymentProcessor`.
- [ ] Criar `PaymentProcessorFactory`.
- [ ] Implementar processor de cartão de crédito.
- [ ] Implementar processor de cartão de débito.
- [ ] Implementar processor de PIX.
- [ ] Criar `PaymentService` com fluxo principal.
- [ ] Definir `PaymentResult` e atualização do estado da entidade.
- [ ] Validar seleção do processador pela factory.

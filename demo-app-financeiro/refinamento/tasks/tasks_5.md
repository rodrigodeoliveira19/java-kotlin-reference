# tasks_5.md

## Entregável 5 — Persistência em DynamoDB e integração com o parceiro

### Objetivo
Implementar a persistência principal dos pagamentos em DynamoDB e a comunicação síncrona com o parceiro externo usando OkHttp3.

### Escopo
- Criar o adaptador de persistência:
  - `DynamoPaymentRepository`
- Configurar DynamoDB:
  - `DynamoDbConfig`
- Definir a tabela `payments` com chave `PK = PAYMENT#{paymentId}`.
- Implementar a interface `PaymentRepository`.
- Implementar os adaptadores de gateway:
  - `PaymentGatewayAdapter`
  - `PaymentHttpClient`
- Definir o contrato do parceiro externo.
- Criar o simulador local do parceiro:
  - `PaymentSimulatorController`

### Regras de implementação
- O DynamoDB será a fonte de verdade do estado do pagamento.
- O Redis deve ser usado somente para idempotência e controle de duplicidade.
- O adaptador HTTP deve encapsular detalhes técnicos de HTTP, timeout, headers e serialização.
- O `PaymentGatewayAdapter` deve mapear o domínio para o contrato do parceiro.
- O endpoint do simulador deve comportar-se como um parceiro externo real, acessado via HTTP/OkHttp.

### Critérios de aceite
- O pagamento é salvo no DynamoDB com status e metadados principais.
- O `PaymentService` consegue persistir e recuperar pagamentos por `id`.
- O gateway externo é chamado por cada processador específico.
- O simulador responde à requisição HTTP do microsserviço sem bypassar o `OkHttp3`.
- O fluxo real de integração é testável em ambiente local.

### Checklist de implementação
- [ ] Criar `PaymentRepository`.
- [ ] Implementar `DynamoPaymentRepository`.
- [ ] Criar configuração do DynamoDB local.
- [ ] Definir chave e estrutura da tabela `payments`.
- [ ] Implementar `PaymentGateway`.
- [ ] Implementar `PaymentGatewayAdapter`.
- [ ] Implementar `PaymentHttpClient` com OkHttp3.
- [ ] Implementar `PaymentSimulatorController`.
- [ ] Validar o fluxo HTTP do pagamento até o simulador.

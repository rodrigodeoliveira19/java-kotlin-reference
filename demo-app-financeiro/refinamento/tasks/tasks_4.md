# tasks_4.md

## Entregável 4 — Idempotência com Redis

### Objetivo
Implementar a camada de idempotência usando Redis de forma atômica e eficiente, sem depender de `ObjectMapper` ou de um `RedisConfig` customizado para a primeira versão.

### Escopo
- Definir `IdempotencyRepository`.
- Implementar `RedisIdempotencyAdapter` com `StringRedisTemplate`.
- Usar a operação atômica de criação de chave:
  - `setIfAbsent()`
  - equivalência conceitual a `SET key value NX EX 86400`
- Definir prefixo da chave e TTL de 24 horas.
- Garantir retorno do pagamento existente quando a mesma `Idempotency-Key` for reutilizada.

### Regras de implementação
- O Redis armazena apenas `String -> String`:
  - `idempotency:<key> -> paymentId`
- Não deve haver `ObjectMapper` no Redis nesta versão.
- Não deve haver `RedisConfig` customizado desnecessário.
- O `PaymentService` deve utilizar `tryAcquire()` e `getPaymentId()` sem conhecer as regras do Redis.

### Critérios de aceite
- Uma mesma `Idempotency-Key` só permite um processamento inicial.
- A segunda requisição com a mesma chave não processa novamente.
- O TTL da chave é configurado para 24 horas.
- O adapter foi testado para:
  - chave inexistente;
  - chave já existente;
  - leitura do `paymentId` associado.

### Checklist de implementação
- [ ] Criar `IdempotencyRepository`.
- [ ] Implementar `RedisIdempotencyAdapter`.
- [ ] Definir prefixo `idempotency:` e TTL de 24h.
- [ ] Garantir uso de `StringRedisTemplate`.
- [ ] Conectar o adapter ao `PaymentService`.
- [ ] Implementar testes unitários do adapter.
- [ ] Validar a lógica de duplicidade para a mesma chave.

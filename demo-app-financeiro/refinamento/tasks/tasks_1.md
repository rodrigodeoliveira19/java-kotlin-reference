# tasks_1.md

## Entregável 1 — Base do projeto e arquitetura hexagonal

### Objetivo
Estabelecer a estrutura inicial da aplicação e a separação de responsabilidades de acordo com o refinamento.

### Escopo
- Criar o projeto Kotlin + Spring Boot com as dependências mínimas:
  - Spring Web
  - Spring Data Redis
  - AWS SDK / DynamoDB
  - OkHttp3
  - JUnit 5
  - Mockito
  - OpenAPI/Swagger
- Definir a estrutura de pacotes:
  - `adapter/input/http`
  - `adapter/output/database/dynamodb`
  - `adapter/output/cache/redis`
  - `adapter/output/gateway/payment`
  - `application/port/input`
  - `application/port/output`
  - `application/service`
  - `domain/entity`
  - `domain/enum`
  - `domain/exception`
- Implementar os tipos de domínio:
  - `PaymentMethod`
  - `PaymentStatus`
  - `Payment`
- Implementar exceções de domínio:
  - `PaymentNotFoundException`
  - `UnsupportedPaymentMethodException`

### Regras de implementação
- `domain` não pode conhecer Spring, Redis, DynamoDB, OkHttp ou qualquer tecnologia externa.
- `application` deve conhecer apenas interfaces (`ports`).
- `adapter` deve implementar essas interfaces.

### Critérios de aceite
- O projeto compila com a estrutura esperada.
- Os enums e a entidade `Payment` existem com os campos e status principais definidos no refinamento.
- A arquitetura de pacotes está alinhada com a visão hexagonal.
- A entidade `Payment` possui comportamentos básicos como `startProcessing()` e `approve()`.

### Checklist de implementação
- [ ] Configurar `build.gradle.kts` / `pom.xml` conforme a stack.
- [ ] Criar os pacotes principais da arquitetura.
- [ ] Implementar `PaymentMethod`.
- [ ] Implementar `PaymentStatus`.
- [ ] Implementar `Payment` com campos e comportamentos mínimos.
- [ ] Implementar exceções de domínio.
- [ ] Validar a compilação inicial do projeto.

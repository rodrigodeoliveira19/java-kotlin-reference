Claro. Vou consolidar o refinamento considerando as decisões que tomamos: **Hexagonal**, `adapter` como pacote principal dos adaptadores, **Factory + Strategy para os meios de pagamento**, **OkHttp3**, **DynamoDB**, **Redis para idempotência com operação atômica**, sem `requestHash`, e sem `RedisConfig` desnecessário.

# Refinamento — Payment Microservice

## 1. Objetivo

Construir um microsserviço de pagamentos para um Marketplace, responsável por processar transações utilizando:

* Cartão de crédito
* Cartão de débito
* PIX

O microsserviço deverá:

* receber solicitações de pagamento;
* garantir idempotência das requisições;
* persistir o estado do pagamento;
* encaminhar a transação para um parceiro externo;
* tratar diferentes meios de pagamento sem concentrar regras no Controller;
* disponibilizar um endpoint para simulação do parceiro;
* possuir testes unitários;
* disponibilizar documentação OpenAPI/Swagger.

---

# 2. Stack

```text
Kotlin
Spring Boot
Spring Web
Spring Data Redis
AWS SDK / DynamoDB
OkHttp3
JUnit 5
Mockito
OpenAPI / Swagger
Docker Compose
LocalStack
Redis
```

Arquitetura:

```text
                 ┌──────────────────────┐
                 │      Client          │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │ PaymentController    │
                 │   Adapter / Input    │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │   PaymentService     │
                 │     Application      │
                 └──────────┬───────────┘
                            │
                    ┌───────┴────────┐
                    │                │
                    ▼                ▼
              Idempotency       PaymentProcessor
                 Port                Factory
                    │                │
                    ▼                ▼
                 Redis        Credit/Debit/PIX
                                     │
                                     ▼
                              PaymentGateway
                                     │
                                     ▼
                              OkHttp3 Adapter
                                     │
                                     ▼
                              Payment Partner
```

---

# 3. Arquitetura Hexagonal

A estrutura de pacotes será:

```text
src/main/kotlin/com/demo/appfinanceiro

├── adapter
│   ├── input
│   │   └── http
│   │       ├── PaymentController.kt
│   │       ├── PaymentSimulatorController.kt
│   │       └── GlobalExceptionHandler.kt
│   │
│   └── output
│       ├── database
│       │   └── dynamodb
│       │       ├── DynamoPaymentRepository.kt
│       │       └── DynamoDbConfig.kt
│       │
│       ├── cache
│       │   └── redis
│       │       └── RedisIdempotencyAdapter.kt
│       │
│       └── gateway
│           └── payment
│               ├── PaymentGatewayAdapter.kt
│               └── PaymentHttpClient.kt
│
├── application
│   ├── port
│   │   ├── input
│   │   │   ├── CreatePaymentUseCase.kt
│   │   │   └── GetPaymentUseCase.kt
│   │   │
│   │   └── output
│   │       ├── PaymentRepository.kt
│   │       ├── PaymentGateway.kt
│   │       └── IdempotencyRepository.kt
│   │
│   └── service
│       ├── PaymentService.kt
│       ├── PaymentProcessor.kt
│       ├── PaymentProcessorFactory.kt
│       ├── CreditCardPaymentProcessor.kt
│       ├── DebitCardPaymentProcessor.kt
│       └── PixPaymentProcessor.kt
│
├── domain
│   ├── entity
│   │   └── Payment.kt
│   │
│   ├── enum
│   │   ├── PaymentMethod.kt
│   │   └── PaymentStatus.kt
│   │
│   ├── exception
│   │   ├── PaymentNotFoundException.kt
│   │   └── UnsupportedPaymentMethodException.kt
│   │
│   └── valueobject
│
└── PaymentApplication.kt
```

A principal decisão aqui é:

> **`domain` não conhece Spring, Redis, DynamoDB, OkHttp ou qualquer tecnologia externa.**

A aplicação conhece apenas as interfaces (`ports`).

Os adapters implementam essas interfaces.

---

# 4. Domínio

## PaymentMethod

```kotlin
enum class PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    PIX
}
```

## PaymentStatus

```kotlin
enum class PaymentStatus {
    CREATED,
    PROCESSING,
    APPROVED,
    DECLINED,
    FAILED,
    CANCELLED,
    PENDING
}
```

O `PENDING` é particularmente útil para PIX, caso o parceiro não confirme imediatamente a transação.

---

# 5. Entidade Payment

Exemplo:

```kotlin
data class Payment(
    val id: String,
    val orderId: String,
    val customerId: String,
    val amount: BigDecimal,
    val currency: String,
    val paymentMethod: PaymentMethod,
    var status: PaymentStatus,
    var providerTransactionId: String? = null,
    val createdAt: Instant,
    var updatedAt: Instant
)
```

A entidade pode possuir comportamentos próprios:

```kotlin
fun startProcessing() {
    status = PaymentStatus.PROCESSING
    updatedAt = Instant.now()
}
```

e:

```kotlin
fun approve(providerTransactionId: String) {
    status = PaymentStatus.APPROVED
    this.providerTransactionId = providerTransactionId
    updatedAt = Instant.now()
}
```

Isso evita colocar todas as regras de negócio no `PaymentService`.

---

# 6. Input Port

O Controller não conversa diretamente com o `PaymentRepository`, Redis ou Gateway.

Ele conhece apenas o caso de uso:

```kotlin
interface CreatePaymentUseCase {

    fun execute(
        command: CreatePaymentCommand
    ): Payment
}
```

O comando poderia ser:

```kotlin
data class CreatePaymentCommand(
    val orderId: String,
    val customerId: String,
    val amount: BigDecimal,
    val currency: String,
    val paymentMethod: PaymentMethod,
    val idempotencyKey: String
)
```

---

# 7. Controller

O Controller fica responsável por HTTP:

```text
HTTP
 ↓
DTO
 ↓
Command
 ↓
Use Case
```

Ele **não deve possuir**:

```kotlin
when (paymentMethod) { ... }
```

nem:

```kotlin
if (redis.exists(...))
```

nem:

```kotlin
dynamoDB.putItem(...)
```

Essas responsabilidades pertencem a outras camadas.

Exemplo conceitual:

```kotlin
@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val createPaymentUseCase: CreatePaymentUseCase
) {

    @PostMapping
    fun create(
        @RequestHeader("Idempotency-Key")
        idempotencyKey: String,

        @RequestBody
        request: CreatePaymentRequest
    ): ResponseEntity<PaymentResponse> {

        val command = request.toCommand(idempotencyKey)

        val payment = createPaymentUseCase.execute(command)

        return ResponseEntity.ok(
            payment.toResponse()
        )
    }
}
```

---

# 8. Estratégia para os meios de pagamento

Aqui entra uma das principais decisões do refinamento.

Não queremos:

```kotlin
if (paymentMethod == CREDIT_CARD) {
    ...
} else if (paymentMethod == DEBIT_CARD) {
    ...
} else if (paymentMethod == PIX) {
    ...
}
```

E muito menos esse `when` dentro do Controller.

Utilizaremos **Strategy + Factory**.

## PaymentProcessor

```kotlin
interface PaymentProcessor {

    fun supports(
        paymentMethod: PaymentMethod
    ): Boolean

    fun process(
        payment: Payment
    ): PaymentResult
}
```

---

## CreditCardPaymentProcessor

```kotlin
@Component
class CreditCardPaymentProcessor(
    private val paymentGateway: PaymentGateway
) : PaymentProcessor {

    override fun supports(
        paymentMethod: PaymentMethod
    ) = paymentMethod == PaymentMethod.CREDIT_CARD

    override fun process(
        payment: Payment
    ): PaymentResult {
        return paymentGateway.processCreditCard(payment)
    }
}
```

O mesmo conceito será utilizado para:

```text
DebitCardPaymentProcessor
PixPaymentProcessor
```

---

# 9. Factory

A Factory recebe todas as estratégias disponibilizadas pelo Spring:

```kotlin
@Component
class PaymentProcessorFactory(
    private val processors: List<PaymentProcessor>
) {

    fun getProcessor(
        paymentMethod: PaymentMethod
    ): PaymentProcessor {

        return processors.firstOrNull {
            it.supports(paymentMethod)
        } ?: throw UnsupportedPaymentMethodException(
            "Unsupported payment method: $paymentMethod"
        )
    }
}
```

Isso permite adicionar futuramente:

```text
APPLE_PAY
GOOGLE_PAY
BOLETO
PAYPAL
...
```

sem transformar o Controller em um enorme `when`.

---

# 10. Output Ports

A aplicação define as dependências externas através de interfaces.

## Repository

```kotlin
interface PaymentRepository {

    fun save(payment: Payment): Payment

    fun findById(id: String): Payment?
}
```

## Gateway

```kotlin
interface PaymentGateway {

    fun processCreditCard(
        payment: Payment
    ): PaymentResult

    fun processDebitCard(
        payment: Payment
    ): PaymentResult

    fun processPix(
        payment: Payment
    ): PaymentResult
}
```

## Idempotência

```kotlin
interface IdempotencyRepository {

    fun tryAcquire(
        key: String,
        paymentId: String
    ): Boolean

    fun getPaymentId(
        key: String
    ): String?
}
```

O ponto importante é:

```text
Application
    ↓
IdempotencyRepository
    ↓
RedisIdempotencyAdapter
    ↓
Redis
```

A aplicação **não sabe que é Redis**.

---

# 11. Idempotência com Redis

A ideia é utilizar:

```text
Idempotency-Key
```

enviado pelo cliente.

Exemplo:

```http
POST /api/v1/payments
Idempotency-Key: 8f92a123
```

No Redis:

```text
idempotency:8f92a123
        ↓
PAYMENT-001
```

## Por que Redis?

Porque precisamos fazer uma verificação extremamente rápida e, principalmente, evitar a condição de corrida:

```text
Request A              Request B

exists(key)?           exists(key)?
     ↓                      ↓
   false                  false
     ↓                      ↓
 processa              processa
```

Isso poderia resultar em **duas transações de pagamento**.

---

# 12. Operação atômica

Não vamos fazer:

```kotlin
if (!redis.exists(key)) {
    redis.set(key, paymentId)
}
```

São duas operações.

Vamos utilizar:

```kotlin
setIfAbsent()
```

que corresponde conceitualmente ao Redis:

```text
SET key value NX EX 86400
```

`NX` significa:

> Só crie a chave se ela ainda não existir.

Assim:

```text
Request A

SET idempotency:ABC PAYMENT-001 NX
       ↓
      TRUE
       ↓
   pode processar
```

Enquanto simultaneamente:

```text
Request B

SET idempotency:ABC PAYMENT-002 NX
       ↓
      FALSE
       ↓
não deve processar novamente
```

Essa é uma propriedade importante de concorrência do Redis.

---

# 13. Redis Adapter

Como estamos armazenando somente:

```text
String → String
```

não precisamos de `ObjectMapper`.

Também não precisamos criar um `RedisConfig` apenas para esse caso.

Podemos utilizar `StringRedisTemplate`.

```kotlin
@Component
class RedisIdempotencyAdapter(
    private val redisTemplate: StringRedisTemplate
) : IdempotencyRepository {

    companion object {
        private const val KEY_PREFIX = "idempotency:"
        private val TTL = Duration.ofHours(24)
    }

    override fun tryAcquire(
        key: String,
        paymentId: String
    ): Boolean {

        return redisTemplate.opsForValue()
            .setIfAbsent(
                "$KEY_PREFIX$key",
                paymentId,
                TTL
            ) ?: false
    }

    override fun getPaymentId(
        key: String
    ): String? {

        return redisTemplate.opsForValue()
            .get("$KEY_PREFIX$key")
    }
}
```

Portanto:

```text
Redis
   ↓
StringRedisTemplate
   ↓
RedisIdempotencyAdapter
   ↓
IdempotencyRepository
   ↓
PaymentService
```

---

# 14. Por que não usar ObjectMapper?

Neste momento, o Redis armazena:

```text
idempotency:ABC123 → PAYMENT-001
```

Não temos:

```json
{
  "paymentId": "PAYMENT-001",
  "status": "PROCESSING",
  "createdAt": "..."
}
```

Então não existe necessidade de JSON.

Consequentemente:

```text
ObjectMapper ❌
Redis JSON serializer ❌
RedisConfig customizado ❌
```

Para a primeira versão:

```text
StringRedisTemplate ✅
```

Se futuramente decidirmos armazenar um objeto completo no Redis, aí podemos introduzir um serializer JSON/Jackson. Nesse cenário, o `ObjectMapper` pode ser utilizado pelo serializer, sem necessariamente precisarmos chamá-lo manualmente.

---

# 15. PaymentService

O `PaymentService` orquestra o caso de uso:

```text
Create Payment
      │
      ▼
Criar Payment
      │
      ▼
Tentar adquirir Idempotency-Key
      │
      ├── FALSE → buscar pagamento existente
      │
      └── TRUE
           │
           ▼
       Persistir
           │
           ▼
      PROCESSING
           │
           ▼
   PaymentProcessorFactory
           │
           ▼
   Processor específico
           │
           ▼
     PaymentGateway
           │
           ▼
       Resultado
           │
           ▼
     Atualizar Payment
           │
           ▼
       DynamoDB
```

Conceitualmente:

```kotlin
@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val idempotencyRepository: IdempotencyRepository,
    private val paymentProcessorFactory: PaymentProcessorFactory
) : CreatePaymentUseCase {

    override fun execute(
        command: CreatePaymentCommand
    ): Payment {

        val payment = Payment.create(command)

        val acquired = idempotencyRepository.tryAcquire(
            key = command.idempotencyKey,
            paymentId = payment.id
        )

        if (!acquired) {

            val existingPaymentId =
                idempotencyRepository.getPaymentId(
                    command.idempotencyKey
                )

            return paymentRepository.findById(
                existingPaymentId
                    ?: throw PaymentNotFoundException(
                        "Payment not found"
                    )
            ) ?: throw PaymentNotFoundException(
                "Payment not found: $existingPaymentId"
            )
        }

        paymentRepository.save(payment)

        payment.startProcessing()

        val processor =
            paymentProcessorFactory.getProcessor(
                payment.paymentMethod
            )

        val result = processor.process(payment)

        payment.applyResult(result)

        return paymentRepository.save(payment)
    }
}
```

---

# 16. Comunicação com o parceiro

A comunicação será síncrona utilizando **OkHttp3**.

A arquitetura será:

```text
PaymentProcessor
       ↓
PaymentGateway
       ↓
PaymentGatewayAdapter
       ↓
PaymentHttpClient
       ↓
OkHttp3
       ↓
Payment Partner
```

O `PaymentHttpClient` fica responsável pelos detalhes técnicos:

```text
HTTP
Headers
Timeout
Serialization
Status codes
Response body
```

Enquanto o `PaymentGatewayAdapter` fica responsável pelo mapeamento entre o nosso domínio e o contrato do parceiro.

Isso evita espalhar detalhes de HTTP pela aplicação.

---

# 17. Simulador do parceiro

Como o projeto precisa simular o parceiro dentro do próprio microsserviço, teremos:

```kotlin
PaymentSimulatorController
```

Por exemplo:

```http
POST /api/v1/payment-simulator/transactions
```

O fluxo local será:

```text
PaymentController
       ↓
PaymentService
       ↓
PaymentProcessorFactory
       ↓
CreditCardPaymentProcessor
       ↓
PaymentGateway
       ↓
PaymentGatewayAdapter
       ↓
PaymentHttpClient
       ↓
OkHttp
       ↓
PaymentSimulatorController
```

Isso é importante porque estamos realmente testando a comunicação HTTP.

Não devemos fazer:

```text
PaymentService
       ↓
PaymentSimulatorController
```

diretamente.

O OkHttp deve participar do fluxo para simular o comportamento de uma integração externa.

---

# 18. DynamoDB

O DynamoDB será a persistência principal dos pagamentos.

Tabela:

```text
payments
```

Chave:

```text
PK = PAYMENT#{paymentId}
```

Exemplo:

```text
PK: PAYMENT#12345

paymentId: 12345
orderId: ORDER-999
customerId: CUSTOMER-123
amount: 149.90
currency: BRL
paymentMethod: CREDIT_CARD
status: APPROVED
providerTransactionId: TX-987
createdAt: ...
updatedAt: ...
```

O DynamoDB será o **source of truth do estado do pagamento**.

Redis será utilizado como camada rápida de idempotência/concurrency control.

---

# 19. Redis × DynamoDB

Essa separação é importante:

| Responsabilidade                | Tecnologia |
| ------------------------------- | ---------- |
| Idempotency-Key                 | Redis      |
| Controle rápido de duplicidade  | Redis      |
| TTL da chave                    | Redis      |
| Estado do pagamento             | DynamoDB   |
| Histórico/consulta do pagamento | DynamoDB   |
| Source of truth                 | DynamoDB   |

Não devemos transformar o Redis no banco principal do pagamento.

---

# 20. TTL da idempotência

Utilizaremos, inicialmente:

```text
24 horas
```

Exemplo:

```text
idempotency:ABC123
       ↓
PAYMENT-001
       ↓
TTL: 24h
```

Depois de 24 horas a chave poderá expirar.

O período pode posteriormente ser transformado em configuração:

```yaml
payment:
  idempotency:
    ttl-hours: 24
```

---

# 21. Docker Compose

Teremos LocalStack para DynamoDB e Redis:

```yaml
services:

  localstack:
    image: localstack/localstack:latest
    container_name: marketplace-localstack
    ports:
      - "4566:4566"
    environment:
      - SERVICES=dynamodb
      - DEBUG=0
    volumes:
      - "./localstack:/var/lib/localstack"
      - "/var/run/docker.sock:/var/run/docker.sock"

  redis:
    image: redis:latest
    container_name: marketplace-redis
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - "./redis-data:/data"
```

Configuração Spring:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

---

# 22. Uma preocupação importante sobre idempotência

A estratégia acima resolve muito bem o problema de:

```text
Cliente envia a mesma requisição duas vezes
```

Por exemplo:

```text
Request 1
   ↓
Redis SET NX → TRUE
   ↓
Processa pagamento


Request 2
   ↓
Redis SET NX → FALSE
   ↓
Não processa novamente
   ↓
Retorna pagamento existente
```

Mas existe uma situação importante:

```text
Request
   ↓
Redis SET NX
   ↓
TRUE
   ↓
DynamoDB
   ↓
Partner APPROVED
   ↓
💥 aplicação cai
   ↓
não atualizou DynamoDB
```

Nesse caso, Redis sozinho não garante **exactly-once end-to-end**.

Para uma implementação mais robusta futuramente, poderíamos evoluir para:

```text
PROCESSING
COMPLETED
FAILED
```

e utilizar mecanismos de recuperação/reconciliação e, idealmente, uma chave de idempotência também no parceiro.

**Para o refinamento atual, porém, manteria a solução simples com `SET NX + TTL`**, deixando essa evolução como uma preocupação de produção futura.

---

# 23. SOLID aplicado

### Single Responsibility

Cada componente possui uma responsabilidade:

```text
Controller
→ HTTP

PaymentService
→ Orquestra caso de uso

PaymentProcessor
→ Processamento específico

PaymentGatewayAdapter
→ Integração com parceiro

PaymentHttpClient
→ Comunicação HTTP

RedisIdempotencyAdapter
→ Idempotência

DynamoPaymentRepository
→ Persistência
```

### Open/Closed

Podemos adicionar:

```text
BoletoPaymentProcessor
ApplePayProcessor
PayPalProcessor
```

sem modificar o Controller.

### Liskov Substitution

Todas as implementações de:

```kotlin
PaymentProcessor
```

podem ser tratadas pela aplicação através da abstração.

### Interface Segregation

Interfaces pequenas:

```text
PaymentRepository
PaymentGateway
IdempotencyRepository
PaymentProcessor
```

em vez de uma interface gigante.

### Dependency Inversion

O `PaymentService` depende de:

```kotlin
PaymentRepository
PaymentGateway
IdempotencyRepository
```

e não diretamente de:

```text
DynamoDB
Redis
OkHttp
```

---

# 24. Testes

## Unitários

Devemos testar principalmente:

### PaymentService

```text
deve criar pagamento
deve impedir processamento duplicado
deve retornar pagamento existente para Idempotency-Key repetida
deve selecionar processor correto
deve atualizar status após aprovação
deve tratar pagamento recusado
```

### Factory

```text
deve retornar CreditCardPaymentProcessor
deve retornar DebitCardPaymentProcessor
deve retornar PixPaymentProcessor
deve lançar exceção para método não suportado
```

### Processors

```text
deve chamar gateway para cartão de crédito
deve chamar gateway para cartão de débito
deve chamar gateway para PIX
```

### Redis Adapter

```text
deve adquirir chave quando ela não existe
deve rejeitar chave já existente
deve retornar paymentId associado à chave
```

---

# 25. Swagger/OpenAPI

Endpoints principais:

```text
POST /api/v1/payments
GET  /api/v1/payments/{id}

POST /api/v1/payment-simulator/transactions
```

O endpoint de criação deve documentar:

```text
Idempotency-Key
Request body
Response
Status codes
```

Por exemplo:

```text
201 Created
400 Bad Request
409 Conflict
422 Unprocessable Entity
500 Internal Server Error
```

Os códigos exatos devem refletir as regras que implementarmos.

---

# 26. Resultado final da arquitetura

A solução fica organizada desta maneira:

```text
                         ┌─────────────────────┐
                         │       Client        │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ PaymentController   │
                         │    INPUT ADAPTER    │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   PaymentService    │
                         │     USE CASE        │
                         └──────┬────────┬─────┘
                                │        │
                   ┌────────────┘        └─────────────┐
                   ▼                                   ▼
          ┌─────────────────┐                ┌──────────────────┐
          │ Idempotency     │                │ ProcessorFactory │
          │ Repository      │                └────────┬─────────┘
          └────────┬────────┘                         │
                   │                         ┌────────┼─────────┐
                   ▼                         ▼        ▼         ▼
              ┌─────────┐                Credit    Debit      PIX
              │  Redis  │                Card      Card
              └─────────┘                   │        │         │
                                            └────────┼─────────┘
                                                     ▼
                                          ┌──────────────────┐
                                          │ PaymentGateway   │
                                          └────────┬─────────┘
                                                   ▼
                                          ┌──────────────────┐
                                          │ Gateway Adapter  │
                                          └────────┬─────────┘
                                                   ▼
                                             ┌──────────┐
                                             │ OkHttp3  │
                                             └────┬─────┘
                                                  ▼
                                        ┌────────────────────┐
                                        │ Payment Simulator  │
                                        │  INPUT ADAPTER     │
                                        └────────────────────┘


                         PaymentService
                              │
                              ▼
                    ┌───────────────────┐
                    │ PaymentRepository │
                    └─────────┬─────────┘
                              ▼
                         ┌──────────┐
                         │ DynamoDB │
                         └──────────┘
```

### Em resumo, as principais decisões do refinamento são:

**Hexagonal**
→ `domain` e `application` independentes de infraestrutura.

**Strategy + Factory**
→ seleção do processamento por meio de pagamento fora do Controller.

**Redis**
→ somente para idempotência/concurrency control inicialmente.

**`SET NX + TTL`**
→ aquisição atômica da `Idempotency-Key`.

**Sem `requestHash`**
→ simplificação deliberada nesta versão.

**Sem `RedisConfig`**
→ `StringRedisTemplate` é suficiente para `String → String`.

**Sem `ObjectMapper` no Redis**
→ não estamos armazenando objetos/JSON.

**DynamoDB**
→ fonte persistente do estado dos pagamentos.

**OkHttp3**
→ comunicação síncrona com o parceiro.

**Payment Simulator**
→ simula o contrato externo, mas passa pelo HTTP/OkHttp real.

**SOLID**
→ responsabilidades separadas e dependências invertidas.

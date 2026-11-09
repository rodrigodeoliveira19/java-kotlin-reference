package com.demo.appfinanceiro.adapter.input.http

import com.demo.appfinanceiro.application.port.input.CreatePaymentCommand
import com.demo.appfinanceiro.application.port.input.CreatePaymentUseCase
import com.demo.appfinanceiro.application.port.input.GetPaymentUseCase
import com.demo.appfinanceiro.domain.entity.Payment
import com.demo.appfinanceiro.domain.enum.PaymentMethod
import com.demo.appfinanceiro.domain.enum.PaymentStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.Instant

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val getPaymentUseCase: GetPaymentUseCase
) {

    @PostMapping
    @Operation(summary = "Create a payment")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Payment created successfully"),
            ApiResponse(responseCode = "400", description = "Bad request", content = [Content(schema = Schema(implementation = Map::class))]),
            ApiResponse(responseCode = "409", description = "Conflict", content = [Content(schema = Schema(implementation = Map::class))]),
            ApiResponse(responseCode = "422", description = "Invalid request payload", content = [Content(schema = Schema(implementation = Map::class))]),
            ApiResponse(responseCode = "500", description = "Internal server error", content = [Content(schema = Schema(implementation = Map::class))])
        ]
    )
    fun create(
        @RequestHeader("Idempotency-Key") idempotencyKey: String,
        @RequestBody request: CreatePaymentRequest
    ): ResponseEntity<PaymentResponse> {
        val command = CreatePaymentCommand(
            orderId = request.orderId,
            customerId = request.customerId,
            amount = request.amount,
            currency = request.currency,
            paymentMethod = request.paymentMethod,
            idempotencyKey = idempotencyKey
        )

        val payment = createPaymentUseCase.execute(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(payment.toResponse())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<PaymentResponse> {
        val payment = getPaymentUseCase.execute(id)
        return ResponseEntity.ok(payment.toResponse())
    }
}

data class CreatePaymentRequest(
    val orderId: String,
    val customerId: String,
    val amount: BigDecimal,
    val currency: String,
    val paymentMethod: PaymentMethod
)

data class PaymentResponse(
    val id: String,
    val orderId: String,
    val customerId: String,
    val amount: BigDecimal,
    val currency: String,
    val paymentMethod: PaymentMethod,
    val status: PaymentStatus,
    val providerTransactionId: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)

private fun Payment.toResponse(): PaymentResponse = PaymentResponse(
    id = id,
    orderId = orderId,
    customerId = customerId,
    amount = amount,
    currency = currency,
    paymentMethod = paymentMethod,
    status = status,
    providerTransactionId = providerTransactionId,
    createdAt = createdAt,
    updatedAt = updatedAt
)

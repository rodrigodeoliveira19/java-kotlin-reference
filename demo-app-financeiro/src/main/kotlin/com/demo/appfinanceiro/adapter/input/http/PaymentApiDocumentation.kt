package com.demo.appfinanceiro.adapter.input.http

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity

class PaymentApiDocumentation {
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
    fun createApiDoc(
        @Parameter(name = "Idempotency-Key", `in` = ParameterIn.HEADER, required = true)
        idempotencyKey: String,
        request: CreatePaymentRequest
    ): ResponseEntity<PaymentResponse> = ResponseEntity.ok().build()

    @Operation(summary = "Get payment by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Payment found"),
            ApiResponse(responseCode = "404", description = "Payment not found", content = [Content(schema = Schema(implementation = Map::class))]),
            ApiResponse(responseCode = "500", description = "Internal server error", content = [Content(schema = Schema(implementation = Map::class))])
        ]
    )
    fun getByIdApiDoc(@Parameter(description = "Payment identifier", required = true) id: String): ResponseEntity<PaymentResponse> = ResponseEntity.ok().build()
}

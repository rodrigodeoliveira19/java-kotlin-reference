package com.demo.appfinanceiro.application.port.input

import com.demo.appfinanceiro.domain.entity.Payment
import com.demo.appfinanceiro.domain.enum.PaymentMethod
import java.math.BigDecimal

interface CreatePaymentUseCase {
    fun execute(command: CreatePaymentCommand): Payment
}

data class CreatePaymentCommand(
    val orderId: String,
    val customerId: String,
    val amount: BigDecimal,
    val currency: String,
    val paymentMethod: PaymentMethod,
    val idempotencyKey: String
)

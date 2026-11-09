package com.demo.appfinanceiro.application.port.output

import com.demo.appfinanceiro.domain.entity.Payment

interface PaymentGateway {
    fun processCreditCard(payment: Payment): PaymentResult
    fun processDebitCard(payment: Payment): PaymentResult
    fun processPix(payment: Payment): PaymentResult
}

data class PaymentResult(
    val approved: Boolean,
    val providerTransactionId: String? = null,
    val status: String? = null,
    val message: String? = null
)

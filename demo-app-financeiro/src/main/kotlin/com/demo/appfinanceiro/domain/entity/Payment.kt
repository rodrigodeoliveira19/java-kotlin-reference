package com.demo.appfinanceiro.domain.entity

import com.demo.appfinanceiro.domain.enum.PaymentMethod
import com.demo.appfinanceiro.domain.enum.PaymentStatus
import java.math.BigDecimal
import java.time.Instant

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
) {
    fun startProcessing() {
        status = PaymentStatus.PROCESSING
        updatedAt = Instant.now()
    }

    fun approve(providerTransactionId: String) {
        status = PaymentStatus.APPROVED
        this.providerTransactionId = providerTransactionId
        updatedAt = Instant.now()
    }

    fun decline() {
        status = PaymentStatus.DECLINED
        updatedAt = Instant.now()
    }

    fun markFailed() {
        status = PaymentStatus.FAILED
        updatedAt = Instant.now()
    }

    fun cancel() {
        status = PaymentStatus.CANCELLED
        updatedAt = Instant.now()
    }
}

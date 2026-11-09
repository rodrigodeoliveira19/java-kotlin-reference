package com.demo.appfinanceiro.application.port.output

interface IdempotencyRepository {
    fun tryAcquire(key: String, paymentId: String): Boolean
    fun getPaymentId(key: String): String?
}

package com.demo.appfinanceiro.application.port.output

import com.demo.appfinanceiro.domain.entity.Payment

interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findById(id: String): Payment?
}

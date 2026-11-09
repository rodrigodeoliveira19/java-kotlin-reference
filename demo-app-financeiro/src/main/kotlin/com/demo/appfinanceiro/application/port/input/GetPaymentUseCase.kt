package com.demo.appfinanceiro.application.port.input

import com.demo.appfinanceiro.domain.entity.Payment

interface GetPaymentUseCase {
    fun execute(id: String): Payment
}

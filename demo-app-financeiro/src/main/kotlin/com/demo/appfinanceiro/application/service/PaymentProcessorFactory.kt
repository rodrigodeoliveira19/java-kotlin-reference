package com.demo.appfinanceiro.application.service

import com.demo.appfinanceiro.domain.enum.PaymentMethod
import com.demo.appfinanceiro.domain.exception.UnsupportedPaymentMethodException
import org.springframework.stereotype.Component

@Component //teste
class PaymentProcessorFactory(
    private val processors: List<PaymentProcessor>,
    private val processors2: Map<String, PaymentProcessor>

) {
    fun getProcessor(paymentMethod: PaymentMethod): PaymentProcessor {
        return processors.firstOrNull { it.supports(paymentMethod) }
            ?: throw UnsupportedPaymentMethodException("Unsupported payment method: $paymentMethod")

//        return processors2[paymentMethod.name]
//            ?: throw IllegalArgumentException("Unsupported payment method: $paymentMethod")
    }
}

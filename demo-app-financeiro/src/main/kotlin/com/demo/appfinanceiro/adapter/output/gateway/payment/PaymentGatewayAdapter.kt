package com.demo.appfinanceiro.adapter.output.gateway.payment

import com.demo.appfinanceiro.application.port.output.PaymentGateway
import com.demo.appfinanceiro.application.port.output.PaymentResult
import com.demo.appfinanceiro.domain.entity.Payment
import org.springframework.stereotype.Component

@Component
class PaymentGatewayAdapter(
    private val paymentHttpClient: PaymentHttpClient
) : PaymentGateway {

    override fun processCreditCard(payment: Payment): PaymentResult {
        return paymentHttpClient.send(
            payment = payment,
            type = "CREDIT_CARD"
        )
    }

    override fun processDebitCard(payment: Payment): PaymentResult {
        return paymentHttpClient.send(
            payment = payment,
            type = "DEBIT_CARD"
        )
    }

    override fun processPix(payment: Payment): PaymentResult {
        return paymentHttpClient.send(
            payment = payment,
            type = "PIX"
        )
    }
}

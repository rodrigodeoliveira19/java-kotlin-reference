package com.demo.appfinanceiro.application.service

import com.demo.appfinanceiro.application.port.output.PaymentGateway
import com.demo.appfinanceiro.application.port.output.PaymentResult
import com.demo.appfinanceiro.domain.entity.Payment
import com.demo.appfinanceiro.domain.enum.PaymentMethod
import org.springframework.stereotype.Component

/*
* CREDIT_CARD,
  DEBIT_CARD,
  PIX
* */
interface PaymentProcessor {
    fun supports(paymentMethod: PaymentMethod): Boolean
    fun process(payment: Payment): PaymentResult
}

@Component("CREDIT_CARD")
class CreditCardPaymentProcessor(
    private val paymentGateway: PaymentGateway
) : PaymentProcessor {
    override fun supports(paymentMethod: PaymentMethod): Boolean = paymentMethod == PaymentMethod.CREDIT_CARD

    override fun process(payment: Payment): PaymentResult = paymentGateway.processCreditCard(payment)
}

@Component("DEBIT_CARD")
class DebitCardPaymentProcessor(
    private val paymentGateway: PaymentGateway
) : PaymentProcessor {
    override fun supports(paymentMethod: PaymentMethod): Boolean = paymentMethod == PaymentMethod.DEBIT_CARD

    override fun process(payment: Payment): PaymentResult = paymentGateway.processDebitCard(payment)
}
@Component("PIX")
class PixPaymentProcessor(
    private val paymentGateway: PaymentGateway
) : PaymentProcessor {
    override fun supports(paymentMethod: PaymentMethod): Boolean = paymentMethod == PaymentMethod.PIX

    override fun process(payment: Payment): PaymentResult = paymentGateway.processPix(payment)
}

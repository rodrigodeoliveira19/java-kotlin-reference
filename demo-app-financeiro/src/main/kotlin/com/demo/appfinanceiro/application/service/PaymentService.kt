package com.demo.appfinanceiro.application.service

import com.demo.appfinanceiro.application.port.input.CreatePaymentCommand
import com.demo.appfinanceiro.application.port.input.CreatePaymentUseCase
import com.demo.appfinanceiro.application.port.input.GetPaymentUseCase
import com.demo.appfinanceiro.application.port.output.IdempotencyRepository
import com.demo.appfinanceiro.application.port.output.PaymentRepository
import com.demo.appfinanceiro.domain.entity.Payment
import com.demo.appfinanceiro.domain.enum.PaymentStatus
import com.demo.appfinanceiro.domain.exception.PaymentNotFoundException
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val idempotencyRepository: IdempotencyRepository,
    private val paymentProcessorFactory: PaymentProcessorFactory
) : CreatePaymentUseCase, GetPaymentUseCase {

    override fun execute(command: CreatePaymentCommand): Payment {
        val payment = Payment(
            id = "PAYMENT-${Instant.now().toEpochMilli()}",
            orderId = command.orderId,
            customerId = command.customerId,
            amount = command.amount,
            currency = command.currency,
            paymentMethod = command.paymentMethod,
            status = PaymentStatus.CREATED,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val acquired = idempotencyRepository.tryAcquire(command.idempotencyKey, payment.id)
        if (!acquired) {
            val existingPaymentId = idempotencyRepository.getPaymentId(command.idempotencyKey)
                ?: throw PaymentNotFoundException("Payment not found for idempotency key: ${command.idempotencyKey}")

            return paymentRepository.findById(existingPaymentId)
                ?: throw PaymentNotFoundException("Payment not found: $existingPaymentId")
        }

        paymentRepository.save(payment)
        payment.startProcessing()

        val processor = paymentProcessorFactory.getProcessor(payment.paymentMethod)
        val result = processor.process(payment)

        if (result.approved) {
            payment.approve(result.providerTransactionId ?: "TX-${payment.id}")
        } else {
            payment.decline()
        }

        return paymentRepository.save(payment)
    }

    override fun execute(id: String): Payment {
        return paymentRepository.findById(id)
            ?: throw PaymentNotFoundException("Payment not found: $id")
    }
}

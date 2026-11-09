package com.demo.appfinanceiro

import com.demo.appfinanceiro.adapter.input.http.CreatePaymentRequest
import com.demo.appfinanceiro.adapter.input.http.PaymentController
import com.demo.appfinanceiro.application.port.input.CreatePaymentCommand
import com.demo.appfinanceiro.application.port.input.CreatePaymentUseCase
import com.demo.appfinanceiro.application.port.input.GetPaymentUseCase
import com.demo.appfinanceiro.domain.entity.Payment
import com.demo.appfinanceiro.domain.enum.PaymentMethod
import com.demo.appfinanceiro.domain.enum.PaymentStatus
import com.demo.appfinanceiro.domain.exception.PaymentNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.time.Instant

class PaymentControllerTest {

    @Test
    fun `should create payment via controller`() {
        val createPaymentUseCase = mock(CreatePaymentUseCase::class.java)
        val getPaymentUseCase = mock(GetPaymentUseCase::class.java)
        val controller = PaymentController(createPaymentUseCase, getPaymentUseCase)

        val payment = Payment(
            id = "PAYMENT-1",
            orderId = "ORDER-1",
            customerId = "CUSTOMER-1",
            amount = BigDecimal("199.99"),
            currency = "BRL",
            paymentMethod = PaymentMethod.CREDIT_CARD,
            status = PaymentStatus.CREATED,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        `when`(createPaymentUseCase.execute(CreatePaymentCommand(
            orderId = "ORDER-1",
            customerId = "CUSTOMER-1",
            amount = BigDecimal("199.99"),
            currency = "BRL",
            paymentMethod = PaymentMethod.CREDIT_CARD,
            idempotencyKey = "key-1"
        ))).thenReturn(payment)

        val response = controller.create("key-1", CreatePaymentRequest(
            orderId = "ORDER-1",
            customerId = "CUSTOMER-1",
            amount = BigDecimal("199.99"),
            currency = "BRL",
            paymentMethod = PaymentMethod.CREDIT_CARD
        ))

        assertEquals(201, response.statusCode.value())
        assertNotNull(response.body)
        assertEquals("PAYMENT-1", response.body!!.id)
    }

    @Test
    fun `should return payment by id`() {
        val createPaymentUseCase = mock(CreatePaymentUseCase::class.java)
        val getPaymentUseCase = mock(GetPaymentUseCase::class.java)
        val controller = PaymentController(createPaymentUseCase, getPaymentUseCase)
        val payment = Payment(
            id = "PAYMENT-2",
            orderId = "ORDER-2",
            customerId = "CUSTOMER-2",
            amount = BigDecimal("50.00"),
            currency = "BRL",
            paymentMethod = PaymentMethod.PIX,
            status = PaymentStatus.PENDING,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        `when`(getPaymentUseCase.execute("PAYMENT-2")).thenReturn(payment)

        val response = controller.getById("PAYMENT-2")

        assertEquals(200, response.statusCode.value())
        assertEquals("PAYMENT-2", response.body!!.id)
    }

    @Test
    fun `should throw when payment is missing`() {
        val createPaymentUseCase = mock(CreatePaymentUseCase::class.java)
        val getPaymentUseCase = mock(GetPaymentUseCase::class.java)
        val controller = PaymentController(createPaymentUseCase, getPaymentUseCase)

        `when`(getPaymentUseCase.execute("NOT_FOUND")).thenThrow(PaymentNotFoundException("Payment not found"))

        try {
            controller.getById("NOT_FOUND")
        } catch (exception: PaymentNotFoundException) {
            assertEquals("Payment not found", exception.message)
            return
        }

        throw AssertionError("Expected PaymentNotFoundException to be thrown")
    }
}

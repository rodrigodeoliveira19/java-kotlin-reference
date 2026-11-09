package com.demo.appfinanceiro.adapter.output.gateway.payment

import com.demo.appfinanceiro.application.port.output.PaymentResult
import com.demo.appfinanceiro.domain.entity.Payment
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class PaymentHttpClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    fun send(payment: Payment, type: String): PaymentResult {
        val payload = """
            {
              "paymentId": "${payment.id}",
              "orderId": "${payment.orderId}",
              "customerId": "${payment.customerId}",
              "amount": "${payment.amount}",
              "currency": "${payment.currency}",
              "paymentMethod": "$type"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("http://localhost:8080/api/v1/payment-simulator/transactions")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .build()

        return client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: "{}"
            if (!response.isSuccessful) {
                return PaymentResult(
                    approved = false,
                    status = "FAILED",
                    message = "HTTP ${response.code}: $body"
                )
            }

            return PaymentResult(
                approved = true,
                providerTransactionId = "SIM-${payment.id}",
                status = "APPROVED",
                message = body
            )
        }
    }
}

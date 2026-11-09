package com.demo.appfinanceiro.adapter.output.database.dynamodb

import com.demo.appfinanceiro.application.port.output.PaymentRepository
import com.demo.appfinanceiro.domain.entity.Payment
import com.demo.appfinanceiro.domain.enum.PaymentMethod
import com.demo.appfinanceiro.domain.enum.PaymentStatus
import org.springframework.stereotype.Repository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.math.BigDecimal
import java.time.Instant

@Repository
class DynamoPaymentRepository(
    private val dynamoDbClient: DynamoDbClient
) : PaymentRepository {

    companion object {
        private const val TABLE_NAME = "payments"
        private const val PARTITION_KEY = "PK"
    }

    override fun save(payment: Payment): Payment {
        val item = mapOf(
            PARTITION_KEY to stringAttribute("PAYMENT#${payment.id}"),
            "paymentId" to stringAttribute(payment.id),
            "orderId" to stringAttribute(payment.orderId),
            "customerId" to stringAttribute(payment.customerId),
            "amount" to stringAttribute(payment.amount.toPlainString()),
            "currency" to stringAttribute(payment.currency),
            "paymentMethod" to stringAttribute(payment.paymentMethod.name),
            "status" to stringAttribute(payment.status.name),
            "providerTransactionId" to stringAttribute(payment.providerTransactionId ?: ""),
            "createdAt" to stringAttribute(payment.createdAt.toString()),
            "updatedAt" to stringAttribute(payment.updatedAt.toString())
        )

        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build()
        )

        return payment
    }

    override fun findById(id: String): Payment? {
        val response = dynamoDbClient.getItem(
            GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(
                    mapOf(
                        PARTITION_KEY to stringAttribute("PAYMENT#$id")
                    )
                )
                .build()
        )

        if (response.item().isNullOrEmpty()) {
            return null
        }

        val item = response.item()
        return Payment(
            id = item.stringValue("paymentId") ?: id,
            orderId = item.stringValue("orderId") ?: "",
            customerId = item.stringValue("customerId") ?: "",
            amount = BigDecimal(item.stringValue("amount") ?: "0"),
            currency = item.stringValue("currency") ?: "BRL",
            paymentMethod = PaymentMethod.valueOf(item.stringValue("paymentMethod") ?: PaymentMethod.CREDIT_CARD.name),
            status = PaymentStatus.valueOf(item.stringValue("status") ?: PaymentStatus.CREATED.name),
            providerTransactionId = item.stringValue("providerTransactionId")?.takeIf { it.isNotBlank() },
            createdAt = Instant.parse(item.stringValue("createdAt") ?: Instant.now().toString()),
            updatedAt = Instant.parse(item.stringValue("updatedAt") ?: Instant.now().toString())
        )
    }
}

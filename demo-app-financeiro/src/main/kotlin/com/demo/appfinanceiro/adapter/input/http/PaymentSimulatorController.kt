package com.demo.appfinanceiro.adapter.input.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payment-simulator")
class PaymentSimulatorController {

    @PostMapping("/transactions")
    fun simulate(@RequestBody request: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        val paymentMethod = request["paymentMethod"] as? String ?: "UNKNOWN"
        val paymentId = request["paymentId"] as? String ?: "SIMULATED"

        val response = mapOf(
            "paymentId" to paymentId,
            "status" to "APPROVED",
            "providerTransactionId" to "SIM-$paymentId",
            "paymentMethod" to paymentMethod
        )

        return ResponseEntity.ok(response)
    }
}

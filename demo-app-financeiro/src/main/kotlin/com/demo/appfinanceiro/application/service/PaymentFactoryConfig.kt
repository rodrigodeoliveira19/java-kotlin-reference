//package com.demo.appfinanceiro.application.service
//
//import com.demo.appfinanceiro.adapter.output.gateway.payment.PaymentGatewayAdapter
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//
//@Configuration
//class PaymentFactoryConfig(
//    private val paymentGatewayAdapter: PaymentGatewayAdapter
//) {
//
//    @Bean
//    fun paymentProcessorFactory(): PaymentProcessorFactory {
//        val processors = listOf(
//            CreditCardPaymentProcessor(paymentGatewayAdapter),
//            DebitCardPaymentProcessor(paymentGatewayAdapter),
//            PixPaymentProcessor(paymentGatewayAdapter)
//        )
//        return PaymentProcessorFactory(processors)
//    }
//}

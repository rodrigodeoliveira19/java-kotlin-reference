package org.example.solid.lsp

class CreditCardPayment : PaymentMethod {
    override fun pay(amount: Double) {
        require(amount > 0)
        println("Pagamento de $amount realizado")
    }
}
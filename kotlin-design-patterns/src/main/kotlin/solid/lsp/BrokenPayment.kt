package org.example.solid.lsp

/**
 * Esta classe viola o princípio de substituição de Liskov, pois não é possível substituir a classe pai (PaymentMethod) pela classe filha (BrokenPayment)
 * sem quebrar o código. A classe BrokenPayment lança uma exceção quando o método pay é chamado, o que não é esperado pelo contrato da interface PaymentMethod.

 Ela implementa a interface, mas não cumpre a expectativa essencial de quem a utiliza. A substituição quebra o comportamento esperado.

 */
class BrokenPayment : PaymentMethod {
    override fun pay(amount: Double) {
        throw UnsupportedOperationException(
            "Não consigo pagar"
        )
    }
}
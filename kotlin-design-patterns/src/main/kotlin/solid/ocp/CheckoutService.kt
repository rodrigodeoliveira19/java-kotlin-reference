package org.example.solid.ocp

class CheckoutService(
    private val discountPolicy: DiscountPolicy
) {
    fun finalPrice(total: Double): Double {
        return total - discountPolicy.calculate(total)
    }
}


/*
    OCP - Open/Closed Principle
    O princípio do aberto/fechado afirma que as classes devem ser abertas para extensão, mas fechadas para modificação.
    Isso significa que você deve ser capaz de adicionar novos comportamentos a uma classe sem alterar seu código-fonte existente.

    No exemplo acima, a classe CheckoutService é aberta para extensão, pois podemos passar diferentes implementações de DiscountPolicy (como RegularDiscount ou VipDiscount) para calcular o desconto. No entanto, a classe em si não precisa ser modificada para adicionar novos tipos de desconto, mantendo-a fechada para modificação.

    val checkout = CheckoutService(PremiumDiscount())
    println(checkout.finalPrice(200.0)) // 170.0

 */
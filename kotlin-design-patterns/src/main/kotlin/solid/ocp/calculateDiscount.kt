package org.example.solid.ocp


/*
 * Hoje existem dois tipos de clientes. Amanhã chega o cliente PREMIUM. Depois, um desconto especial de aniversário.
 *
 * A cada novidade, precisamos editar o mesmo when. Conforme as regras crescem, essa função pode virar um ponto central de mudanças e regressões.
 *
 * A solução: variar o comportamento por uma abstração
 * Macete para a memória
  Se cada nova funcionalidade exige adicionar mais um if, when ou switch a uma classe central, considere usar polimorfismo ou uma estratégia intercambiável.
 */
class calculateDiscount {

    fun calculateDiscount(
        customerType: String,
        total: Double
    ): Double {
        return when (customerType) {
            "REGULAR" -> total * 0.05
            "VIP" -> total * 0.10
            else -> 0.0
        }
    }
}
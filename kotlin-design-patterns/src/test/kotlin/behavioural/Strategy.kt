package behavioural

import org.junit.jupiter.api.Test
import java.util.Locale.getDefault

/*
Quando usar Strategy

O padrão é indicado quando:

Existem várias formas de realizar uma mesma tarefa.
Você quer evitar grandes blocos de if/else ou when.
Deseja trocar o algoritmo em tempo de execução.
Novos comportamentos devem ser adicionados sem alterar a classe principal.

Exemplos comuns incluem cálculo de frete, algoritmos de ordenação, formas de pagamento, políticas de desconto, estratégias de autenticação e formatação de dados.
* */

// Exemplo 1 usando função (String) -> String
class Printer(private val stringFormatterStrategy: (String) -> String) {
    fun printString(message: String) {
        println(stringFormatterStrategy(message))
    }
}

val lowercaseFormatter = {it: String -> it.lowercase(getDefault()) }
val uppercaseFormatter = {it: String -> it.uppercase(getDefault()) }


// Ex 2 com implementação.
interface FreteStrategy {
    fun calcular(valor: Double): Double
}

class Correios : FreteStrategy {

    override fun calcular(valor: Double) =
        valor * 0.1
}


class Sedex : FreteStrategy {

    override fun calcular(valor: Double) =
        valor * 0.2
}

class FreteGratis : FreteStrategy {

    override fun calcular(valor: Double) = 0.0
}

class CalculadoraFrete(
    private val strategy: FreteStrategy
) {

    fun calcular(valor: Double) =
        strategy.calcular(valor)
}


class StrategyTest {
    @Test
    fun testStrategy() {
        val inputString = "LOREM ipsum DOLOR sit amet"

        val lowercasePrinter = Printer(lowercaseFormatter)
        lowercasePrinter.printString(inputString)

        val uppercasePrinter = Printer(uppercaseFormatter)
        uppercasePrinter.printString(inputString)
    }
}

class StrategyTest2 {
    @Test
    fun testStrategy() {
        val calculadora = CalculadoraFrete(Sedex())

        println(calculadora.calcular(100.0))
    }
}
package structural

import org.junit.jupiter.api.Test


// Ex 1
interface CoffeeMachine {
    fun makeSmallCoffee()
    fun makeLargeCoffee()
}

class NormalCoffeeMachine: CoffeeMachine {
    override fun makeSmallCoffee() {
        println("Normal coffee machine: Making small coffee")
    }

    override fun makeLargeCoffee() {
        println("Normal coffee machine: Making large coffee")
    }
}

// Decorator
class EnhancedCoffeeMachine(private val coffeeMachine: CoffeeMachine): CoffeeMachine by coffeeMachine {
    // Overriding behaviour
    override fun makeLargeCoffee() {
        println("Enhanced coffee machine: Making large coffee")
    }

    // Extending behaviour
    fun makeMilkCoffee() {
        println("Enhanced coffee machine: Making milk coffee")
        coffeeMachine.makeSmallCoffee()
        println("Enhanced coffee machine: Adding milk")
    }
}

// Ex 2
interface Coffee {
    fun cost(): Double
    fun description(): String
}

class SimpleCoffee : Coffee {
    override fun cost() = 5.0

    override fun description() = "Café"
}

abstract class CoffeeDecorator(
    protected val coffee: Coffee
) : Coffee

class MilkDecorator(
    coffee: Coffee
) : CoffeeDecorator(coffee) {

    override fun cost() =
        coffee.cost() + 2.0

    override fun description() =
        coffee.description() + ", leite"
}

class WhippedCreamDecorator(
    coffee: Coffee
) : CoffeeDecorator(coffee) {

    override fun cost() =
        coffee.cost() + 3.5

    override fun description() =
        coffee.description() + ", chantilly"
}

class DecoratorTest {
    @Test
    fun testDecorator() {
        val normalMachine = NormalCoffeeMachine()
        val enhancedMachine = EnhancedCoffeeMachine(normalMachine)

        enhancedMachine.makeSmallCoffee()
        println("------------------")
        enhancedMachine.makeLargeCoffee()
        println("------------------")
        enhancedMachine.makeMilkCoffee()
    }
}

class DecoratorTest2 {
    @Test
    fun testDecorator() {
        val coffee = SimpleCoffee()

        println(coffee.description())
        println(coffee.cost())

        val withMilk = MilkDecorator(coffee)

        println(withMilk.description())
        println(withMilk.cost())

        val deluxe =
            WhippedCreamDecorator(
                MilkDecorator(
                    SimpleCoffee()
                )
            )

        println(deluxe.description())
        println(deluxe.cost())
    }
}
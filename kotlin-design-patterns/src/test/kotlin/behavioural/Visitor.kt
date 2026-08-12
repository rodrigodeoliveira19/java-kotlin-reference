package behavioural

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/*Permite adicionar ou remover novas informações a um objeto sem alterar o objeto.
 As novas operações são os visitantes.
 Os objetos dizem "eu aceito visitantes". Os visitantes sabem exatamente o que fazer para cada tipo de objeto.
* */

// Ex 1
interface ReportElement {
    fun <R> accept(visitor: ReportVisitor<R>): R
}

class FixedPriceContract(val costPerYear: Long) : ReportElement {
    override fun <R> accept(visitor: ReportVisitor<R>): R = visitor.visit(this)
}

class TimeAndMaterialsContract(val costPerHour: Long, val hours: Long) : ReportElement {
    override fun <R> accept(visitor: ReportVisitor<R>): R = visitor.visit(this)
}

class SupportContract(val costPerMonth: Long) : ReportElement {
    override fun <R> accept(visitor: ReportVisitor<R>): R = visitor.visit(this)
}

interface ReportVisitor<out R> {
    fun visit(contract: FixedPriceContract): R
    fun visit(contract: TimeAndMaterialsContract): R
    fun visit(contract: SupportContract): R
}

class MonthlyCostReportVisitor : ReportVisitor<Long> {
    override fun visit(contract: FixedPriceContract): Long = contract.costPerYear / 12

    override fun visit(contract: TimeAndMaterialsContract): Long = contract.costPerHour * contract.hours

    override fun visit(contract: SupportContract): Long = contract.costPerMonth
}

class YearlyCostReportVisitor : ReportVisitor<Long> {
    override fun visit(contract: FixedPriceContract): Long = contract.costPerYear

    override fun visit(contract: TimeAndMaterialsContract): Long = contract.costPerHour * contract.hours

    override fun visit(contract: SupportContract): Long = contract.costPerMonth * 12
}


// Ex 2

interface Animal {
    fun accept(visitor: AnimalVisitor)
}

class Leao : Animal {
    override fun accept(visitor: AnimalVisitor) {
        visitor.visit(this)
    }
}

class Macaco : Animal {
    override fun accept(visitor: AnimalVisitor) {
        visitor.visit(this)
    }
}

interface AnimalVisitor {
    fun visit(leao: Leao)
    fun visit(macaco: Macaco)
}

//Visitantes
class Alimentador : AnimalVisitor {

    override fun visit(leao: Leao) {
        println("Dando carne ao leão")
    }

    override fun visit(macaco: Macaco) {
        println("Dando banana ao macaco")
    }
}

class Veterinario : AnimalVisitor {

    override fun visit(leao: Leao) {
        println("Examinando o leão")
    }

    override fun visit(macaco: Macaco) {
        println("Examinando o macaco")
    }
}

class VisitorTest {
    @Test
    fun testVisitor() {
        val projectAlpha = FixedPriceContract(10_000)
        val projectBeta = SupportContract(500)
        val projectGamma = TimeAndMaterialsContract(150, 10)
        val projectKappa = TimeAndMaterialsContract(50, 50)

        val project = arrayListOf(projectAlpha, projectBeta, projectGamma, projectKappa)

        val monthlyCostVisitor = MonthlyCostReportVisitor()
        val monthlyCost = project.map { it.accept(monthlyCostVisitor) }.sum()
        println("Monthly cost: $monthlyCost")
        Assertions.assertThat(monthlyCost).isEqualTo(5333)

        val yearlyCostVisitor = YearlyCostReportVisitor()
        val yearlyCost = project.map { it.accept(yearlyCostVisitor) }.sum()
        println("Yearly cost: $yearlyCost")
        Assertions.assertThat(yearlyCost).isEqualTo(20_000)
    }
}


class VisitorTest2 {
    @Test
    fun testVisitor() {
        val animais = listOf(
            Leao(),
            Macaco()
        )

        val alimentador = Alimentador()
        val veterinario = Veterinario()

        animais.forEach {
            it.accept(alimentador)
        }

        animais.forEach {
            it.accept(veterinario)
        }
    }
}
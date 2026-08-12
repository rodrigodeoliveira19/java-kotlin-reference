package structural

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/*
O Composite é um padrão de projeto estrutural que permite tratar objetos individuais e grupos de objetos da mesma forma.
A ideia central é que um objeto "composto" contenha outros objetos que implementam a mesma interface.

* */

// Exemplo 1
open class Equipment(
    open val price: Int,
    val name: String
)

open class Composite(name: String): Equipment(0, name) {
    private val equipments = ArrayList<Equipment>()

    override val price: Int
        get() = equipments.map { it.price }.sum()

    fun add(equipment: Equipment) = apply { equipments.add(equipment) }
}

class Computer: Composite("PC")
class Processor: Equipment(1000, "Processor")
class HardDrive: Equipment(250, "Hard Drive")
class Memory: Composite("Memory")
class ROM: Equipment(100, "Read Only Memory")
class RAM: Equipment(75, "Random Access Memory")


// Exemplo 2
interface Graphic {
    fun draw()
}

class Circle : Graphic {
    override fun draw() {
        println("Círculo")
    }
}

class Group : Graphic {

    private val graphics = mutableListOf<Graphic>()

    fun add(graphic: Graphic) {
        graphics += graphic
    }

    override fun draw() {
        graphics.forEach { it.draw() }
    }
}


class CompositeTest {
    @Test
    fun testComposite() {
        val memory = Memory()
            .add(ROM())
            .add(RAM())
        val pc = Computer()
            .add(memory)
            .add(Processor())
            .add(HardDrive())
        println("PC price: ${pc.price}")

        Assertions.assertThat(pc.name).isEqualTo("PC")
        Assertions.assertThat(pc.price).isEqualTo(1425)
    }
}


class CompositeTest2 {
    @Test
    fun testComposite() {
        val group = Group()
        group.add(Circle())
        group.add(Circle())
        group.draw()

        val root = Group()
        root.add(group)
        root.add(Circle())
        root.draw()
    }
}
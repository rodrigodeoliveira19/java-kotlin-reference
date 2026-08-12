package creational

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test


/*
Resumindo
✅ Objetos de valor (Value Objects): use data class e copy().
✅ Entidades de domínio (DDD): geralmente use class comum e implemente um método explícito de cópia quando necessário.
❌ Evite Cloneable; ele é uma herança do Java e não é considerado idiomático em Kotlin.
* */

// Indicação para Kotlin, usar o copy
data class PessoaData(val nome: String, val idade: Int)

fun main() {
    val original = PessoaData("Ana", 28)
    // Clonando o objeto inteiro
    val copia = original.copy()
    println("copia 1: $copia")

    // Clonando e alterando a idade
    val copiaMaisVelha = original.copy(idade = 29)
    println("copia 2: $copiaMaisVelha")
}

// Curso
abstract class Shape: Cloneable {
    var id: String? = null
    var type: String? = null

    abstract fun draw()

    public override fun clone(): Any {
        var clone: Any? = null
        try {
            clone = super.clone()
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }
        return clone!!
    }
}

class Teste(
    val id: String
): Cloneable{
    public override fun clone(): Any {
        val clone = super.clone()
        return clone as Teste
    }
}


class Rectangle: Shape() {
    override fun draw() {
        println("Inside Rectangle::draw() method.")
    }

    init {
        type = "Rectangle"
    }
}

class Square: Shape() {
    override fun draw() {
        println("Inside Square::draw() method.")
    }

    init {
        type = "Square"
    }
}

class Circle: Shape() {
    override fun draw() {
        println("Inside Circle::draw() method.")
    }

    init {
        type = "Circle"
    }
}

object ShapeCache {
    private val shapeMap = hashMapOf<String?, Shape>()

    fun loadCache() {
        val circle = Circle()
        val square = Square()
        val rectangle = Rectangle()

        shapeMap.put("1", circle)
        shapeMap.put("2", square)
        shapeMap.put("3", rectangle)
    }

    fun getShape(shapeId: String): Shape {
        val cachedShape = shapeMap.get(shapeId)
        return cachedShape?.clone() as Shape
    }
}

class PrototypeTest {
    @Test
    fun testPrototype() {

        val teste = Teste("Rodrigo")
        val t1 = teste.clone()

        /*ShapeCache.loadCache()
        val clonedShape1 = ShapeCache.getShape("1")
        val clonedShape2 = ShapeCache.getShape("2")
        val clonedShape3 = ShapeCache.getShape("3")

        clonedShape1.draw()
        clonedShape2.draw()
        clonedShape3.draw()

        Assertions.assertThat(clonedShape1.type).isEqualTo("Circle")
        Assertions.assertThat(clonedShape2.type).isEqualTo("Square")
        Assertions.assertThat(clonedShape3.type).isEqualTo("Rectangle")*/
    }
}
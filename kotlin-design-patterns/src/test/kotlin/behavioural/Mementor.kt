package behavioural

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

// EX 1
data class Memento(val state: String)

class Originator(var state: String) {
    fun createMemento() = Memento(state)
    fun restoreMemento(memento: Memento) {
        state = memento.state
    }
}

class CareTaker {
    private val mementoList = arrayListOf<Memento>()

    fun saveState(state: Memento) {
        mementoList.add(state)
    }

    fun restore(index: Int): Memento = mementoList[index]
}


// Ex 2

data class TextMemento(val text: String)

class TextEditor {

    var text = ""

    fun save(): TextMemento =
        TextMemento(text)

    fun restore(memento: TextMemento) {
        text = memento.text
    }
}


class MementoTest {
    @Test
    fun testMemento() {
        val originator = Originator("initial state")
        val careTaker = CareTaker()
        careTaker.saveState(originator.createMemento())
        println("Current state is ${originator.state}")

        originator.state = "State 1"
        careTaker.saveState(originator.createMemento())
        println("Current state is ${originator.state}")

        originator.state = "State 2"
        careTaker.saveState(originator.createMemento())
        println("Current state is ${originator.state}")

        Assertions.assertThat(originator.state).isEqualTo("State 2")

        originator.restoreMemento(careTaker.restore(1))
        println("Current state is ${originator.state}")
        Assertions.assertThat(originator.state).isEqualTo("State 1")

        originator.restoreMemento(careTaker.restore(0))
        println("Current state is ${originator.state}")
        Assertions.assertThat(originator.state).isEqualTo("initial state")

        originator.restoreMemento(careTaker.restore(2))
        println("Current state is ${originator.state}")
        Assertions.assertThat(originator.state).isEqualTo("State 2")
    }
}


class MementoTest2 {
    @Test
    fun testMemento() {
        val editor = TextEditor()

        editor.text = "Olá"
        val m1 = editor.save()

        editor.text = "Olá Mundo"
        val m2 = editor.save()

        editor.text = "Olá Mundo!!!"

        editor.restore(m2)
        println(editor.text) // Olá Mundo

        editor.restore(m1)
        println(editor.text) // Olá
    }
}
package behavioural

import org.junit.jupiter.api.Test
/*
O Command é um padrão de projeto comportamental que encapsula uma solicitação (ou ação) em um objeto. Em vez de um objeto chamar diretamente um método de outro, ele cria um comando que representa essa operação.
Isso permite tratar ações como objetos: armazená-las, enfileirá-las, desfazê-las (undo), registrá-las em log ou executá-las posteriormente.
* */

// Ex 1
interface Command {
    fun execute()
}

class OrderAddCommand(val id: Long) : Command {
    override fun execute() {
        println("Adding order with id $id")
    }
}

class OrderPayCommand(val id: Long) : Command {
    override fun execute() {
        println("Paying for order with id $id")
    }
}

class CommandProcessor {
    private val queue = arrayListOf<Command>()

    fun addToQueue(command: Command): CommandProcessor = apply { queue.add(command) }

    fun processCommands(): CommandProcessor = apply {
        queue.forEach { it.execute() }
        queue.clear()
    }
}

// Ex 2
class Lampada {

    fun ligar() {
        println("Lâmpada ligada")
    }
}

class LigarLampadaCommand(
    private val lampada: Lampada
) : Command {

    override fun execute() {
        lampada.ligar()
    }
}

class Botao(
    private val command: Command
) {

    fun pressionar() {
        command.execute()
    }
}

class CommandTest {
    @Test
    fun testCommand() {
        CommandProcessor()
            .addToQueue(OrderAddCommand(1L))
            .addToQueue(OrderAddCommand(2L))
            .addToQueue(OrderPayCommand(2L))
            .addToQueue(OrderPayCommand(1L))
            .processCommands()
    }
}

class CommandTest2 {
    @Test
    fun testCommand() {
        val lampada = Lampada()

        val ligar = LigarLampadaCommand(lampada)

        val botao = Botao(ligar)

        botao.pressionar()
    }
}
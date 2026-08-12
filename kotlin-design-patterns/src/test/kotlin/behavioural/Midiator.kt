package behavioural

import org.junit.jupiter.api.Test
/*é reduzir o acoplamento entre objetos, centralizando a comunicação entre eles em uma única classe chamada Mediator.
  Em vez de um objeto conhecer e chamar diretamente outro, todos conversam com o mediador.
* */

// Ex 1
class ChatUser(private val mediator: Mediator, private val name: String) {
    fun send(msg: String) {
        println("$name: Sending message: $msg")
        mediator.sendMessage(msg, this)
    }

    fun receive(msg: String) {
        println("$name: Received message: $msg")
    }
}

class Mediator {
    private val users = arrayListOf<ChatUser>()

    fun sendMessage(msg: String, user: ChatUser) {
        users
            .filter { it != user }
            .forEach { it.receive(msg) }
    }

    fun addUser(user: ChatUser): Mediator = apply { users.add(user) }
}


// Ex 2

interface ChatMediator {
    fun send(message: String, sender: User)
}

class ChatRoom : ChatMediator {

    private val users = mutableListOf<User>()

    fun addUser(user: User) {
        users.add(user)
    }

    override fun send(message: String, sender: User) {
        users
            .filter { it != sender }
            .forEach { it.receive(message, sender.name) }
    }
}

class User(
    val name: String,
    private val mediator: ChatMediator
) {

    fun send(message: String) {
        println("$name enviou: $message")
        mediator.send(message, this)
    }

    fun receive(message: String, from: String) {
        println("$name recebeu de $from: $message")
    }
}


class MediatorTest {
    @Test
    fun testMediator() {
        val mediator = Mediator()
        val alice = ChatUser(mediator, "Alice")
        val bob = ChatUser(mediator, "Bob")
        val carol = ChatUser(mediator, "Carol")

        mediator.addUser(alice)
            .addUser(bob)
            .addUser(carol)

        carol.send("Hi everyone!")
    }
}

class MediatorTest2 {
    @Test
    fun testMediator() {
        val chat = ChatRoom()

        val joao = User("João", chat)
        val maria = User("Maria", chat)
        val ana = User("Ana", chat)

        chat.addUser(joao)
        chat.addUser(maria)
        chat.addUser(ana)

        joao.send("Olá pessoal!")
    }
}
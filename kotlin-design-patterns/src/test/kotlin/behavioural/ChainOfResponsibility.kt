package behavioural

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/*
é um padrão de projeto comportamental que permite que uma solicitação seja passada por uma sequência de objetos (os handlers) até que um deles consiga tratá-la.
A ideia principal é desacoplar quem faz a requisição de quem a processa.
* */

// EX 1
interface HandlerChain {
    fun addHeader(inputHeader: String): String
}

class AuthenticationHeader(val token: String?, var next: HandlerChain? = null): HandlerChain {
    override fun addHeader(inputHeader: String) =
        "$inputHeader\nAuthorization: $token"
            .let { next?.addHeader(it) ?: it }
}

class ContentTypeHeader(val contentType: String, var next: HandlerChain? = null): HandlerChain {
    override fun addHeader(inputHeader: String) =
        "$inputHeader\nContentType: $contentType"
            .let { next?.addHeader(it) ?: it }
}

class BodyPayloadHeader(val body: String, var next: HandlerChain? = null): HandlerChain {
    override fun addHeader(inputHeader: String) =
        "$inputHeader\n$body"
            .let { next?.addHeader(it) ?: it }
}


//Ex 2
abstract class Handler {

    private var next: Handler? = null

    fun setNext(handler: Handler): Handler {
        next = handler
        return handler
    }

    protected fun next(value: Int) {
        next?.handle(value)
    }

    abstract fun handle(value: Int)
}

class SmallHandler : Handler() {

    override fun handle(value: Int) {
        if (value <= 10) {
            println("SmallHandler processou.")
        } else {
            next(value)
        }
    }
}

class MediumHandler : Handler() {

    override fun handle(value: Int) {
        if (value <= 100) {
            println("MediumHandler processou.")
        } else {
            next(value)
        }
    }
}

class LargeHandler : Handler() {

    override fun handle(value: Int) {
        println("LargeHandler processou.")
    }

}


class ChainOfResponsibilityTest {
    @Test
    fun testChainOfResponsibility() {
        val authenticationHeader = AuthenticationHeader("123456")
        val contentTypeHeader = ContentTypeHeader("json")
        val bodyPayloadHeader = BodyPayloadHeader("Body: {\"username\" = \"john\"}")

        authenticationHeader.next = contentTypeHeader
        contentTypeHeader.next = bodyPayloadHeader

        val messageWithAuthentication = authenticationHeader.addHeader("Headers with authentication")
        println(messageWithAuthentication)

        println("-------------------------")

        val messageWithoutAuthentication = contentTypeHeader.addHeader("Headers without authentication")
        println(messageWithoutAuthentication)

        Assertions.assertThat(messageWithAuthentication).isEqualTo(
            """
                    Headers with authentication
                    Authorization: 123456
                    ContentType: json
                    Body: {"username" = "john"}
                """.trimIndent()
        )

        Assertions.assertThat(messageWithoutAuthentication).isEqualTo(
            """
                    Headers without authentication
                    ContentType: json
                    Body: {"username" = "john"}
                """.trimIndent()
        )
    }
}



class ChainOfResponsibilityTest2 {

    @Test
    fun testChainOfResponsibility() {
        val small = SmallHandler()
        val medium = MediumHandler()
        val large = LargeHandler()

        small.setNext(medium)
            .setNext(large)

        small.handle(5)
        small.handle(50)
        small.handle(500)
    }
}
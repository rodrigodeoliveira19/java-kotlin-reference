package creational

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

object NetworkDriver {
    init {
        println("NetworkDriver $this")
    }

    fun log() : NetworkDriver = apply { println("NetworkDriver $this") }
}



class SigletonTest(){

    @Test
    fun testSigleton() {
        println("Start")
        val networkDriver1 = NetworkDriver.log()
        val networkDriver2 = NetworkDriver.log()

        Assertions.assertThat(networkDriver1).isSameAs(networkDriver2)
        Assertions.assertThat(networkDriver1).isSameAs(NetworkDriver)
    }
}
package structural

import org.junit.jupiter.api.Test

/*
* O Proxy é um padrão de projeto estrutural que fornece um objeto substituto (proxy) para controlar o acesso a outro objeto (o objeto real).
Em vez de o cliente conversar diretamente com o objeto real, ele conversa com o proxy.

* */
interface Image {
    fun display()
}

class RealImage(private val filename: String): Image {
    override fun display() {
        println("RealImage: Displaying $filename")
    }

    private fun loadFromDisk(filename: String) {
        println("RealImage: Loading $filename")
    }

    init {
        loadFromDisk(filename)
    }
}

class ProxyImage(private val filename: String): Image {
    private var realImage: RealImage? = null

    override fun display() {
        println("ProxyImage: Displaying $filename")
        if (realImage == null) {
            realImage = RealImage(filename)
        }
        realImage!!.display()
    }
}

class ProxyTest {
    @Test
    fun testProxy() {
        val image = ProxyImage("test.jpg")

        // load image from disk
        image.display()
        println("-------------------")

        //load image from "cache"
        image.display()
    }
}
package org.example.solid.isp

/**
 * Uma impressora simples que só pode imprimir.
 *
 * Percebe o problema? A impressora é obrigada a implementar funcionalidades que não oferece. As exceções são um sinal de que a interface talvez esteja grande demais.
 *
 * A solução: interfaces menores e específicas
 */
class SimplePrinter : Machine {

    override fun print(document: String) {
        println("Imprimindo: $document")
    }

    override fun scan(document: String) {
        throw UnsupportedOperationException()
    }

    override fun fax(document: String) {
        throw UnsupportedOperationException()
    }
}
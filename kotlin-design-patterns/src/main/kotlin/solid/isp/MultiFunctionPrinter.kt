package solid.isp

/**
 * Macete para a memória
 *
 * Se uma interface tem métodos que diversas implementações deixam vazios ou implementam lançando exceções, investigue se ela precisa ser dividida.
 */
class MultiFunctionPrinter :
    Printer, Scanner, Fax {

    override fun print(document: String) {
        println("Imprimindo: $document")
    }

    override fun scan(document: String) {
        println("Digitalizando: $document")
    }

    override fun fax(document: String) {
        println("Enviando fax: $document")
    }
}
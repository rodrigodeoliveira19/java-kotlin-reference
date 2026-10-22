package solid.isp

class SimplePrinter2 : Printer {
    override fun print(document: String) {
        println("Imprimindo: $document")
    }
}
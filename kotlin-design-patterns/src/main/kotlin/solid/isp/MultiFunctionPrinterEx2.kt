package solid.isp

/**
 * Agora, em outro lugar do código, você pode receber o dispositivo pela interface:
 *
 *
 * kotlin
 * class OfficeService(
 *     private val device: MultiFunctionDevice
 * ) {
 *     fun processDocument(document: String) {
 *         device.print(document)
 *         device.scan(document)
 *         device.fax(document)
 *     }
 * }
 */
class MultiFunctionPrinterEx2 : MultiFunctionDevice {

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